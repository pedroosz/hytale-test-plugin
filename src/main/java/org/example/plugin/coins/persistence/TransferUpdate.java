package org.example.plugin.coins.persistence;

public final class TransferUpdate {

    private final BalanceUpdate from;
    private final BalanceUpdate to;

    public TransferUpdate(BalanceUpdate from, BalanceUpdate to) {
        this.from = from;
        this.to = to;
    }

    public BalanceUpdate getFrom() {
        return from;
    }

    public BalanceUpdate getTo() {
        return to;
    }
}
