package es.ucm.fdi.iw.model;

import java.util.*;

import es.ucm.fdi.iw.controller.DTOs.GameSetupDTO;
import es.ucm.fdi.iw.controller.DTOs.QuestionDataPrivateDTO;

// this is for everyone to have the same session of questions

public class MultiplayerGameSession {

    private String code;

    private GameSetupDTO setup;

    private List<QuestionDataPrivateDTO> questions;

    private List<User> players = new ArrayList<>();

    private Map<Long, Integer> scores = new HashMap<>();
    private int currentQuestionIndex = 0;
    private int currentPlayerIndex = 0;
    private boolean finished = false;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public GameSetupDTO getSetup() {
        return setup;
    }

    public void setSetup(GameSetupDTO setup) {
        this.setup = setup;
    }

    public List<QuestionDataPrivateDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDataPrivateDTO> questions) {
        this.questions = questions;
    }

    public List<User> getPlayers() {
        return players;
    }

    public Map<Long, Integer> getScores() {
        return scores;
    }
    public QuestionDataPrivateDTO getCurrentQuestion() {
    if (questions == null || questions.isEmpty()) return null;
        return questions.get(currentQuestionIndex);
    }

    public User getCurrentPlayer() {
        if (players.isEmpty()) return null;
        return players.get(currentPlayerIndex);
    }

    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public void nextQuestion() {
        currentQuestionIndex++;

        if (currentQuestionIndex >= questions.size()) {
            finished = true;
        }
    }
}