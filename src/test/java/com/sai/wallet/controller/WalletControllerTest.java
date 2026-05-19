package com.sai.wallet.controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sai.wallet.dto.OperationType;
import com.sai.wallet.dto.WalletRequest;
import com.sai.wallet.entity.Wallet;
import com.sai.wallet.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void shouldGetWalletBalance() throws Exception {

        mockMvc.perform(get("/api/v1/wallets/" + walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    void shouldDepositAmount() throws Exception {

        WalletRequest request = new WalletRequest();

        request.setWalletId(walletId);
        request.setOperationType(OperationType.DEPOSIT);
        request.setAmount(BigDecimal.valueOf(500));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500));
    }

    @Test
    void shouldReturnNotFoundForInvalidWallet() throws Exception {

        UUID invalidWalletId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/wallets/" + invalidWalletId))
                .andExpect(status().isNotFound());
    }
}