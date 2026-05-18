package org.database.dao;

public record ResultFilterCriteria(
        String playerNamePrefix,
        Integer minScore,
        String gameName
) {

    public static ResultFilterCriteria empty() {
        return new ResultFilterCriteria(null, null, null);
    }
}
