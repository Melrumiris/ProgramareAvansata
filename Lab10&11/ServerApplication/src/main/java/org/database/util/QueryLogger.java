package org.database.util;

import org.slf4j.Logger;

import java.util.function.Supplier;

/**
 * Utility for timing JPQL query execution and logging results / exceptions.
 */
public final class QueryLogger {

    private QueryLogger() {}

    /**
     * Executes {@code query}, logs its execution time, and returns its result.
     * Any {@link RuntimeException} is logged at ERROR level before being re-thrown.
     *
     * @param log       the caller's SLF4J logger
     * @param queryName human-readable description of the JPQL operation
     * @param query     the actual JPA call to time
     * @param <T>       return type of the query
     * @return whatever the query returns
     */
    public static <T> T timed(Logger log, String queryName, Supplier<T> query) {
        long start = System.currentTimeMillis();
        try {
            T result = query.get();
            long elapsed = System.currentTimeMillis() - start;
            log.info("[JPQL] {} executed in {}ms", queryName, elapsed);
            return result;
        } catch (RuntimeException e) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("[JPQL] {} failed after {}ms: {}", queryName, elapsed, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Variant for void operations (persist, executeUpdate, etc.).
     */
    public static void timedVoid(Logger log, String queryName, Runnable action) {
        long start = System.currentTimeMillis();
        try {
            action.run();
            long elapsed = System.currentTimeMillis() - start;
            log.info("[JPQL] {} executed in {}ms", queryName, elapsed);
        } catch (RuntimeException e) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("[JPQL] {} failed after {}ms: {}", queryName, elapsed, e.getMessage(), e);
            throw e;
        }
    }
}
