package com.HeiseiChain.HeiseiChain.controller;

import com.HeiseiChain.HeiseiChain.model.Block;
import com.HeiseiChain.HeiseiChain.model.Blockchain;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blockchain")
public class BlockchainController {
    private final Blockchain blockchain = new Blockchain();

    @GetMapping
    public Blockchain getBlockchain() {
        return blockchain;
    }

    @PostMapping("/add")
    public String addBlock(@RequestBody String data) {
        Block newBlock = new Block(data, blockchain.getLatestBlock().getHash());
        blockchain.addBlock(newBlock);
        return "Block added!";
    }

    @GetMapping("/validate")
    public boolean isBlockchainValid() {
        return blockchain.isChainValid();
    }
}