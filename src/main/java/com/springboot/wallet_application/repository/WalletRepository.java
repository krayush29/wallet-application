package com.springboot.wallet_application.repository;

import com.springboot.wallet_application.entity.User;
import com.springboot.wallet_application.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUser(User user);
}
