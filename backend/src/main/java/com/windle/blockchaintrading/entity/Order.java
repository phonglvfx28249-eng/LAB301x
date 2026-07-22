package com.windle.blockchaintrading.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "side", nullable = false)
    private Side side;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false)
    private OrderType orderType;

    @Column(name = "price", precision = 28, scale = 8)
    private BigDecimal price;

    @Column(name = "quantity", precision = 28, scale = 8, nullable = false)
    private BigDecimal quantity;

    @Column(name = "remaining_quantity", precision = 28, scale = 8, nullable = false)
    private BigDecimal remainingQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order")
    private List<WalletTransaction> walletTransactions = new ArrayList<>();

    // This order acting as the buy side of a trade
    @OneToMany(mappedBy = "buyOrder")
    private List<Trade> buyTrades = new ArrayList<>();

    // This order acting as the sell side of a trade
    @OneToMany(mappedBy = "sellOrder")
    private List<Trade> sellTrades = new ArrayList<>();

    public enum Side {
        BUY, SELL
    }

    public enum OrderType {
        LIMIT, MARKET, STOP_LIMIT
    }

    public enum OrderStatus {
        PENDING, OPEN, PARTIALLY_FILLED, FILLED, CANCELLED, REJECTED
    }

    public Order() {
    }

    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(BigDecimal remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<WalletTransaction> getWalletTransactions() {
        return walletTransactions;
    }

    public void setWalletTransactions(List<WalletTransaction> walletTransactions) {
        this.walletTransactions = walletTransactions;
    }

    public List<Trade> getBuyTrades() {
        return buyTrades;
    }

    public void setBuyTrades(List<Trade> buyTrades) {
        this.buyTrades = buyTrades;
    }

    public List<Trade> getSellTrades() {
        return sellTrades;
    }

    public void setSellTrades(List<Trade> sellTrades) {
        this.sellTrades = sellTrades;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", side=" + side +
                ", orderType=" + orderType +
                ", price=" + price +
                ", quantity=" + quantity +
                ", remainingQuantity=" + remainingQuantity +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}