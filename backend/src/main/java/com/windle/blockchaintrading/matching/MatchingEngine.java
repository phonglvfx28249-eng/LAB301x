package com.windle.blockchaintrading.matching;

import com.windle.blockchaintrading.entity.Order;
import com.windle.blockchaintrading.entity.User;
import com.windle.blockchaintrading.entity.Wallet;
import com.windle.blockchaintrading.repository.OrderRepository;
import com.windle.blockchaintrading.service.TradeService;
import com.windle.blockchaintrading.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.PriorityQueue;

@Component
public class MatchingEngine {

    private final PriorityQueue<Order> buyOrders;
    private final PriorityQueue<Order> sellOrders;
    private final WalletService walletService;
    private final OrderRepository orderRepository; // Added OrderRepository for direct database updates
    private final TradeService tradeService;

    @Autowired
    public MatchingEngine(WalletService walletService, OrderRepository orderRepository, TradeService tradeService) {
        // Updated Comparators to handle Price-Time (FIFO) priority and prevent null price crashes
        this.buyOrders = new PriorityQueue<>(
                Comparator.comparing(Order::getPrice, Comparator.reverseOrder())
                        .thenComparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
        );
        this.sellOrders = new PriorityQueue<>(
                Comparator.comparing(Order::getPrice)
                        .thenComparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
        );
        this.walletService = walletService;
        this.orderRepository = orderRepository;
        this.tradeService = tradeService;
    }

    private boolean addOrder(Order order) {
        if (order.getSide().equals(Order.Side.BUY)) {
            buyOrders.offer(order);
        } else if (order.getSide().equals(Order.Side.SELL)) {
            sellOrders.offer(order);
        } else {
            throw new IllegalArgumentException("Invalid order side: " + order.getSide());
        }
        return true;
    }

    @Transactional
    public boolean matchOrder(Order order) {
        boolean isMarket = order.getOrderType() == Order.OrderType.MARKET;

        if (order.getSide().equals(Order.Side.BUY)) {
            // Updated while-condition to prevent NPE when order.getPrice() is null for Market Orders
            while (!sellOrders.isEmpty() && order.getRemainingQuantity().compareTo(BigDecimal.ZERO) > 0) {
                Order sellOrder = sellOrders.peek(); // Peek first before polling

                // Price Check: Limit orders must meet price condition. Market orders skip this check.
                if (!isMarket) {
                    if (order.getPrice() == null || order.getPrice().compareTo(sellOrder.getPrice()) < 0) {
                        break;
                    }
                }

                sellOrders.poll(); // Now safely poll

                BigDecimal tradeQuantity = order.getRemainingQuantity().min(sellOrder.getRemainingQuantity());
                BigDecimal tradePrice = sellOrder.getPrice();

                // Call tradeService to execute and persist the trade
                tradeService.executeTrade(
                        order.getId(),
                        sellOrder.getId(),
                        order.getUser().getId(),
                        sellOrder.getUser().getId(),
                        tradePrice,
                        tradeQuantity
                );

                // Update the remaining quantities locally and in the database
                order.setRemainingQuantity(order.getRemainingQuantity().subtract(tradeQuantity));
                sellOrder.setRemainingQuantity(sellOrder.getRemainingQuantity().subtract(tradeQuantity));

                updateRemainingQuantity(sellOrder.getId(), sellOrder.getRemainingQuantity());
                updateRemainingQuantity(order.getId(), order.getRemainingQuantity());

                if (sellOrder.getRemainingQuantity().compareTo(BigDecimal.ZERO) == 0) {
                    updateOrderStatus(sellOrder.getId(), Order.OrderStatus.FILLED);
                } else {
                    addOrder(sellOrder);
                    updateOrderStatus(sellOrder.getId(), Order.OrderStatus.PARTIAL);
                }

                if (order.getRemainingQuantity().compareTo(BigDecimal.ZERO) == 0) {
                    updateOrderStatus(order.getId(), Order.OrderStatus.FILLED);
                    break;
                }
            }

            // if dont have enough liquidity to fill the order, set the order status to PARTIAL
            if (order.getRemainingQuantity().compareTo(BigDecimal.ZERO) > 0 && order.getRemainingQuantity().compareTo(order.getQuantity()) < 0) {
                updateOrderStatus(order.getId(), Order.OrderStatus.PARTIAL);
            }

            if (order.getRemainingQuantity().compareTo(BigDecimal.ZERO) > 0) {
                if (order.getOrderType().equals(Order.OrderType.LIMIT)) {
                    addOrder(order);
                } else {
                    Wallet userWallet = getWalletByOrder(order);

                    // Fixed: Market order remaining quantity is NOT forcibly zeroed if liquidity runs out
                    if (order.getRemainingQuantity().compareTo(order.getQuantity()) == 0) {
                        updateOrderStatus(order.getId(), Order.OrderStatus.CANCELLED);
                    } else {
                        updateOrderStatus(order.getId(), Order.OrderStatus.PARTIAL);
                    }
                }
            }

        } else if (order.getSide().equals(Order.Side.SELL)) {
            while (!buyOrders.isEmpty() && order.getRemainingQuantity().compareTo(BigDecimal.ZERO) > 0) {
                Order buyOrder = buyOrders.peek();

                if (!isMarket) {
                    if (order.getPrice() == null || order.getPrice().compareTo(buyOrder.getPrice()) > 0) {
                        break;
                    }
                }

                buyOrders.poll();

                BigDecimal tradeQuantity = order.getRemainingQuantity().min(buyOrder.getRemainingQuantity());
                BigDecimal tradePrice = buyOrder.getPrice();

                tradeService.executeTrade(
                        buyOrder.getId(),
                        order.getId(),
                        buyOrder.getUser().getId(),
                        order.getUser().getId(),
                        tradePrice,
                        tradeQuantity
                );

                order.setRemainingQuantity(order.getRemainingQuantity().subtract(tradeQuantity));
                buyOrder.setRemainingQuantity(buyOrder.getRemainingQuantity().subtract(tradeQuantity));

                updateRemainingQuantity(buyOrder.getId(), buyOrder.getRemainingQuantity());
                updateRemainingQuantity(order.getId(), order.getRemainingQuantity());

                if (buyOrder.getRemainingQuantity().compareTo(BigDecimal.ZERO) == 0) {
                    updateOrderStatus(buyOrder.getId(), Order.OrderStatus.FILLED);
                } else {
                    addOrder(buyOrder);
                    updateOrderStatus(buyOrder.getId(), Order.OrderStatus.PARTIAL);
                }

                if (order.getRemainingQuantity().compareTo(BigDecimal.ZERO) == 0) {
                    updateOrderStatus(order.getId(), Order.OrderStatus.FILLED);
                    break;
                }
            }

            if (order.getRemainingQuantity().compareTo(BigDecimal.ZERO) > 0 && order.getRemainingQuantity().compareTo(order.getQuantity()) < 0) {
                updateOrderStatus(order.getId(), Order.OrderStatus.PARTIAL);
            }

            if (order.getRemainingQuantity().compareTo(BigDecimal.ZERO) > 0) {
                if (order.getOrderType().equals(Order.OrderType.LIMIT)) {
                    addOrder(order);
                } else {
                    Wallet userWallet = getWalletByOrder(order);

                    if (order.getRemainingQuantity().compareTo(order.getQuantity()) == 0) {
                        updateOrderStatus(order.getId(), Order.OrderStatus.CANCELLED);
                    } else {
                        updateOrderStatus(order.getId(), Order.OrderStatus.PARTIAL);
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid order side: " + order.getSide());
        }
        return true;
    }

    // Helper method to update status using repository directly
    private void updateOrderStatus(Long orderId, Order.OrderStatus status) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(status);
            orderRepository.save(order);
        });
    }

    // Helper method to update remaining quantity using repository directly
    private void updateRemainingQuantity(Long orderId, BigDecimal remainingQuantity) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setRemainingQuantity(remainingQuantity);
            orderRepository.save(order);
        });
    }

    private User getUserByOrder(Order order) {
        return order.getUser();
    }

    private Wallet getWalletByOrder(Order order) {
        User user = getUserByOrder(order);
        if (user != null && user.getWallets() != null && !user.getWallets().isEmpty()) {
            return user.getWallets().getFirst();
        }
        return null;
    }
}