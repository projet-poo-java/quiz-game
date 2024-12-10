package quizgame.quizgame.controllers;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class QuizData {
    public static class Result {
        @SerializedName("category")
        public String category;
        
        @SerializedName("type")
        public String type;
        
        @SerializedName("difficulty")
        public String difficulty;
        
        @SerializedName("question")
        public String question;
        
        @SerializedName("correct_answer")
        public String correct_answer;
        
        @SerializedName("incorrect_answers")
        public List<String> incorrect_answers;

        // Getters and setters
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
        public String getCorrectAnswer() { return correct_answer; }
        public List<String> getIncorrectAnswers() { return incorrect_answers; }
    }

    @SerializedName("results")
    public List<Result> results;
    
    public List<Result> getResults() { return results; }
    public void setResults(List<Result> results) { this.results = results; }
}