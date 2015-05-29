package com.alaimos.other;

public class Pair<U, V> {

    public U first;
    public V second;

    public Pair(U first, V second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "{" + first + ", " + second + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair pair = (Pair) o;
        return first.equals(pair.first) && second.equals(pair.second);
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }
}
