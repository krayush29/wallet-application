package com.springboot.wallet_application.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.springboot.wallet_application.exception.WalletException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double balance;

    @OneToOne(mappedBy = "wallet")
    @JsonBackReference
    private User user;

    public Wallet() {
        this.balance = 0.0;
    }

    public void deposit(double amount) {
        this.balance += amount;
    }

    public void withdraw(double amount) {
        if (this.balance < amount) {
            throw new WalletException("Insufficient balance in wallet for user: " + this.id);
        }

        this.balance -= amount;
    }
}
