package com.game.Ticket_To_Flight.Utilities;

public class Identifiable {
    public final int id;

    protected Identifiable(int id){this.id = id;}

    public int getId() {
    return id;
    }
    @Override
    public int hashCode(){
        return Integer.hashCode(id);
    }
    @Override
    public boolean equals(Object other){
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Identifiable that = (Identifiable) other;
        return id == that.id;
    }
}
