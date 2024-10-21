package com.springboot.wallet_application.repository;

import com.springboot.wallet_application.entity.TransferTransaction;
import com.springboot.wallet_application.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferTransactionRepository extends JpaRepository<TransferTransaction, Long> {
    List<TransferTransaction> findAllByFromUser(User user);

    List<TransferTransaction> findAllByToUser(User user);
}
