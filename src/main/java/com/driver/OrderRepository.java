package com.driver;

import org.springframework.stereotype.Repository;

import java.sql.SQLOutput;
import java.util.*;

@Repository
public class OrderRepository {
    Map<String, Order> orders = new HashMap<>();
    Map<String,DeliveryPartner> partners = new HashMap();
    Map<String, String> orderPartnerPairs = new HashMap<>();
    Map<String, List<String>> partnerOrdersList = new HashMap<>();


    public void addOrder(Order order) {
        orders.put(order.getId(), order);
    }

    public void addPartner(String partnerId) {
        DeliveryPartner np = new DeliveryPartner(partnerId);
//        System.out.println("partneradded");
        partners.put(partnerId,np);
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
//        System.out.println("1111");
//        System.out.println(orders.get(orderId) + " " + partners.get(partnerId));
       if(orders.containsKey(orderId) && partners.containsKey(partnerId)){
//           System.out.println("Working");
           orderPartnerPairs.put(orderId, partnerId);

           List<String> currentOrders = new ArrayList<>();
           if(partnerOrdersList.containsKey(partnerId)){
               currentOrders = partnerOrdersList.get(partnerId);
           }
           currentOrders.add(orderId);
           partnerOrdersList.put(partnerId, currentOrders);

           DeliveryPartner deliveryPartner = partners.get(partnerId);
           deliveryPartner.setNumberOfOrders(currentOrders.size());
//           System.out.println(deliveryPartner.getNumberOfOrders());
       }
    }

    public Order getOrderById(String orderId) {
        return orders.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return partners.get(partnerId);
    }

    public int getOrderCountByPartnerId(String partnerId) {
        if(partners.containsKey(partnerId))
            return partners.get(partnerId).getNumberOfOrders();
        return 0;
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        return partnerOrdersList.get(partnerId);
    }

    public List<String> getAllOrders() {
        List<String> allOrders = new ArrayList<>();
        for(String key : orders.keySet())
            allOrders.add(key);

        return allOrders;
    }

    public int getCountOfUnassignedOrders() {
        int count = orders.size() - orderPartnerPairs.size();
        return count;
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(int t, String partnerId) {
        int count = 0;
        if(partnerOrdersList.containsKey(partnerId)){
            List<String> list = partnerOrdersList.get(partnerId);
            for(int i = 0 ; i<list.size() ; i++){
                String order_id = list.get(i);
                Order order = orders.get(order_id);
                if(order.getDeliveryTime() > t)
                    count++;
            }
        }
        return count;
    }

    public int getLastDeliveryTimeByPartnerId(String partnerId) {
        int maxTime = 0;
        System.out.println(partnerOrdersList.get(partnerId));
        if(partnerOrdersList.containsKey(partnerId)){
            List<String> list = partnerOrdersList.get(partnerId);
            for(int i = 0 ; i<list.size(); i++){
                Order order = orders.get(list.get(i));
                System.out.println("Working!"+ " " + order.getDeliveryTime());
                if(order.getDeliveryTime() > maxTime)
                    maxTime = order.getDeliveryTime();
            }
        }
        System.out.println(maxTime);
        return maxTime;
    }

    public void deletePartnerById(String partnerId) {
        if(partners.containsKey(partnerId))
            partners.remove(partnerId);

        List<String> listOfOrders = new ArrayList<>();
        if(partnerOrdersList.containsKey(partnerId))
            listOfOrders = partnerOrdersList.remove(partnerId);

        for(int i = 0 ; i<listOfOrders.size() ; i++){
            if(orderPartnerPairs.containsKey(listOfOrders.get(i)))
                orderPartnerPairs.remove(listOfOrders.get(i));
        }
    }

    public void deleteOrderById(String orderId) {
        if(orders.containsKey(orderId))
            orders.remove(orderId);

        String dpid = "";
        if(orderPartnerPairs.containsKey(orderId)) {
            dpid = orderPartnerPairs.get(orderId);
            orderPartnerPairs.remove(orderId);
        }
        if(partnerOrdersList.containsKey(dpid))
        {
            List<String> list = partnerOrdersList.get(dpid);
            for(int i = 0 ; i<list.size() ; i++){
                if(list.get(i).equals(orderId))
                    list.remove(i);
            }
        }
    }
}
