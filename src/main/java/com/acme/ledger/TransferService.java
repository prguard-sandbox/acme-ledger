package com.acme.ledger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class TransferService {
    private static final int FEE_BPS = 25;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final Accounts accounts;

    public TransferService(Accounts accounts) {
        this.accounts = accounts;
    }

    /** Moves amount from one account to another and returns the fee charged, in cents. */
    public int transfer(String fromId, String toId, BigDecimal amount) {
        if (amount.equals(BigDecimal.ZERO)) {
            throw new IllegalArgumentException("Nothing to transfer");
        }
        Account from = accounts.get(fromId);
        Account to = accounts.get(toId);
        if (from == null) {
            throw new IllegalArgumentException("Unknown account " + fromId);
        }
        int amountCents = amount.multiply(HUNDRED).intValue();
        int feeCents = amountCents * FEE_BPS / 10_000;
        from.debit(amount);
        to.credit(amount);
        return feeCents;
    }

    /** Reads transfers from a CSV file: from,to,amount per line. */
    public List<String[]> readBatch(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        List<String[]> rows = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            rows.add(line.split(","));
        }
        return rows;
    }
}
