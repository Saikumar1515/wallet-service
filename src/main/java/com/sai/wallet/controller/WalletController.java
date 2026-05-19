package com.sai.wallet.controller;

import com.sai.wallet.dto.*;
import com.sai.wallet.service.WalletService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/wallet")
    public WalletResponse updateWallet(
            @Valid @RequestBody WalletRequest request
    ) {
        return walletService.updateBalance(request);
    }

    @GetMapping("/wallets/{walletId}")
    public WalletResponse getBalance(
            @PathVariable UUID walletId
    ) {
        return walletService.getBalance(walletId);
    }
}