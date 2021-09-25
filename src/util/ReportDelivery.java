package util;

import automail.MailItem;
import automail.Robot;
import exceptions.MailAlreadyDeliveredException;
import simulation.Clock;
import simulation.IMailDelivery;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ReportDelivery implements IMailDelivery {


    /**An array list to record mails that have been delivered*/
    private Set<MailItem> deliveredItems;
    private static double total_delay = 0;

    public ReportDelivery()
    {
        deliveredItems = new HashSet<>();
    }

    /** Confirm the delivery and calculate the total score */
    @Override
    public void deliver(Robot robot, MailItem deliveryItem, String additionalLog ){
        if(!deliveredItems.contains(deliveryItem))
        {
            deliveredItems.add(deliveryItem);
            System.out.printf("T: %3d > %7s-> Delivered(%4d) [%s%s| Service Fee: %.2f | Maintainence : %.2f | Avg Operating Time"
            		+ ": %.2f | Total Charge: %.2f] %n", Clock.Time(), robot.getIdTube(), 
            		deliveredItems.size(), deliveryItem.toString(), additionalLog,
            		getServiceFee(deliveryItem.getDestFloor()),robot.getMaintainenceCost(), robot.getAvgOperatingTime()
            		, getTotalFee(robot.getMaintainenceCost(),getServiceFee(deliveryItem.getDestFloor())));
            // Calculate delivery score
            total_delay += calculateDeliveryDelay(deliveryItem);
        }
        else{
            try {
                throw new MailAlreadyDeliveredException();
            } catch (MailAlreadyDeliveredException e) {
                e.printStackTrace();
            }
        }
    }

    public double getTotal_delay()
    {
        return total_delay;
    }

    @Override
    public Set<MailItem> getDeliveredItems() {
        return Collections.unmodifiableSet(deliveredItems);
    }

    private static double calculateDeliveryDelay(MailItem deliveryItem) {
        // Penalty for longer delivery times
        final double penalty = 1.2;
        double priority_weight = 0;
        // Take (delivery time - arrivalTime)**penalty * (1+sqrt(priority_weight))
        return Math.pow(Clock.Time() - deliveryItem.getArrivalTime(),penalty)*(1+Math.sqrt(priority_weight));
    }
    private static double getServiceFee(int floor) {
    	WIFIAdapter wfa = new WIFIAdapter();
    	return wfa.getServiceFee(floor);
    }
    private static double getTotalFee(double maintainenceCost, double serviceFee) {
    	return maintainenceCost + serviceFee;
    }

}
