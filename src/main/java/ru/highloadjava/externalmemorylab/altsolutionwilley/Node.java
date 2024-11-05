package ru.highloadjava.externalmemorylab.altsolutionwilley;

public class Node {
    private int value;
    int rank;
    private Node next;

    public Node(int value) {
        this.value = value;
        this.rank = 1;
        this.next = null;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}
