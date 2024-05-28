package com.xcs.spring;

public class Scores {

    private long id;

    private long score;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Scores{" +
                "id=" + id +
                ", score=" + score +
                '}';
    }
}
