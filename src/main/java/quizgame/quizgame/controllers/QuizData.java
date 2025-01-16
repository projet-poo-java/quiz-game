package quizgame.quizgame.controllers;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class QuizData {
    static {
        // Disable SSL certificate validation
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
            (hostname, session) -> true);
        
        try {
            javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[] {
                new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
            };

            javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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