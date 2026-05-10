package com.game.Ticket_To_Flight.Utilities;

public class ClosedRange<T extends Comparable<T>> {
    public T from;
    public T to;

    private ClosedRange() { this.from = null; this.to = null; }

    public ClosedRange(T from, T to) {
        if (from != null && to != null && from.compareTo(to) > 0) throw new IllegalArgumentException("from > to");
        this.from = from;
        this.to = to;
    }

    public ClosedRange(ClosedRange<T> other){
        from = other.getFrom();
        to = other.getTo();
    }

    public boolean contains(T value) {
        if(from == null && to == null)
            return true;
        if(from == null)
            return value.compareTo(to) <=0;
        if(to == null)
            return value.compareTo(from) >= 0;
        return value.compareTo(from) >= 0 && value.compareTo(to) <= 0;
    }

    public T getFrom() { return from; }
    public T getTo() { return to; }
}
