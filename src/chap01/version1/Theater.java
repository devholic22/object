package chap01.version1;

public class Theater {

    private TicketSeller ticketSeller;

    public Theater(TicketSeller ticketSeller) {
        this.ticketSeller = ticketSeller;
    }

    public void enter(Audience audience) {
        // 관람객의 가방 안에 초대장이 들어 있는지 확인
        if (audience.getBag().hasInvitation()) {
            // 티켓을 매표소에서 받아 관객의 가방에 넣어준다
            Ticket ticket = ticketSeller.getTicketOffice().getTicket();
            audience.getBag().setTicket(ticket);
        } else {
            // 아니라면 관객의 가방에서 티켓 요금만큼 현금을 뺀 뒤에 매표소에 금액을 넣은 뒤 티켓을 넣어준다
            Ticket ticket = ticketSeller.getTicketOffice().getTicket();
            audience.getBag().minusAmount(ticket.getFee());
            ticketSeller.getTicketOffice().plusAmount(ticket.getFee());
            audience.getBag().setTicket(ticket);
        }
    }
}
