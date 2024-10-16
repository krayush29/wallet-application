package com.springboot.wallet_application.repository;

import com.springboot.wallet_application.entity.Transaction;
import com.springboot.wallet_application.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByFromUser(User fromUser);
}
