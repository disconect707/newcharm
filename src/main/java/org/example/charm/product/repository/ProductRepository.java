package org.example.charm.product.repository;

import jakarta.persistence.LockModeType;
import org.example.charm.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ЭТА МАГИЯ РЕШАЕТ ПРОБЛЕМУ ОДНОВРЕМЕННЫХ ПОКУПОК
    // Когда один юзер вызывает этот метод, база данных "замораживает" эту строку,
    // пока транзакция не завершится. Второй юзер будет ждать своей очереди.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
}