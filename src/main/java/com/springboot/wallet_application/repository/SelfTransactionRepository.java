package com.springboot.wallet_application.repository;

import com.springboot.wallet_application.entity.SelfTransaction;
import com.springboot.wallet_application.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SelfTransactionRepository extends JpaRepository<SelfTransaction, Long> {
    List<SelfTransaction> findAllByUser(User user);
}
