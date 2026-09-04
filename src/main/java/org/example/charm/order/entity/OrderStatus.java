package org.example.charm.order.entity;

public enum OrderStatus {
    CREATED,    // Заказ только что создан из корзины
    PAID,       // Оплата прошла успешно
    SHIPPED,    // Заказ передан в доставку (или готов к самовывозу)
    DELIVERED,  // Заказ получен клиентом
    CANCELLED   // Заказ отменен
}