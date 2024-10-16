package com.springboot.wallet_application.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.springboot.wallet_application.enums.CurrencyType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false)
    @Size(min = 5, message = "Username should have at least 5 characters")
    private String username;

    @Column(nullable = false)
    @Size(min = 5, message = "Password should have at least 5 characters")
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "wallet_id", referencedColumnName = "id", nullable = false)
    @JsonManagedReference
    private Wallet wallet;

    public User() {
        this.wallet = new Wallet();
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.wallet = new Wallet();
    }

    public User(String username, String password, CurrencyType currencyType) {
        this.username = username;
        this.password = password;
        if (currencyType == null) currencyType = CurrencyType.INR;

        this.wallet = new Wallet(currencyType);
    }
}
