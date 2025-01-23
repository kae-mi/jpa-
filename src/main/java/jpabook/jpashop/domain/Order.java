package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // ===연관관계 메서드===
    public void setMember(Member member) {
        this.member = member;
        member.getOrder().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==order를 생성하는 메서드, 생성하면서 여러 연관관계를 셋팅함==// static 으로 만든 이유는 바로 호출할 수 있기 때문??
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//
    /**
     * 주문을 취소했을 때
     */
    public void cancel() {
         if (delivery.getStatus() == DeliveryStatus.COMP) {
             throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
         }

         this.setStatus(OrderStatus.CANCEL);
         for (OrderItem orderItem : orderItems) {
            orderItem.cancel(); //주문을 취소하면 주문안의 상품들의 재고도 원복해주어야 하며, 그것을 위한 메서드이다.
         }
    }

    //==조회 로직==//
    /**
     * 하나의 주문에 대한 total price 조회
     */
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
