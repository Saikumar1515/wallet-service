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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class WalletConcurrencyTest {

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
    void shouldHandleConcurrentWithdrawals() throws Exception {

        int threadCount = 100;

        ExecutorService executorService =
                Executors.newFixedThreadPool(threadCount);

        CountDownLatch latch =
                new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {

            executorService.submit(() -> {

                try {

                    WalletRequest request = new WalletRequest();

                    request.setWalletId(walletId);
                    request.setOperationType(OperationType.WITHDRAW);
                    request.setAmount(BigDecimal.ONE);

                    walletService.updateBalance(request);

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        WalletResponse response =
                walletService.getBalance(walletId);

        assertEquals(
                BigDecimal.valueOf(900),
                response.getBalance()
        );
    }
}