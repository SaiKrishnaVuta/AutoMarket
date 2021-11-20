package com.market.fyersBackTesting;

import com.market.backTesting.model.OBBTEntryOrderOuput;
import com.market.backTesting.model.OBBTOrderDetails;
import com.market.entity.HistoricData;
import com.market.entity.HistoricKey;
import com.market.entity.Orders;
import com.market.repository.HistoricDataRepository;
import com.market.repository.SSBTOrdersRepository;
import com.market.service.FyersAppService;
import com.market.shortStrangleModel.EntryOrderOutput;
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
public class OptionBuyingStrategyService {
    @Autowired
    FyersAppService fyersAppService;

    @Autowired
    HistoricDataRepository historicDataRepository;

    @Autowired
    SSBTOrdersRepository ordersRepository;

    protected static Date currentDate;
    protected final int lotSize=25;
    protected final String index_instrument="NSE:NIFTYBANK-INDEX";
    protected static Double put_previousPrice=0.0;
    protected static Double call_previousPrice=0.0;
    protected static Double stopLoss_percent=0.0;
    protected static Double target_percent=0.0;


    public EntryOrderOutput entrySetup(Date currentDateTime, Integer lots){
        EntryOrderOutput output =new EntryOrderOutput();
        List<HistoricData> allOrdersUnderRadar= new ArrayList<>();
        List<Orders> ordersList = new ArrayList<>();
        log.info("Current Date ::"+currentDateTime);
        String spotSymbol="";
        currentDate=currentDateTime;
        HistoricKey indexKey= new HistoricKey();
        indexKey.setSymbol(index_instrument);
        indexKey.setDatetime(currentDate);
        Optional<HistoricData> indexData=historicDataRepository.findById(indexKey);
        if(indexData.isPresent()) {
            HistoricData indexDataValues = indexData.get();
            output.setBankNifty_currentValue(indexDataValues.getClose());
            Long spotRounded=fyersAppService.isAround(indexDataValues.getClose());
            log.info("Rounded Spot ::"+spotRounded);
            List<HistoricData> callData=historicDataRepository.getHistoricDataEndsWith(spotRounded+"CE", currentDate);
            List<HistoricData> putData=historicDataRepository.getHistoricDataEndsWith(spotRounded+"PE", currentDate);
            if(!(callData.isEmpty()) && !(putData.isEmpty())){
                allOrdersUnderRadar.addAll(putData);
                allOrdersUnderRadar.addAll(callData);
            }
            for(HistoricData h:allOrdersUnderRadar){
                Orders o = new Orders();
                o.setDatetime(h.getDatetime());
                o.setLots(lots);
                o.setCurrentprice(h.getClose());
                o.setStatus("Awaiting Entry");
                o.setSymbol(h.getSymbol());
                ordersList.add(o);
                if(h.getSymbol().endsWith("CE")){
                    call_previousPrice=h.getClose();
                }
                else{
                    put_previousPrice=h.getClose();
                }
            }
        }
        output.setOrdersList(ordersRepository.saveAll(ordersList));
        if(!(ordersList.isEmpty())){
            output.setDate(output.getOrdersList().get(0).getDatetime());
            currentDate=output.getOrdersList().get(0).getDatetime();
        }
        return output;

    }
    public OBBTEntryOrderOuput observeNEnter(int n){
        Double pl=0.0;
        String remarks="";
        Double cCPercentage=0.0;
        Double cPPercentage=0.0;
        OBBTEntryOrderOuput output =new OBBTEntryOrderOuput();
        try {
            List<Orders> ordersList = ordersRepository.findAll();
            List<HistoricKey> hKey = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            dateFormat.format(currentDate);

            //Getting Proper Date's data
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            if (dateFormat.parse(dateFormat.format(currentDate)).after(dateFormat.parse("14:45"))) {
                output.setRemarks("TimeOut");
            } else {
                long timeInSecs = currentDate.getTime();
                currentDate = new Date(timeInSecs + (n * 60 * 1000));
            }
            output.setDate(currentDate);
            log.info("Day of the week :: "+cal.get(cal.DAY_OF_WEEK));
            log.info("Day of the week :: "+cal.getTime());
            List<HistoricKey> historicKeys= new ArrayList<>();
            if(!(ordersList.isEmpty())) {
                for (Orders o : ordersList) {
                    HistoricKey key = new HistoricKey();
                    key.setSymbol(o.getSymbol());
                    key.setDatetime(currentDate);
                    historicKeys.add(key);
                }
                HistoricKey indexKey = new HistoricKey();
                indexKey.setSymbol(index_instrument);
                indexKey.setDatetime(currentDate);
                Optional<HistoricData> indexData = historicDataRepository.findById(indexKey);
                if (indexData.isPresent()) {
                    HistoricData indexDataValues = indexData.get();
                    output.setBankNifty_currentValue(indexDataValues.getClose());
                    List<HistoricData> historicDataList = historicDataRepository.findAllById(historicKeys);
                    if (!(historicDataList.isEmpty())) {
                        HistoricData cP = null;
                        HistoricData cC = null;
                        for (HistoricData h : historicDataList) {
                            if (h.getSymbol().endsWith("CE")) {
                                cC = h;
                                cCPercentage = ((h.getClose() / call_previousPrice) - 1) * 100;
                                log.info("Call Percentage ::" + cCPercentage);
                                if (cCPercentage >= this.getEntryPercentageByDayOfTheWeek(cal.get(cal.DAY_OF_WEEK))) {
                                    this.entry(cC);
                                    ordersList = ordersRepository.findAll();
                                    output.setRemarks("Entry Taken in Call :D");
                                    break;
                                }
                            } else {
                                cP = h;
                                cPPercentage = ((h.getClose() / put_previousPrice) - 1) * 100;
                                log.info("Put Percentage ::" + cPPercentage);
                                if (cPPercentage >= this.getEntryPercentageByDayOfTheWeek(cal.get(cal.DAY_OF_WEEK))) {
                                    this.entry(cP);
                                    ordersList = ordersRepository.findAll();
                                    output.setRemarks("Entry Taken in Put :D");
                                    break;
                                }
                            }
                        }
                    }
                }
                List<OBBTOrderDetails> orderDetailsList = new ArrayList<>();
                for (Orders o : ordersList) {
                    OBBTOrderDetails orderDetails = new OBBTOrderDetails();
                    orderDetails.setInitialdatetime(o.getDatetime());
                    orderDetails.setCurrentdatetime(currentDate);
                    orderDetails.setSymbol(o.getSymbol());
                    orderDetails.setBuy_price(o.getBuyprice());
                    orderDetails.setSell_price(o.getSellprice());
                    orderDetails.setInitial_price(o.getCurrentprice());
                    orderDetails.setStatus(o.getStatus());
                    orderDetails.setNo_of_lots(o.getLots());
                    if (o.getSymbol().endsWith("CE")) {
                        orderDetails.setChange_In_Percent(cCPercentage);
                    }
                    else{
                        orderDetails.setChange_In_Percent(cPPercentage);
                    }
                    HistoricKey key = new HistoricKey();
                    key.setSymbol(o.getSymbol());
                    key.setDatetime(currentDate);
                    Optional<HistoricData> hData = historicDataRepository.findById(key);
                    if (hData.isPresent()) {
                        HistoricData h = hData.get();
                        orderDetails.setCurrent_price(h.getClose());

                    }
                    orderDetailsList.add(orderDetails);
                }
                output.setOrdersList(orderDetailsList);
            }
        }catch (ParseException pe){
            log.error("Parse exception during observe call ::"+pe.getMessage());
        }
        return output;
    }
    public Double getEntryPercentageByDayOfTheWeek(Integer dayOfTheWeek){
        log.info("day is ::"+dayOfTheWeek);
        log.info("Is this ::"+dayOfTheWeek.equals(5));
        log.info("Is this  ::"+(dayOfTheWeek==5));
        if(dayOfTheWeek.equals(3) || dayOfTheWeek.equals(2)){
            //MonDay TuesDay
            target_percent=15.0;
            stopLoss_percent=-15.0;
            return 30.0;
        }
        else if(dayOfTheWeek.equals(4)){
            //WednesDay
            target_percent=20.0;
            stopLoss_percent=-20.0;
            return 40.0;
        }
        else if(dayOfTheWeek.equals(5)){
            //ThursDay
            target_percent=25.0;
            stopLoss_percent=-25.0;
            return 50.0;
        }
        else if(dayOfTheWeek.equals(6)){
            //Friday
            target_percent=10.0;
            stopLoss_percent=-10.0;
            return 20.0;
        }
        else
            return null;
    }
    public Orders entry(HistoricData h){
        Orders order=null;
        Optional<Orders> o=ordersRepository.findById(h.getSymbol());
        ordersRepository.deleteAll();
        if(o.isPresent()){
            order = o.get();
            order.setBuyprice(h.getClose());
            order.setStatus("InProgress");
            ordersRepository.save(order);
        }
        return order;
    }
    public OBBTEntryOrderOuput observeNexit(int n){
        Double pl=0.0;
        String remarks="";
        OBBTEntryOrderOuput output =new OBBTEntryOrderOuput();
        List<OBBTOrderDetails> orderDetailsList = new ArrayList<>();
        try{
            List<Orders> ordersList=ordersRepository.findAll();
            List<HistoricKey> hKey= new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm") ;
            dateFormat.format(currentDate);

            //Getting Proper Date's data
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            output.setDate(currentDate);
            if (dateFormat.parse(dateFormat.format(currentDate)).after(dateFormat.parse("15:20"))) {
                remarks="TimeOut";
            } else {
                long timeInSecs = currentDate.getTime();
                currentDate = new Date(timeInSecs + (n * 60 * 1000));
            }
            if(!(ordersList.isEmpty())){
                HistoricKey indexKey = new HistoricKey();
                indexKey.setSymbol(index_instrument);
                indexKey.setDatetime(currentDate);
                Optional<HistoricData> indexData = historicDataRepository.findById(indexKey);
                if (indexData.isPresent()) {
                    HistoricData indexDataValues = indexData.get();
                    output.setBankNifty_currentValue(indexDataValues.getClose());
                }
                for(Orders o:ordersList){
                    OBBTOrderDetails orderDetails= new OBBTOrderDetails();
                    orderDetails.setInitialdatetime(o.getDatetime());
                    orderDetails.setCurrentdatetime(currentDate);
                    orderDetails.setSymbol(o.getSymbol());
                    orderDetails.setBuy_price(o.getBuyprice());
                    orderDetails.setSell_price(o.getSellprice());
                    orderDetails.setStatus(o.getStatus());
                    orderDetails.setNo_of_lots(o.getLots());
                    if(o.getStatus().equalsIgnoreCase("InProgress")){
                        HistoricKey key= new HistoricKey();
                        key.setSymbol(o.getSymbol());
                        key.setDatetime(currentDate);
                        Optional<HistoricData> hData=historicDataRepository.findById(key);
                        if(hData.isPresent()){

                            HistoricData h= hData.get();
                            orderDetails.setCurrent_price(h.getClose());
                            double ctarget_percent=((h.getClose()/o.getBuyprice())-1)*100;
                            double cStoploss_percent=ctarget_percent;
                            orderDetails.setProfit_or_loss((h.getClose()-o.getBuyprice())*lotSize*o.getLots());
                            pl=(h.getClose()-o.getBuyprice())*lotSize;
                            if (cStoploss_percent<=stopLoss_percent) {
                                this.exit(currentDate);
                                remarks = "Exit Triggered :D";
                            }
                            else if(ctarget_percent>=target_percent){
                                target_percent+=0.25*target_percent;
                                stopLoss_percent+=0.80*ctarget_percent;
                            }
                            log.info("SL Trigger ::"+stopLoss_percent);
                            log.info("Target Trigger ::"+target_percent);
                            orderDetails.setStoploss_percent(cStoploss_percent);
                            orderDetails.setTarget_percent(ctarget_percent);
                        }
                    }
                    orderDetailsList.add(orderDetails);
                }
            }

            output.setOrdersList(orderDetailsList);
            output.setProfit_or_Loss(pl*lotSize);
            output.setRemarks(remarks);
        }catch (ParseException pe){
            log.error("Parse exception during observe call ::"+pe.getMessage());
        }
        return output;
    }
    private void exit(Date cTime){
        List<Orders> allOrders= ordersRepository.findAll();
        for(Orders o:allOrders){
            HistoricKey key=new HistoricKey();
            key.setDatetime(cTime);
            key.setSymbol(o.getSymbol());
            Optional<HistoricData> h=historicDataRepository.findById(key);
            if(o.getStatus().equalsIgnoreCase("InProgress")){
                Optional<Orders> pendingOrder= ordersRepository.findById(o.getSymbol());
                if(pendingOrder.isPresent()){
                    Orders pOrder=pendingOrder.get();
                    pOrder.setStatus("Complete");
                    if(h.isPresent()){
                        pOrder.setSellprice(h.get().getClose());
                    }
                    ordersRepository.save(pOrder);
                }
            }
        }
    }
}
