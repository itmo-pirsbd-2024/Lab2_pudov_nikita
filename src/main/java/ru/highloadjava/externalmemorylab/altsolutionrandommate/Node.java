package ru.highloadjava.externalmemorylab.altsolutionrandommate;

public class Node {
    private final int id;
    private int rank;
    private Integer next;
    private boolean active;
    private String sex;
    private Integer time;

    public Node(int id, Integer next) {
        this.id = id;
        this.next = next;
        this.rank = 1;
        this.active = true;
    }

    public int getId() {
        return id;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Integer getNext() {
        return next;
    }

    public void setNext(Integer next) {
        this.next = next;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public void incrementRank(int additionalRank) {
        this.rank += additionalRank;
    }
}
