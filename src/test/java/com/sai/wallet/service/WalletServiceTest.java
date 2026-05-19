package com.sai.wallet.service;

import com.sai.wallet.dto.OperationType;
import com.sai.wallet.dto.WalletRequest;
import com.sai.wallet.dto.WalletResponse;
import com.sai.wallet.entity.Wallet;
import com.sai.wallet.repository.WalletRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class WalletServiceTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    private UUID walletId;

    @BeforeEach
    void setup() {

        walletRepository.deleteAll();

        Wallet wallet = new Wallet();

        walletId = UUID.randomUUID();

        wallet.setId(walletId);
        wallet.setBalance(BigDecimal.valueOf(1000));

        walletRepository.save(wallet);
    }

    @Test
    void shouldDepositMoney() {

        WalletRequest request = new WalletRequest();

        request.setWalletId(walletId);
        request.setOperationType(OperationType.DEPOSIT);
        request.setAmount(BigDecimal.valueOf(500));

        WalletResponse response = walletService.updateBalance(request);

        assertEquals(BigDecimal.valueOf(1500), response.getBalance());
    }

    @Test
    void shouldWithdrawMoney() {

        WalletRequest request = new WalletRequest();

        request.setWalletId(walletId);
        request.setOperationType(OperationType.WITHDRAW);
        request.setAmount(BigDecimal.valueOf(200));

        WalletResponse response = walletService.updateBalance(request);

        assertEquals(BigDecimal.valueOf(800), response.getBalance());
    }
}