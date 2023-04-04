package org.TouchAPI.models;

import edu.stanford.nlp.pipeline.CoreNLPProtos;

public class SentimentResult {
    private int sentimentScore;
    private int wordCount;

    private CoreNLPProtos.Sentiment sentiment;

    // Constructor
    public SentimentResult() {}

    // Getters and setters
    public int getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(int sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }



    public CoreNLPProtos.Sentiment getSentiment() {
        return sentiment;
    }

    public void setSentiment(CoreNLPProtos.Sentiment sentiment) {
        this.sentiment = sentiment;
    }
}
