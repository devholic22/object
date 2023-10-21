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
### 티켓 판매 어플리케이션 - 문제점
> 로버트 마틴의 `<클린 소프트웨어: 애자일 원칙과 패턴, 그리고 실천 방법>`에 따르면 소프트웨어의 모든 모듈은 제대로 실행되어야 하고, 변경이 용이하며, 이해하기 쉬워야 한다. 그러나 지금까지의 구조는 변경 용이성과 쉬운 이해가 부족하다.
#### 1. 관람객과 판매원이 수동적인 입장이다.
소극장 (Theater) 코드를 보면 문제가 보인다.

여기에서 문제를 보면, 소극장이 관객의 가방을 직접 열어 초대장이 있는지 검사한다. 또, 판매원의 매표소를 직접 확인하여 금액을 넣어주고 티켓을 꺼내고 있다. 따라서 관람객과 판매원은 수동적으로 진행되고 있다.

이렇게 수동적인 관람객과 판매원은 실제 상식에서 벗어나기 때문에 쉬운 이해와 부합하지 않는다. 실제 세계에서는 관람객이 직접 자신의 가방을 꺼내 확인하고, 판매원이 직접 자신의 매표소에 돈을 넣고 티켓을 가져오기 때문이다.

또한, 너무 많은 사항들을 다 알고 있어야만 이해 가능하다. 예시로 관람객은 가방을 가지고 있고, 가방 안에 현금이 있음을 알고 있어야 한다. 이렇듯 하나의 클래스나 메서드에서 너무 많은 세부사항을 다루고 있다면 모두에게 큰 부담을 주게 된다.

#### 2. 변경에 취약하다.
너무 요구사항을 그대로 따르기만 했기 때문에 변경사항이 생길 경우 전부 바꿔야 하는 문제가 있다.

예시로, 관람객이 가방을 들고 있지 않거나, 현금 말고 신용카드를 이용해서 결제를 해야 한다면?

한 객체가 다른 객체에 대해 “알고 있다”는 것은 그 객체를 “의존”하고 있다는 것을 뜻하며, 이는 곧 객체가 변경될 때 그 객체에게 의존하는 다른 객체도 함께 변경될 수 있다는 가능성을 내포한다.

우리가 집중해야 할 것은 협력이 이루어지는 객체 세계를 만드는 것이기 때문에, 완전히 의존성을 가지고 있지 않은 객체를 만들 필요는 없다. 다만, 애플리케이션의 기능을 구축하는 데 있어 필요한 최소한의 의존성만 가지고 있도록 해야 변경에 취약하지 않게 설계할 수 있을 것이다.

의존성이 너무 높을 경우를 가리켜 결합도가 높다고 말한다.
