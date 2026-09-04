package org.example.charm.order.service;

import lombok.RequiredArgsConstructor;
import org.example.charm.order.dto.OrderItemResponse;
import org.example.charm.order.dto.OrderResponse;
import org.example.charm.order.entity.*;
import org.example.charm.order.repository.CartRepository;
import org.example.charm.order.repository.OrderRepository;
import org.example.charm.product.entity.Product;
import org.example.charm.product.entity.ProductVolumePrice;
import org.example.charm.product.repository.ProductRepository;
import org.example.charm.user.entity.User;
import org.example.charm.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    @Transactional
    public OrderResponse createOrderFromCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Корзина не найдена"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Корзина пуста");
        }

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            int requiredVolumeForThisItem = cartItem.getVolumeMl() * cartItem.getQuantity();

            // ЖЕСТКАЯ ПРОВЕРКА ОСТАТКА: Если пока товар лежал в корзине, его раскупили
            if (product.getLeftoverVolumeMl() < requiredVolumeForThisItem) {
                throw new RuntimeException("К сожалению, парфюм " + product.getName()
                        + " в объеме " + requiredVolumeForThisItem + " мл уже закончился. "
                        + "Доступно: " + product.getLeftoverVolumeMl() + " мл.");
            }

            // Атомарно уменьшаем остаток во флаконе
            product.setLeftoverVolumeMl(product.getLeftoverVolumeMl() - requiredVolumeForThisItem);
            productRepository.save(product); // Hibernate зафиксирует update при коммите транзакции

            // Находим актуальную цену за выбранный объем
            ProductVolumePrice priceRule = product.getVolumePrices().stream()
                    .filter(pvp -> pvp.getVolumeMl().equals(cartItem.getVolumeMl()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Цена для объема " + cartItem.getVolumeMl() + " не найдена"));

            BigDecimal itemTotalPrice = priceRule.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotalPrice);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .volumeMl(cartItem.getVolumeMl())
                    .quantity(cartItem.getQuantity())
                    .price(priceRule.getPrice()) // фиксируем цену на момент покупки!
                    .build();

            order.getItems().add(orderItem);
        }

        order.setTotalAmount(totalAmount);

        // Сохраняем заказ в БД. Если что-то упадет, вся транзакция откатится, миллилитры вернутся.
        Order savedOrder = orderRepository.save(order);

        // Очищаем корзину после успешного оформления
        cartService.clearCart(userId);

        return mapToResponse(savedOrder);
    }

    private OrderResponse mapToResponse(Order order) {
        var items = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .volumeMl(item.getVolumeMl())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }
}