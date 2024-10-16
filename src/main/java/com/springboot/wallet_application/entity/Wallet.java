package com.springboot.wallet_application.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.springboot.wallet_application.enums.CurrencyType;
import com.springboot.wallet_application.exception.WalletException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 10, scale = 2)
    private double balance;

    @Enumerated(EnumType.STRING)
    private CurrencyType currencyType;

    @OneToOne(mappedBy = "wallet")
    @JsonBackReference
    private User user;

    public Wallet() {
        // default values
        this.currencyType = CurrencyType.INR;
        this.balance = 0.0;
    }

    public Wallet(CurrencyType currencyType) {
        this.currencyType = currencyType;
        this.balance = 0.0;
    }

    public void deposit(double amount) {
        this.balance += roundOf(amount);
    }

    public void withdraw(double amount) {
        if (this.balance < amount) {
            throw new WalletException("Insufficient balance in wallet for user: " + this.id);
        }

        this.balance -= roundOf(amount);
    }

    public void transfer(Wallet toWallet, double transferAmount) {
        double amountInRecipientCurrency = this.convert(this.currencyType, toWallet.currencyType, transferAmount);

        this.withdraw(roundOf(transferAmount));
        toWallet.deposit(amountInRecipientCurrency);
    }

    public double convert(CurrencyType fromCurrency, CurrencyType toCurrency, double amount) {
        double baseValue = fromCurrency.toBase(amount);
        return roundOf(toCurrency.fromBase(baseValue));
    }

    private double roundOf(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
