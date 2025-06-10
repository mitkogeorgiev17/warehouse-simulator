package com.example.warehouse.simulator.order;

import com.example.warehouse.simulator.product.model.Product;
import com.example.warehouse.simulator.robot.model.Robot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

import static com.example.warehouse.simulator.order.Order.OrderStatus.PENDING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ElementCollection
    @CollectionTable(name = "order_items")
    @MapKeyJoinColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<Product, Long> items;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_robot_id")
    private Robot robot;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = PENDING;

    enum OrderStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}
