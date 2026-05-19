package com.sai.wallet.service;


import com.sai.wallet.dto.*;
import com.sai.wallet.entity.Wallet;
import com.sai.wallet.dto.OperationType;
import com.sai.wallet.exception.*;
import com.sai.wallet.repository.WalletRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    @Transactional
    public WalletResponse updateBalance(WalletRequest request) {

        Wallet wallet = walletRepository.findByIdForUpdate(request.getWalletId())
                .orElseThrow(() ->
                        new WalletNotFoundException("Wallet not found"));

        if (request.getOperationType() == OperationType.DEPOSIT) {

            wallet.setBalance(
                    wallet.getBalance().add(request.getAmount())
            );

        } else {

            if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientFundsException("Insufficient funds");
            }

            wallet.setBalance(
                    wallet.getBalance().subtract(request.getAmount())
            );
        }

        walletRepository.save(wallet);

        return WalletResponse.builder()
                .walletId(wallet.getId())
                .balance(wallet.getBalance())
                .message("Operation successful")
                .build();
    }

    public WalletResponse getBalance(UUID walletId) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() ->
                        new WalletNotFoundException("Wallet not found"));

        return WalletResponse.builder()
                .walletId(wallet.getId())
                .balance(wallet.getBalance())
                .message("Balance fetched successfully")
                .build();
    }
}