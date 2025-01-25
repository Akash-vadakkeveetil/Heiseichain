package com.HeiseiChain.HeiseiChain.service;

import com.HeiseiChain.HeiseiChain.model.*;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class BlockchainService {

    private Blockchain blockchain;
    private Map<String, Wallet> walletDatabase = new HashMap<>();

    public BlockchainService() {
        this.blockchain = new Blockchain();
    }

    public String getBlockchain() {
        return blockchain.displayHTML(walletDatabase);
    }

    public void addTransaction(Transaction transaction) {
        blockchain.addTransaction(transaction);
    }

    public boolean isChainValid() {
        return blockchain.isChainValid();
    }

    public Wallet createWallet(String role) {
        Wallet wallet = new Wallet(role);  // Create a wallet with the specified role
        return wallet;
    }

    // Registers the user by adding a registration transaction
    public void registerUser(Wallet wallet, String username) {
        RegistrationTransaction registrationTransaction = new RegistrationTransaction(wallet.publicKey, username);
        blockchain.addTransaction(registrationTransaction);
        walletDatabase.put(username, wallet);
    }

    public PublicKey getPublicKeyByUsername(String username) {
        for (Block block : blockchain.getChain()) {
            for (Transaction transaction : block.getTransactions()) {
                if (transaction instanceof RegistrationTransaction) {
                    RegistrationTransaction regTransaction = (RegistrationTransaction) transaction;
                    if (regTransaction.getMetadata().contains(username)) {
                        return regTransaction.getSender(); // Registered public key
                    }
                }
            }
        }
        return null; // User not found
    }

    public Wallet getWalletByUsername(String username) {
        return walletDatabase.get(username); // Retrieve wallet by username
    }

    public String generateReport(long startTimestamp, long endTimestamp) {
        StringBuilder report = new StringBuilder();
        report.append("Blockchain Report\n");
        report.append("Date Range: ")
                .append(new Date(startTimestamp)).append(" to ")
                .append(new Date(endTimestamp)).append("\n\n");

        for (Block block : blockchain.getChain()) {
            if (block.getTimestamp() >= startTimestamp && block.getTimestamp() <= endTimestamp) {
                report.append("Block Number: ").append(blockchain.getChain().indexOf(block)).append("\n");
                report.append("Hash: ").append(block.getHash()).append("\n");
                report.append("Previous Hash: ").append(block.getPreviousHash()).append("\n");
                report.append("Timestamp: ").append(block.getFormattedTimestamp()).append("\n");
                report.append("Transactions:\n");

                for (Transaction transaction : block.getTransactions()) {
                    report.append("\tSender: ").append(transaction.getSender()).append("\n");
                    report.append("\tRecipient: ").append(transaction.getRecipient()).append("\n");
                    report.append("\tValue: ").append(transaction.getValue()).append("\n");
                    report.append("\tMetadata: ").append(transaction.getMetadata()).append("\n");
                }

                report.append("\n");
            }
        }

        return report.toString();
    }



}
