package org.commands;

import org.connections.ClientThread;
import org.database.dao.QuestionRepository;
import org.database.entity.Question;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GetVocabularyCommand implements Command {

    private final ClientThread client;
    private final QuestionRepository questionRepository = new QuestionRepository();

    public GetVocabularyCommand(ClientThread client) {
        this.client = client;
    }

    @Override
    public void exec() {
        List<Question> questions = questionRepository.findAll();

        // Flatten all correctAnswers lists into one distinct ordered set
        Set<String> distinctAnswers = new LinkedHashSet<>();
        for (Question q : questions) {
            distinctAnswers.addAll(q.getCorrectAnswers());
        }

        client.sendMessage("VOCABULARY:[" + String.join(", ", distinctAnswers) + "]");
    }
}
