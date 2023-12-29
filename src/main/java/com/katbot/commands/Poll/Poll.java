package com.katbot.commands.Poll;

import java.util.ArrayList;

public class Poll {
    private long id;
    private ArrayList<Vote> votes;

    public Poll(long id) {
        this.id = id;
    }

    public void addVote(Vote vote) {
        votes.add(vote);
    }

    @Override
    public int hashCode() {
        return (int) id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Poll poll = (Poll) o;
        return getId() == poll.getId();
    }

    public long getId() {
        return id;
    }

    public ArrayList<Vote> getVotes() {
        return votes;
    }
}
