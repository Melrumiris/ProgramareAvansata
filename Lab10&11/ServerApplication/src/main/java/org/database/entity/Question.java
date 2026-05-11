package org.database.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String text;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_correct_answers", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "answer")
    private List<String> correctAnswers = new ArrayList<>();

    public Question() {}

    public Question(String text, List<String> correctAnswers) {
        this.text = text;
        this.correctAnswers = correctAnswers;
    }

    public UUID getId() { return id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public List<String> getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(List<String> correctAnswers) { this.correctAnswers = correctAnswers; }

    public boolean isCorrect(String answer) {
        if (answer == null) return false;
        return correctAnswers.stream().anyMatch(a -> a.equalsIgnoreCase(answer.trim()));
    }

    @Override
    public String toString() {
        return "Question{id=" + id + ", text='" + text + "', correctAnswers=" + correctAnswers + "}";
    }
}
