package org.example.charm.order.service;

import lombok.RequiredArgsConstructor;
import org.example.charm.order.dto.CartItemRequest;
import org.example.charm.order.dto.CartItemResponse;
import org.example.charm.order.dto.CartResponse;
import org.example.charm.order.entity.Cart;
import org.example.charm.order.entity.CartItem;
import org.example.charm.order.repository.CartRepository;
import org.example.charm.product.entity.Product;
import org.example.charm.product.entity.ProductVolumePrice;
import org.example.charm.product.repository.ProductRepository;
import org.example.charm.user.entity.User;
import org.example.charm.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public CartResponse addItemToCart(Long userId, CartItemRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        // Проверяем, существует ли цена для выбранного объема распива
        boolean isValidVolume = product.getVolumePrices().stream()
                .anyMatch(pvp -> pvp.getVolumeMl().equals(request.getVolumeMl()));

        if (!isValidVolume) {
            throw new RuntimeException("Объем " + request.getVolumeMl() + " мл недоступен для распива этого аромата");
        }

        // Предварительная проверка остатка во флаконе
        int requestedTotalVolume = request.getVolumeMl() * request.getQuantity();
        if (product.getLeftoverVolumeMl() < requestedTotalVolume) {
            throw new RuntimeException("Недостаточно мл во флаконе. Доступно: " + product.getLeftoverVolumeMl() + " мл");
        }

        // Получаем или создаем корзину для пользователя
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = Cart.builder().user(user).build();
            return cartRepository.save(newCart);
        });

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()) &&
                        item.getVolumeMl().equals(request.getVolumeMl()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + request.getQuantity();

            // Снова проверяем общий запрашиваемый объем
            int newRequestedTotalVolume = request.getVolumeMl() * newQuantity;
            if (product.getLeftoverVolumeMl() < newRequestedTotalVolume) {
                throw new RuntimeException("Недостаточно мл во флаконе для увеличения количества. Доступно: " + product.getLeftoverVolumeMl() + " мл");
            }
            existingItem.setQuantity(newQuantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .volumeMl(request.getVolumeMl())
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);
        return getCart(userId);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Корзина не найдена"));

        BigDecimal totalAmount = BigDecimal.ZERO;
        var itemResponses = cart.getItems().stream().map(item -> {
            Product product = item.getProduct();
            BigDecimal price = product.getVolumePrices().stream()
                    .filter(p -> p.getVolumeMl().equals(item.getVolumeMl()))
                    .findFirst()
                    .map(ProductVolumePrice::getPrice)
                    .orElse(BigDecimal.ZERO);

            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));

            return CartItemResponse.builder()
                    .id(item.getId())
                    .productId(product.getId())
                    .productName(product.getName())
                    .volumeMl(item.getVolumeMl())
                    .quantity(item.getQuantity())
                    .itemPrice(price)
                    .totalPrice(itemTotal)
                    .build();
        }).collect(Collectors.toList());

        for (CartItemResponse item : itemResponses) {
            totalAmount = totalAmount.add(item.getTotalPrice());
        }

        return CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .totalAmount(totalAmount)
                .build();
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Корзина не найдена"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}