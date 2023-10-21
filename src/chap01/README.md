# 1장: 객체, 설계
> 어떤 분야를 막론하고 이론을 정립할 수 없는 초기에는 실무가 먼저 급속한 발전을 이룬다. 소프트웨어 분야는 걸음마 단계이기 때문에 실무가 더 앞서 있고, 때문에 실무를 관찰한 결과를 바탕으로 이론을 정립한다. 

## 티켓 판매 어플리케이션
### 요구사항
* 추첨을 통해 선정된 관람객에게 공연을 무료로 관람할 수 있는 초대장을 발송해준다.
  * 이벤트에 당첨된 관람객은 초대장을 티켓으로 교환한 뒤 입장한다.
  * 이벤트에 당첨되지 못한 관람객은 티켓을 구매해야만 입장할 수 있다.
* 관람객이 가지고 올 수 있는 소지품은 초대장, 현금, 티켓 세 가지이다.
  * 관람객은 소지품을 보관할 용도로 가방을 들고 올 수 있다.
* 관람객이 소극장에 입장하기 위해서는 매표소에서 초대장을 티켓으로 교환하거나 구매해야 한다.
  * 매표소에는 관람객에게 판매할 티켓과 티켓의 판매 금액이 보관되어 있어야 한다.
* 판매원은 매표소에서 초대장을 티켓으로 교환해 주거나 티켓을 판매하는 역할을 수행한다.
### 초기 코드
#### 초대장 (Invitation)

```java
import java.time.LocalDateTime;

public class Invitation {

    private LocalDateTime when;
}
```
#### 티켓 (Ticket)
```java
public class Ticket {
    
    private Long fee; // 티켓의 금액
    
    public Long getFee() {
        return fee;
    }
}
```
#### 가방 (Bag)

```java
import chap01.version1.Invitation;
import chap01.version1.Ticket;

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
```
#### 관객 (Audience)
```java
import chap01.version1.Bag;

public class Audience {

    private Bag bag;
    
    public Audience(Bag bag) {
        this.bag = bag;
    }
    
    public Bag getBag() {
        return bag;
    }
}
```
#### 매표소 (TicketOffice)

```java
import chap01.version1.Ticket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TicketOffice {

    private Long amount;
    private List<Ticket> tickets = new ArrayList<>();

    public TicketOffice(Long amount, Ticket... tickets) {
        this.amount = amount;
        this.tickets.addAll(Arrays.asList(tickets));
    }
    
    public Ticket getTicket() {
        return tickets.remove(0);
    }
    
    public void minusAmount(Long amount) {
        this.amount -= amount;
    }
    
    public void plusAmount(Long amount) {
        this.amount += amount;
    }
}
```
#### 소극장 (Theater)

```java
import chap01.version1.Audience;
import chap01.version1.Ticket;
import chap01.version1.TicketSeller;

public class Theater {

    private TicketSeller ticketSeller;

    public TicketSeller(TicketSeller ticketSeller) {
        this.ticketSeller = ticketSeller;
    }

    public void enter(Audience audience) {
        // 관람객의 가방 안에 초대장이 들어 있는지 확인
        if (audience.getBag().hasInvitation()) {
            // 티켓을 매표소에서 받아 관객의 가방에 넣어준다.
            Ticket ticket = ticketSeller.getTicketOffice().getTicket();
            audience.getBag().setTicket(ticket);
        } else {
            // 아니라면 관객의 가방에서 티켓 요금만큼 현금을 뺀 뒤에 매표소에 금액을 넣은 뒤 티켓을 넣어준다.
            Ticket ticket = ticketSeller.getTicketOffice().getTicket();
            audience.getBag().minusAmount(ticket.getFee());
            ticketSeller.getTicketOffice().plusAmount(ticket.getFee());
            audience.getBag().setTicket(ticket);
        }
    }
}
```
