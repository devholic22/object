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

### 티켓 판매 어플리케이션 - 1차 개선
문제점은 Theater에서 직접 접근한다는 것이다. 때문에 자율성을 높이기 위해 다음과 같이 한다.
#### 1. TicketSeller의 자율성을 높인다.
TicketSeller의 코드에 다음 메서드를 추가한다.
```java
public void sellTo(Audience audience) {
    if (audience.getBag().hasInvitation()) {
        Ticket ticket = ticketOffice.getTicket();
    } else {
        Ticket ticket = ticketOffice.getTicket();
        audience.getBag().minusAmount(ticket.getFee());
        ticketOffice.plusAmount(ticket.getFee());
        audience.getBag().setTicket(ticket);
    }
}
```
또한 기존에 TicketSeller가 가지고 있던 `getTicketOffice` 메서드를 삭제한다.

이제 Theater에서 `enter` 메서드는 다음과 같이 변한다.
```java
public void enter(Audience audience) {
    ticketSeller.sellTo(audience);
}
```

* Theater에서는 TicketSeller가 가지고 있는 TicketOffice에 대해 알 수 있는 방법이 없다. getTicketOffice 메서드를 삭제했기 때문이다.
* TicketSeller는 확실히 이전보다 더 자율적으로 변화되었다고 할 수 있다.
* 이에 따라 캡슐화가 진행되었다고 할 수 있으며, 캡슐화의 목적은 변경하기 쉬운 객체를 만드는 것이다. 캡슐화는 객체와 객체 사이의 결합도를 낮출 수 있다. (= 객체 사이의 불필요한 의존 관계를 없앨 수 있다.)
* Theater는 오직 TicketSeller의 인터페이스에만 의존하게 되었으며, TicketSeller가 TicketOffice 인스턴스를 포함하고 있다는 사실은 구현의 영역에 속한다.

#### 2. Audience의 자율성을 높인다.
TicketSeller의 sellTo 메서드는 아직 Audience의 자율성을 보장하지 못하는 문제가 남아 있다. (그대로 이동시킨 것이기 때문) 

따라서 Audience에 다음 메서드를 추가한다.

```java
public Long buy(Ticket ticket) {
    if (bag.hasInvitation()) {
        bag.setTicket(ticket);
        return 0L;
    } else {
        bag.setTicket(ticket);
        bag.minusAmount(ticket.getFee());
        return ticket.getFee();
    }
}
```
또한, 기존에 Audience가 가지고 있던 `getBag` 메서드를 삭제한다.

TicketSeller의 `sellTo` 메서드는 다음과 같이 변한다.
```java
public void sellTo(Audience audience) {
    ticketOffice.plusAmount(audience.buy(ticketOffice.getTicket()));
}
```
* Audience는 자신의 가방을 보여주도록 하지 않는다.
* 초대장을 받았다면 티켓으로 교환되는 것이기 때문에 0을 더하며, 그렇지 않을 경우에는 티켓을 구입하는 것이기에 티켓 값 만큼 매표소에 더하게 된다.
* 코드를 수정한 결과, TicketSeller와 Audience 간의 결합도를 낮출 수 있게 되었으며, TicketSeller가 Audience의 인터페이스만 의존하게 되었다.

### 캡슐화와 응집도
변경된 Theater는 Audience와 TicketSeller가 어떤 방식으로 주어진 책임을 수행하는지에 대해 구체적으로 알지 못한다. 즉, 예시로 관람객이 가방을 들고 있는지 없는지에 대해서 알 필요가 없게 된 것이다. 따라서 변경을 하더라도 쉽게 대처할 수 있게 되었으며, 이처럼 밀접하게 연관된 작업만을 수행하고 연관성 없는 작업은 다른 객체에게 위임하는 경우를 응집도가 높다고 말한다. 객체지향의 원리에 따라 응집도를 높이기 위해서는 자신이 가지고 있는 데이터를 최대한 감추어 외부의 간섭을 배제 (객체 스스로 자신의 데이터를 책임지기)하고, 메시지를 통해서만 협력할 수 있도록 해야 한다.

## 절차지향 vs 객체지향
### 절차지향
가장 처음에 설계했던 방식이 바로 절차지향적으로 코드를 작성한 방식이다.

Theater의 enter 메서드에서는 Audience, TicketSeller, Bag, TicketOffice를 직, 간접적으로 알고 있으며, 이후 “관람객의 입장” 이라는 문제를 이 메서드에서 전체적으로 다 수행하였다.

이때 Theater의 enter 메서드를 프로세스 (process), Audience, TicketSeller, Bag, TicketOffice를 데이터 (data)라 하며, 프로세스와 데이터를 별도의 모듈에 위치시키는 방식이 절차지향의 특징이다. (프로세스는 Theater에 있는 반면, 데이터들은 전부 각자의 클래스로 흩어져 있다.)

이러한 절차지향은 위에서 다루었듯 우리의 직관에 위배되며, 데이터의 변경으로 인한 영향을 지역적으로 고립시키기 어렵다. 변경에 용이하게 만들려면 한 번에 하나의 클래스만 변경하도록 해야 하는데, 절차지향은 프로세스가 필요한 모든 데이터를 의존하기 때문이다.

### 객체지향
우리가 개선한 방식이 바로 객체지향적으로 코드를 작성한 방식이다.

Theater는 TicketSeller만 의존하게 되었으며, Audience는 자신의 가방을 외부 객체에게 드러낼 필요가 없게 되었다. (TicketSeller가 Audience를 의존하게 되었지만 이는 적절한 트레이드 오프의 결과이다.)

객체지향의 핵심은 데이터를 가지고 있는 곳에서 프로세스가 이루어지는, 즉 데이터와 프로세스가 동일한 모듈 내부에 위치하도록 프로그래밍하는 것이다. 따라서, 캡슐화를 통해 객체 간의 결합도를 낮추어 변경에 용이하게 만든다.

## 책임의 이동
책임 관점에서 정의해보면 절차지향과 객체지향을 다시 비교해볼 수 있다.

절차지향적 관점에서는 Theater가 모든 책임을 갖추고 있었다. 반면 객체지향에서는 책임이 적절하게 나뉘었다.

### 절차지향과 객체지향의 구분법
코드에서 데이터와 데이터를 사용하는 프로세스가 별도의 객체에 위치하고 있다면 절차적 프로그래밍 방식을 따르고 있을 확률이 높으며, 그 반대의 경우에는 객체지향적 프로그래밍 방식을 따르고 있을 확률이 높다.

객체지향의 핵심은 적절한 객체에 적절한 책임을 할당하는 것임을 잊지 말자. 책임 중심적으로 생각해야 한다. 그렇게 하면 우리의 직관에 따르듯이 코드를 설계하게 되어 더 이해하기 쉬워진다.

## 추가 개선점
### 1. Bag 클래스를 자율적으로 만들기
개선된 Audience 클래스를 보면, 더 개선할 수 있는 여지가 있다.
```java
public Long buy(Ticket ticket) {
    if (bag.hasInvitation()) {
        bag.setTicket(ticket);
        return 0L;
    } else {
        bag.setTicket(ticket);
        bag.minusAmount(ticket.getFee());
        return ticket.getFee();
    }
}
```
* Bag이 너무 수동적인 입장이라는 생각이 들 것이다.
* 따라서 Bag 또한 자율적인 객체로 만들어보면 아래와 같다.

Bag에 hold 메서드를 만든다.
```java
public Long hold(Ticket ticket) {
    if (hasInvitation()) {
        setTicket(ticket);
    } else {
        setTicket(ticket);
        minusAmount(ticket.getFee());
        return ticket.getFee();
    }
    // hasInvitation, setTicket, minusAmount의 접근자를 전부 private로 바꿀 수 있게 되었다.
}
```

Bag은 이제 자신이 가지고 있는 데이터 (Invitation, Ticket)를 이용하는 프로세스를 가지고 있도록 되었다.

이에 따라 Audience의 buy 메서드는 다음과 같이 변경된다.

```java
public Long buy(Ticket ticket) {
    return bag.hold(ticket);
}
```
### 2. TicketOffice 클래스를 자율적으로 만들기
기존의 TicketSeller가 가지고 있는 sellTo 메서드를 본다.
```java
public void sellTo(Audience audience) {
    ticketOffice.plusAmount(audience.buy(ticketOffice.getTicket()));
}
```
* TicketOffice가 가지고 있는 티켓을 강제로 꺼내서 Audience에게 할당해주고 있다.
* 따라서 다음과 같이 변경한다.

TicketOffice에 다음 메서드를 추가한다.
```java
public void sellTicketTo(Audience audience) {
    plusAmount(audience.buy(getTicket()));
}
```
자신이 가지고 있는 데이터 (Ticket)를 사용하는 프로세스 (sellTicketTo)의 위치로 같이 두게 되었다.

TicketSeller는 다음과 같이 변경된다.

```java
public void sellTo(Audience audience) {
    ticketOffice.sellTicketTo(audience);
}
```
### 아쉬운 점
그런데 이 변경은 완벽하지 못하다고 할 수 있다. 이전의 관계에서 sellTicketTo 메서드를 작성하기 전에는, TicketOffice가 Audience에 대해 아예 모르고 있었다. 그러나 지금은 의존관계가 생겨버렸다.

Audience에 대한 결합도와, TicketOffice에게 자율성을 부여해주는 것 중 어떤 것을 고려해야 할 지는 개발자들의 선택이며, 따라서 트레이드 오프에 대한 고민을 해야 한다.

## 의인화
객체지향적으로 바꾼 결과, 우리의 직관에 따라 수행하여 더 이해하기 쉬워졌다고 했다. 그러나 Bag, TicketOffice 등은 실세계에서는 자율적인 존재가 아니다. 가방에서 돈을 꺼내는 것은 가방이 아니라 관람객이기 때문이다. 이처럼 현실에서는 수동적인 존재라고 하더라도 객체지향의 세계에 들어오면 모든 것이 능동적이고 자율적인 존재가 되며, 이를 의인화라고 한다.

## 설계의 중요성
> 좋은 설계는 오늘 완성해야 하는 기능을 구현하는 코드를 짜야 하는 동시에 내일 쉽게 변경할 수 있는 코드를 짜는 것이다. 즉, 오늘 요구하는 기능을 온전히 수행하면서 내일의 변경을 매끄럽게 수용할 수 있어야 한다.

### 변경 = 버그 발생 가능
변경되지 않는 코드는 없다. 실제 서비스로 나가면 요구사항은 변경되기 마련이다. 또 이 변경의 과정에서 필연적으로 버그가 발생할 수 있다. 유연하지 못한 설계는 버그 발생률을 높이며, 이는 프로그래머에게 코드 수정 의지를 꺾어뜨리는 요소가 된다.

### 객체지향의 중요한 점
거듭 말했지만, 객체지향을 제대로 이해하기 위해서는 데이터와 프로세스를 동일한 공간에 모아놓는 것을 넘어서서 협력하는 객체 사이의 의존성을 적절히 관리하는 것이 포함된다.
