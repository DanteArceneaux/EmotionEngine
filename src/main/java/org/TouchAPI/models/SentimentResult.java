package org.TouchAPI.models;

import edu.stanford.nlp.pipeline.CoreNLPProtos;

public class SentimentResult {
    private CoreNLPProtos.Sentiment sentiment;
    private int sentimentScore;
    private int wordCount;

    public SentimentResult() {

    }

    public SentimentResult(CoreNLPProtos.Sentiment sentiment, int sentimentScore, int wordCount) {
        this.sentiment = sentiment;
        this.sentimentScore = sentimentScore;
        this.wordCount = wordCount;
    }

    public CoreNLPProtos.Sentiment getSentiment() {
        return sentiment;
    }

    public void setSentiment(CoreNLPProtos.Sentiment sentiment) {
        this.sentiment = sentiment;
    }

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
}
