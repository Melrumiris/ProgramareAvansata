package org.commands;

import org.connections.ClientThread;
import org.database.dao.ResultFilterCriteria;
import org.database.dao.ResultRepository;
import org.database.entity.Result;

import java.util.List;

/**
 * \search [player:<prefix>] [minscore:<n>] [game:<name>]
 *
 * All filters are optional. Example:
 *   \search player:Ali minscore:2 game:trivia
 */
public class SearchCommand implements Command {

    private final ClientThread client;
    private final String args;
    private final ResultRepository resultRepository = new ResultRepository();

    public SearchCommand(ClientThread client, String args) {
        this.client = client;
        this.args = args.trim();
    }

    @Override
    public void exec() {
        if (client.loggedInPlayer == null) {
            client.sendMessage("[Error] You must \\login first.");
            return;
        }

        String playerNamePrefix = null;
        Integer minScore = null;
        String gameName = null;

        for (String token : args.split("\\s+")) {
            if (token.startsWith("player:")) {
                playerNamePrefix = token.substring(7);
            } else if (token.startsWith("minscore:")) {
                try {
                    minScore = Integer.parseInt(token.substring(9));
                } catch (NumberFormatException e) {
                    client.sendMessage("[Error] minscore must be a number.");
                    return;
                }
            } else if (token.startsWith("game:")) {
                gameName = token.substring(5);
            } else if (!token.isEmpty()) {
                client.sendMessage("[Error] Unknown filter '" + token + "'. Usage: \\search [player:<prefix>] [minscore:<n>] [game:<name>]");
                return;
            }
        }

        ResultFilterCriteria criteria = new ResultFilterCriteria(playerNamePrefix, minScore, gameName);
        List<Result> results = resultRepository.search(criteria);

        if (results.isEmpty()) {
            client.sendMessage("No results found matching the given filters.");
            return;
        }

        client.sendMessage(String.format("%-20s %-20s %5s %10s", "Player", "Game", "Score", "Time(ms)"));
        client.sendMessage("-".repeat(60));
        for (Result r : results) {
            client.sendMessage(String.format("%-20s %-20s %5d %10d",
                    r.getPlayer().getUsername(),
                    r.getGame().getName(),
                    r.getScore(),
                    r.getTotalResponseTimeMs()));
        }
    }
}
