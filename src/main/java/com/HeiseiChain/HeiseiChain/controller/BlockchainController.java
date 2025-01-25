package com.HeiseiChain.HeiseiChain.controller;

import com.HeiseiChain.HeiseiChain.model.*;
import com.HeiseiChain.HeiseiChain.service.BlockchainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/blockchain")
public class BlockchainController {

    private final BlockchainService blockchainService;
    public BlockchainController(BlockchainService blockchainService) {
        this.blockchainService = blockchainService; // Spring calls the constructor and injects this instance
    }

    @GetMapping("/display")
    public String getBlockchain() {
        try {
            return blockchainService.getBlockchain();
        } catch (Exception e) {
            System.err.println("Error fetching blockchain: " + e.getMessage());
            return "Error";
        }
    }

    @GetMapping("/validate")
    public boolean isBlockchainValid() {
        return blockchainService.isChainValid();
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String role) {
        try {
            Wallet newWallet = blockchainService.createWallet(role);
            // Check if the public key was generated properly
            if (newWallet.publicKey == null) {
                return "Error: Public key generation failed!";
            }
            blockchainService.registerUser(newWallet,username);

            return "User registered successfully!";
        } catch (Exception e) {
            return "Error registering user: " + e.getMessage();
        }
    }

    @PostMapping("/create")
    public String createTransaction(
            @RequestParam String senderUsername,
            @RequestParam String recipientUsername,
            @RequestParam float value,
            @RequestParam String metadata) {
        try {
            // Step 1: Fetch the sender's public key
            PublicKey senderPublicKey = blockchainService.getPublicKeyByUsername(senderUsername);
            if (senderPublicKey == null) {
                return "Error: Sender username '" + senderUsername + "' is not registered!";
            }

            // Step 2: Fetch the recipient's public key
            PublicKey recipientPublicKey = blockchainService.getPublicKeyByUsername(recipientUsername);
            if (recipientPublicKey == null) {
                return "Error: Recipient username '" + recipientUsername + "' is not registered!";
            }

            // Step 3: Fetch the sender's wallet
            Wallet senderWallet = blockchainService.getWalletByUsername(senderUsername);
            if (senderWallet == null || senderWallet.privateKey == null) {
                return "Error: Could not retrieve wallet or private key for sender '" + senderUsername + "'!";
            }

            //System.out.println(senderWallet.privateKey);

            // Step 4: Create inputs (UTXOs) for the transaction
            ArrayList<TransactionInput> inputs = new ArrayList<>();

            // Skip UTXO checks if the metadata is "donation"
            if (!"donation".equals(metadata)) {
                // Fetch UTXOs (unspent transaction outputs) for the sender
                List<UTXO> availableUTXOs = senderWallet.getUTXOs();
                if (availableUTXOs == null || availableUTXOs.isEmpty()) {
                    return "Error: No UTXOs available for sender '" + senderUsername + "'!";
                }

                // Prepare inputs for the transaction
                float totalInputValue = 0;
                for (UTXO utxo : availableUTXOs) {
                    inputs.add(new TransactionInput(utxo.getId()));
                    totalInputValue += utxo.getValue();
                    if (totalInputValue >= value) {
                        break;
                    }
                }

                // Check if the sender has sufficient funds
                if (totalInputValue < value) {
                    return String.format(
                            "Error: Insufficient funds for sender '%s'. Available: %.2f, Required: %.2f.",
                            senderUsername, totalInputValue, value
                    );
                }

            }

            // Step 5: Create the transaction
            Transaction transaction = new Transaction(
                    senderPublicKey,
                    recipientPublicKey,
                    metadata,
                    value,
                    metadata,
                    inputs
            );

            // Step 6: Generate the transaction's signature using the sender's private key
            transaction.generateSignature(senderWallet.privateKey);

            // Step 7: Process the transaction
            boolean success = transaction.processTransaction();
            //System.out.println(success);
            if (success) {
                blockchainService.addTransaction(transaction);

                return "Transaction created successfully! Transaction ID: " + transaction.getTransactionId();
            } else {
                return "Transaction failed during processing!";
            }
        } catch (Exception e) {
            return "Error creating transaction: " + e.getMessage();
        }
    }

}
