package com.example.warehouse.simulator.order.model;

import com.example.warehouse.simulator.product.model.Product;
import com.example.warehouse.simulator.robot.model.Robot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

import static com.example.warehouse.simulator.order.model.Order.OrderStatus.PENDING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
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

    @Enumerated(EnumType.STRING)
    private OrderStatus status = PENDING;

    enum OrderStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}
