package mrnavastar.nocoin.util;

public record Balance(int balance) implements Comparable<Balance> {

    @Override
    public int compareTo(Balance account) {
        return this.balance - account.balance;
    }
}