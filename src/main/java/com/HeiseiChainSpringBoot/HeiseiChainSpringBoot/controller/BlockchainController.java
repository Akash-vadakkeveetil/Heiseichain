package com.HeiseiChainSpringBoot.HeiseiChainSpringBoot.controller;

import com.HeiseiChainSpringBoot.HeiseiChainSpringBoot.model.Block;
import com.HeiseiChainSpringBoot.HeiseiChainSpringBoot.model.Blockchain;
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
