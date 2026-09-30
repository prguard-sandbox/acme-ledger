package com.acme.ledger;

import java.util.HashMap;
import java.util.Map;

public final class Accounts {
    private final Map<String, Account> byId = new HashMap<>();

    public void add(Account account) {
        byId.put(account.id(), account);
    }

    /** Returns the account, or null when there is none. */
    public Account get(String id) {
        return byId.get(id);
    }
}
