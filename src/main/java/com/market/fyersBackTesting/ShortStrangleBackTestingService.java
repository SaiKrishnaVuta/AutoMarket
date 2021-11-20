package com.market.fyersBackTesting;

import com.market.entity.HistoricData;
import com.market.entity.HistoricKey;
import com.market.entity.Orders;
import com.market.repository.HistoricDataRepository;
import com.market.repository.SSBTOrdersRepository;
import com.market.service.FyersAppService;
import com.market.shortStrangleModel.EntryOrderOutput;
import com.market.shortStrangleModel.EntrySet;
import com.market.shortStrangleModel.OrderDetails;
import com.market.shortStrangleModel.OrderOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class ShortStrangleBackTestingService {



    @Autowired
    FyersAppService fyersAppService;

    @Autowired
    HistoricDataRepository historicDataRepository;

    @Autowired
    SSBTOrdersRepository ordersRepository;

    protected List<String> symbolList;
    protected static Date currentDate;
    protected Double straddleSum=0.0;
    protected final int lotSize=25;
    protected Double originalLevel=0.0;
    protected final String index_instrument="NSE:NIFTYBANK-INDEX";


    //Fixed Method
    public EntryOrderOutput entry(Date entrydate, String currentSpotSymbol, Double desired, Integer lots){
        String currentSpotvalue=fyersAppService.toSymbolDto(currentSpotSymbol).getStrike();
        originalLevel=Double.valueOf(currentSpotvalue);
        EntryOrderOutput output =new EntryOrderOutput();
        //EntrySet entrySet= new EntrySet();
        this.symbolList=fyersAppService.generateSymbolList(currentSpotSymbol);
        List<HistoricData> closeCEStrikes= new ArrayList<>();
        List<HistoricData> closePEStrikes= new ArrayList<>();
        List<HistoricKey> entryKeyList= new ArrayList<>();
        List<Orders> ordersList = new ArrayList<>();
        Map<String, HistoricData> historicDataMap = new HashMap<>();
        for(String strike: symbolList){
            HistoricKey key = new HistoricKey();
            key.setSymbol(strike);
            key.setDatetime(entrydate);
            entryKeyList.add(key);
        }
        HistoricKey indexKey= new HistoricKey();
        indexKey.setSymbol(index_instrument);
        indexKey.setDatetime(currentDate);
        Optional<HistoricData> indexData=historicDataRepository.findById(indexKey);
        if(indexData.isPresent()) {
            HistoricData indexDataValues = indexData.get();
            output.setBankNifty_currentValue(indexDataValues.getClose());
        }

        List<HistoricData> entryList=historicDataRepository.findAllById(entryKeyList);
        for(HistoricData h:entryList){
            if(h.getSymbol().endsWith("CE"))
                closeCEStrikes.add(h);
            else
                closePEStrikes.add(h);
        }
        HistoricData ce=fyersAppService.isAround(closeCEStrikes, desired);
        HistoricData pe=fyersAppService.isAround(closePEStrikes, desired);
        for(HistoricData h:entryList){
            if(h.getClose().equals(ce.getClose()) && h.getSymbol().endsWith("CE")){
                Orders o = new Orders();
                o.setDatetime(h.getDatetime());
                o.setLots(lots);
                o.setSellprice(h.getClose());
                o.setStatus("Pending");
                o.setSymbol(h.getSymbol());
                //entrySet.setEntryCall(h);
                ordersList.add(o);
            }else if(h.getClose().equals(pe.getClose()) && h.getSymbol().endsWith("PE")){
                //entrySet.setEntryPut(h);
                Orders o = new Orders();
                o.setDatetime(h.getDatetime());
                o.setLots(lots);
                o.setSellprice(h.getClose());
                o.setStatus("Pending");
                o.setSymbol(h.getSymbol());
                ordersList.add(o);
            }

        }
        output.setOrdersList(ordersRepository.saveAll(ordersList));
        if(!(ordersList.isEmpty())){
            output.setDate(output.getOrdersList().get(0).getDatetime());
            currentDate=output.getOrdersList().get(0).getDatetime();
        }

        output.setProfit_or_Loss(0.0*lotSize);
        return output;
    }

    public OrderOutput afterNMins(int n){
        Double pl=0.0;
        String remarks="";
        OrderOutput output =new OrderOutput();
        List<OrderDetails> orderDetailsList = new ArrayList<>();
        try{
            List<Orders> ordersList=ordersRepository.findAll();
            List<HistoricKey> hKey= new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm") ;
            dateFormat.format(currentDate);

            //Getting Proper Date's data
            if(dateFormat.parse(dateFormat.format(currentDate)).after(dateFormat.parse("15:15"))){
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);
                cal.add(Calendar.DATE, 1);
                cal.set(Calendar.HOUR_OF_DAY, 9);
                cal.set(Calendar.MINUTE, 30);
                currentDate = cal.getTime();
                log.info("Current Date ::"+currentDate.toString());
                HistoricKey key = new HistoricKey();
                String s=ordersList.get(0).getSymbol();
                key.setSymbol(s);
                key.setDatetime(currentDate);
                Optional<HistoricData> h=historicDataRepository.findById(key);
                while(!(h.isPresent())) {
                    cal.setTime(currentDate);
                    cal.add(Calendar.DATE, 1);
                    cal.set(Calendar.HOUR_OF_DAY, 9);
                    cal.set(Calendar.MINUTE, 30);
                    currentDate = cal.getTime();
                    key.setSymbol(s);
                    log.info("Current Date ::"+currentDate.toString());
                    key.setDatetime(currentDate);
                    h=historicDataRepository.findById(key);
                }
            }else{
                long timeInSecs = currentDate.getTime();
                currentDate= new Date(timeInSecs + (n * 60 * 1000));
            }
            output.setDate(currentDate);

            List<HistoricKey> historicKeys= new ArrayList<>();
            if(!(ordersList.isEmpty())) {
                for (Orders o : ordersList) {
                    if(o.getStatus().equalsIgnoreCase("Pending")){
                        HistoricKey key = new HistoricKey();
                        key.setSymbol(o.getSymbol());
                        key.setDatetime(currentDate);
                        historicKeys.add(key);
                    }
                }
                HistoricKey indexKey= new HistoricKey();
                indexKey.setSymbol(index_instrument);
                indexKey.setDatetime(currentDate);
                Optional<HistoricData> indexData=historicDataRepository.findById(indexKey);
                if(indexData.isPresent()) {
                    HistoricData indexDataValues = indexData.get();
                    List<HistoricData> historicDataList = historicDataRepository.findAllById(historicKeys);
                    if (!(historicDataList.isEmpty())) {
                        HistoricData cP = null;
                        HistoricData cC = null;
                        for (HistoricData h : historicDataList) {
                            if (h.getSymbol().endsWith("CE")) {
                                cC = h;
                            } else {
                                cP = h;
                            }
                        }
                        //compare
                        Integer cCStrike = Integer.valueOf(fyersAppService.toSymbolDto(cC.getSymbol()).getStrike());
                        Integer cPStrike = Integer.valueOf(fyersAppService.toSymbolDto(cP.getSymbol()).getStrike());
                        if(dateFormat.parse(dateFormat.format(currentDate)).after(dateFormat.parse("15:15"))){
                            this.exit(currentDate);
                            remarks = "Exit Triggered :D";
                        }
                        else if (cP != null && cC != null && cPStrike == cCStrike) {
                            if (this.straddleSum == 0) {
                                this.straddleSum = cP.getClose() + cC.getClose();
                            } else {
                                if (straddleSum * 1.1 < cP.getClose() + cC.getClose()) {
                                    this.exit(currentDate);
                                    remarks = "Exit Triggered :D";
                                } else {
                                    this.straddleSum = cP.getClose() + cC.getClose();
                                }
                            }
                        } else if (cP != null && cC != null && cP.getClose() <= (cC.getClose() / 2)) {
//                            if(originalLevel!=0 && originalLevel<indexDataValues.getClose()){
//                                remarks = this.applyAdjustment(cP, cC, ordersList.get(0).getLots(), currentDate);
//                                ordersList = ordersRepository.findAll();
//                            }
//                            else if(originalLevel!=0 && originalLevel>indexDataValues.getClose()){
//                                remarks = this.applyAdjustment(cC, cP, ordersList.get(0).getLots(), currentDate);
//                                ordersList = ordersRepository.findAll();
//                            }
                            remarks = this.applyAdjustment(cP, cC, ordersList.get(0).getLots(), currentDate);
                                ordersList = ordersRepository.findAll();
                        } else if (cP != null && cC != null && cC.getClose() <= (cP.getClose() / 2)) {
//                            if(originalLevel!=0 && originalLevel<indexDataValues.getClose()){
//                                remarks = this.applyAdjustment(cP, cC, ordersList.get(0).getLots(), currentDate);
//                                ordersList = ordersRepository.findAll();
//                            }
//                            else if(originalLevel!=0 && originalLevel>indexDataValues.getClose()){
//                                remarks = this.applyAdjustment(cC, cP, ordersList.get(0).getLots(), currentDate);
//                                ordersList = ordersRepository.findAll();
//                            }
                            remarks = this.applyAdjustment(cC, cP, ordersList.get(0).getLots(), currentDate);
                                ordersList = ordersRepository.findAll();
                        }
                    }
                    output.setBankNifty_currentValue(indexDataValues.getClose());
                }
            }
            if(!(ordersList.isEmpty())){
                for(Orders o:ordersList){
                    OrderDetails orderDetails= new OrderDetails();
                    orderDetails.setInitialdatetime(o.getDatetime());
                    orderDetails.setCurrentdatetime(currentDate);
                    orderDetails.setSymbol(o.getSymbol());
                    orderDetails.setBuy_price(o.getBuyprice());
                    orderDetails.setSell_price(o.getSellprice());
                    orderDetails.setStatus(o.getStatus());
                    orderDetails.setNo_of_lots(o.getLots());
                    if(o.getStatus().equalsIgnoreCase("Pending")){
                        HistoricKey key= new HistoricKey();
                        key.setSymbol(o.getSymbol());
                        key.setDatetime(currentDate);
                        Optional<HistoricData> hData=historicDataRepository.findById(key);
                        if(hData.isPresent()){
                            HistoricData h= hData.get();
                            orderDetails.setCurrent_price(h.getClose());
                            orderDetails.setProfit_or_loss((o.getSellprice()-h.getClose())*lotSize*o.getLots());
                            pl+=o.getSellprice()-h.getClose();
                            if (pl >= 1000/lotSize) {
                                this.exit(currentDate);
                                remarks = "Exit Triggered :D";

                            }
                        }
                    }
                    else if(o.getStatus().equalsIgnoreCase("Complete")){
                        pl+=o.getSellprice()-o.getBuyprice();
                        orderDetails.setProfit_or_loss((o.getSellprice()-o.getBuyprice())*lotSize*o.getLots());
                    }
                    orderDetailsList.add(orderDetails);
                }
            }

            output.setSSOrdersList(orderDetailsList);
            output.setProfit_or_Loss(pl*lotSize);
            output.setRemarks(remarks);
        }catch (ParseException pe){
            log.error("Parse exception during observe call ::"+pe.getMessage());
        }
        return output;
    }


    public String applyAdjustment(HistoricData value, HistoricData competitor, Integer lots, Date currentDate){
        String remarks="";
        Double desiredValue=80*(competitor.getClose())/100;
        String competitorSymbol= competitor.getSymbol();
        String optionType=fyersAppService.toSymbolDto(value.getSymbol()).getOptionType();
        List<String> SymbolList=fyersAppService.generateSymbolList(value.getSymbol());
        List<HistoricKey> historicKeys= new ArrayList<>();
        for(String symbol: symbolList){
            if(symbol.endsWith(optionType)){
                HistoricKey key= new HistoricKey();
                key.setSymbol(symbol);
                key.setDatetime(value.getDatetime());
                historicKeys.add(key);
            }
        }
        List<HistoricData> historicDataList=historicDataRepository.findAllById(historicKeys);
        if(!(historicDataList.isEmpty())){
            HistoricData newValue=fyersAppService.isAround(historicDataList,desiredValue);
            Integer newValueStrike=Integer.valueOf(fyersAppService.toSymbolDto(newValue.getSymbol()).getStrike());
            Integer competingStrike=Integer.valueOf(fyersAppService.toSymbolDto(competitor.getSymbol()).getStrike());
            if(newValueStrike>competingStrike && competitorSymbol.endsWith("CE")){
                this.exit(currentDate);
                remarks="Exit Triggered :D";
            }
            else if(newValueStrike<competingStrike && competitorSymbol.endsWith("PE")){
                this.exit(currentDate);
                remarks="Exit Triggered :D";
            }
            else{
                Optional<Orders> pendingOrder= ordersRepository.findById(value.getSymbol());
                if(pendingOrder.isPresent()){
                    Orders pOrder=pendingOrder.get();
                    pOrder.setStatus("Complete");
                    pOrder.setBuyprice(value.getClose());
                    ordersRepository.save(pOrder);
                }
                Orders newOrder= new Orders();
                newOrder.setSymbol(newValue.getSymbol());
                newOrder.setDatetime(newValue.getDatetime());
                newOrder.setSellprice(newValue.getClose());
                newOrder.setLots(lots);
                newOrder.setStatus("Pending");
                ordersRepository.save(newOrder);
                remarks="Adjustment Triggered :|";
            }
        }
        return remarks;
    }

    private void exit(Date cTime){
        List<Orders> allOrders= ordersRepository.findAll();
        for(Orders o:allOrders){
            HistoricKey key=new HistoricKey();
            key.setDatetime(cTime);
            key.setSymbol(o.getSymbol());
            Optional<HistoricData> h=historicDataRepository.findById(key);
            if(o.getStatus().equalsIgnoreCase("Pending")){
                Optional<Orders> pendingOrder= ordersRepository.findById(o.getSymbol());
                if(pendingOrder.isPresent()){
                    Orders pOrder=pendingOrder.get();
                    pOrder.setStatus("Complete");
                    if(h.isPresent()){
                        pOrder.setBuyprice(h.get().getClose());
                    }
                    ordersRepository.save(pOrder);
                }
            }
        }
    }
}
