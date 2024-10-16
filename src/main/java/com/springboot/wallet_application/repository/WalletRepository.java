package com.springboot.wallet_application.repository;

import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUser(User user);

    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN TRUE ELSE FALSE END FROM Wallet w WHERE w.id = :walletId AND w.user = :user")
    boolean existsByIdAndUser(@Param("walletId") Long walletId, @Param("user") User user);
}
