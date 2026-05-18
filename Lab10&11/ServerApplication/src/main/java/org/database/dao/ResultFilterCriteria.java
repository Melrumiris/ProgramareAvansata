package org.database.dao;

/**
 * Immutable criteria object for the dynamic Criteria API search in {@link ResultRepository}.
 * All fields are optional — a {@code null} value means "no filter applied".
 *
 * @param playerNamePrefix  filter results where the player's username starts with this value (case-insensitive)
 * @param minScore          filter results where score >= this value
 * @param gameName          filter results belonging to a game with exactly this name (case-insensitive)
 */
public record ResultFilterCriteria(
        String playerNamePrefix,
        Integer minScore,
        String gameName
) {
    /** Convenience factory — all optional args parsed from command tokens. */
    public static ResultFilterCriteria empty() {
        return new ResultFilterCriteria(null, null, null);
    }
}
