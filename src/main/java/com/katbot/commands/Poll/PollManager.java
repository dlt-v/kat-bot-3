package com.katbot.commands.Poll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class PollManager {
    private HashMap<Long, Poll> polls;
    private static PollManager instance;
    private static final Logger logger = LoggerFactory.getLogger(PollManager.class);

    private PollManager() {
        polls = new HashMap<>();
    }
    public static PollManager getInstance() {
        if (instance == null) {
            instance = new PollManager();
        }
        return instance;
    }

    public void addPoll(Poll poll) {
        polls.put(poll.getId(), poll);
    }

    public Poll getPoll(long id) {
        return polls.get(id);
    }

    public boolean addVote(long pollId, Vote vote) {
        Poll poll = getPoll(pollId);
        if (poll == null) {
            logger.error("Vote was not added. This poll is not cached anymore in memory.");
            return false;
        }
        if (poll.containsVote(vote.userId())) {
            logger.error("Vote was not added. User with id: " + vote.userId() + " already voted in this poll.");
            return false;
        }
        poll.addVote(vote);
        return true;
    }
}
