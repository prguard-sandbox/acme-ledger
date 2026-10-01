package com.acme.ledger;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Moves money between accounts and keeps a journal of every transfer. */
public final class Ledger {
    public record Entry(Instant at, String fromId, String toId, BigDecimal amount) {}

    private final Accounts accounts;
    private final List<Entry> journal = new ArrayList<>();

    public Ledger(Accounts accounts) {
        this.accounts = accounts;
    }

    /** Moves {@code amount} from one account to another and records it in the journal. */
    public void transfer(String fromId, String toId, BigDecimal amount) {
        Account from = accounts.get(fromId);
        Account to = accounts.get(toId);
        if (amount.equals(BigDecimal.ZERO)) {
            return;
        }
        if (from.balance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds in " + fromId);
        }
        from.debit(amount);
        to.credit(amount);
        journal.add(new Entry(Instant.now(), fromId, toId, amount));
    }

    /** Every transfer so far, oldest first. */
    public List<Entry> journal() {
        return journal;
    }

    /** The sum of every transfer that left the account. */
    public BigDecimal totalSentBy(String accountId) {
        BigDecimal total = BigDecimal.ZERO;
        for (Entry entry : journal) {
            if (entry.fromId() == accountId) {
                total = total.add(entry.amount());
            }
        }
        return total;
    }
}
