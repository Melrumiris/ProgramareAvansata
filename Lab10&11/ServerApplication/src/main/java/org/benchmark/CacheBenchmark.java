package org.benchmark;

import org.database.JPAUtil;
import org.database.dao.QuestionRepository;
import org.database.entity.Question;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * JMH benchmark comparing cold (cache-bypass) vs. warm (second-level cache) reads.
 *
 * Run via the fat jar:
 *   java -jar target/benchmarks.jar CacheBenchmark
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class CacheBenchmark {

    private QuestionRepository questionRepository;

    @Setup(Level.Trial)
    public void setup() {
        // Trigger EMF initialisation (Caffeine cache is created here too)
        questionRepository = new QuestionRepository();
        // Prime the second-level cache with one fetch before measurement begins
        questionRepository.findAll();
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        JPAUtil.close();
    }

    /**
     * Reads all questions; subsequent iterations are served from the second-level cache.
     */
    @Benchmark
    public List<Question> findAllQuestions_cached() {
        return questionRepository.findAll();
    }

    /**
     * Reads all questions with the second-level cache evicted beforehand.
     * This simulates a cold read on every iteration.
     */
    @Benchmark
    public List<Question> findAllQuestions_coldEvict() {
        JPAUtil.getEntityManagerFactory().getCache().evictAll();
        return questionRepository.findAll();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(CacheBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
