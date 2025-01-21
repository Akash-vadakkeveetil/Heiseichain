package com.HeiseiChain.HeiseiChain.model;

import java.security.PublicKey;

public class UTXO {
    private String id;      // Unique identifier for the UTXO
    private PublicKey owner; // Public key of the owner of this UTXO
    private float value;    // Value of the UTXO

    // Constructor
    public UTXO(String id, PublicKey owner, float value) {
        this.id = id;
        this.owner = owner;
        this.value = value;
    }

    // Getters
    public String getId() {
        return id;
    }

    public PublicKey getOwner() {
        return owner;
    }

    public float getValue() {
        return value;
    }
}

