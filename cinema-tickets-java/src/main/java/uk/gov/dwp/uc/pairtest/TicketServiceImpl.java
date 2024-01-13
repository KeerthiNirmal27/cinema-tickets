package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.*;
import java.util.stream.Collectors;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */


    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        //Validation 1 ------------- AccountId is greater than or equal to 0
        if (accountId <= 0) {
            throw new InvalidPurchaseException("AccountId is invalid");
        }

        // validation 2---------- Check TicketTypeRequest object is not null
        if(ticketTypeRequests == null && ticketTypeRequests.length==0) {
            throw new InvalidPurchaseException("Invalid Purchase Request Parameters");
        }

        // validation 3---------- noOfTickets is not greater than 20
        int totalTicketCount = Arrays.stream(ticketTypeRequests)
                                        .collect(Collectors.summingInt(TicketTypeRequest::getNoOfTickets));

        if (totalTicketCount > 20) {
            throw new InvalidPurchaseException("Maximum of 20 tickets can be purchased at a time");
        }

        // validation 4 ----------- Check for Adult ticket if Child or infant type exists in requestList
        List<TicketTypeRequest.Type> reqList =  Arrays.stream(ticketTypeRequests)
                                                        .map(TicketTypeRequest::getTicketType)
                                                        .collect(Collectors.toList());

        if ((reqList.contains(TicketTypeRequest.Type.CHILD) ||
                reqList.contains(TicketTypeRequest.Type.INFANT))&&
                !reqList.contains(TicketTypeRequest.Type.ADULT)) {
            throw new InvalidPurchaseException("Child And Infant ticket cannot be purchased without Adult Ticket");
        }

        //filter infant type tickets from the list for payment processing and seat allocation
        Map<TicketTypeRequest.Type, Integer> ticketRequestsForProcessing =
                        Arrays.stream(ticketTypeRequests)
                            .filter(ticketTypeRequest -> !ticketTypeRequest.getTicketType().equals(TicketTypeRequest.Type.INFANT))
                            .collect(Collectors.groupingBy(TicketTypeRequest::getTicketType, Collectors.summingInt(TicketTypeRequest::getNoOfTickets)));
        //System.out.println(ticketRequestsForProcessing.toString());

        if (ticketRequestsForProcessing.size() > 0) {
            int totalAmountToPay = 0;
            int totalSeatsToAllocate = 0;
            int ticketPricePerType;
            for (Map.Entry<TicketTypeRequest.Type, Integer> entry : ticketRequestsForProcessing.entrySet()) {
                ticketPricePerType = getTicketPrice(entry.getKey()) * entry.getValue();
                totalAmountToPay = totalAmountToPay + ticketPricePerType;

                totalSeatsToAllocate = totalSeatsToAllocate + entry.getValue();
            }

            if (totalAmountToPay > 0)
                System.out.println("totalAmountToPay : " + totalAmountToPay);
                new TicketPaymentServiceImpl().makePayment(accountId, totalAmountToPay);

            if (totalSeatsToAllocate > 0)
                System.out.println("Total Seats for allocation : " + totalSeatsToAllocate);
                new SeatReservationServiceImpl().reserveSeat(accountId, totalSeatsToAllocate);
            }

    }

    private int getTicketPrice(TicketTypeRequest.Type type) {
       switch (type) {
           case ADULT: return  20;
           case CHILD: return 10;
           default: return 0;
        }
     }
}