package com.katbot.commands.Poll;

import java.util.HashSet;

public class PollManager {
    private HashSet<Poll> polls;

    public PollManager() {
        polls = new HashSet<>();
    }

    public void addPoll(Poll poll) {
        polls.add(poll);
    }

    public boolean addVote(int pollId, Vote vote) {
        for (Poll poll : polls) {
            if (poll.getId() == pollId) {
                if (poll.getVotes().contains(vote)) {
                    return false;
                }
                poll.addVote(vote);
                return true;
            }
        }
        return false;
    }
}
