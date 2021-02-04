package com.bank.rest.data;

public enum TransactionType {
    CREDIT("Credit Operation"), DEBIT("Debit Operation");

    public final String label;

    TransactionType(String label) {
        this.label = label;
    }
}
