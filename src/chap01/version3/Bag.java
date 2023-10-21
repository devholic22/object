package chap01.version3;

public class Bag {

    private Long amount; // 현금
    private Invitation invitation;
    private Ticket ticket;

    // 관객은 현금만 가지고 있거나
    public Bag(long amount) {
        this(null, amount);
    }
    // 초대장과 현금을 가진 채로 입장할 수 있다.
    public Bag(Invitation invitation, long amount) {
        this.invitation = invitation;
        this.amount = amount;
    }

    public boolean hasInvitation() {
        return invitation != null;
    }

    public boolean hasTicket() {
        return ticket != null;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public void minusAmount(Long amount) {
        this.amount -= amount;
    }

    public void plusAmount(Long amount) {
        this.amount += amount;
    }
}
