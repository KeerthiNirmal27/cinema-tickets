package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import java.util.ArrayList;
import java.util.List;

public class MainForTest {

    public static void main(String[] args) {
        System.out.println("Testing main Class");
        long accountId = 123;
        TicketServiceImpl ticketService = new TicketServiceImpl();
        TicketTypeRequest ticketRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.INFANT,5);
        TicketTypeRequest ticketRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD,5);
        TicketTypeRequest ticketRequest3 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT,5);
       // TicketTypeRequest ticketRequest4 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT,3);
        TicketTypeRequest ticketRequest5 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD,5);
        ticketService.purchaseTickets(accountId,ticketRequest1
                ,ticketRequest2
                ,ticketRequest3
                //,ticketRequest4
                ,ticketRequest5
        );
    }
}
