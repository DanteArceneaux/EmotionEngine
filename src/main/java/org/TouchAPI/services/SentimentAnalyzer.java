package org.TouchAPI.services;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreNLPProtos;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.TouchAPI.exceptions.RateLimitException;
import org.TouchAPI.models.SentimentRequest;
import org.TouchAPI.models.SentimentResult;
import org.TouchAPI.services.rateLimiting.RateLimiter;
import org.TouchAPI.utils.StopWords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SentimentAnalyzer {

    @Autowired
    private RateLimiter rateLimiter;

    private static final Logger logger = Logger.getLogger(SentimentAnalyzer.class.getName());

    private static Map<String, Integer> wordList = new HashMap<String, Integer>();
    private static Set<String> stopWords = null;

    static {
        // Load word list and stop words from files
        try {
            loadWordList("my_word_list.txt");
            stopWords = StopWords.loadStopWords("stopwords.txt");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading resources.", e);
        }
    }

    public SentimentResult analyzeSentiment(@Valid @NotEmpty @Size(max = 500) String text, HttpServletRequest request) throws RateLimitException, RateLimiter.RateLimitExceededException {
        SentimentRequest sentimentRequest = new SentimentRequest(text, request);
        rateLimiter.checkRateLimits(request);
        return analyzeSentiment(sentimentRequest);
    }

    private SentimentResult analyzeSentiment(SentimentRequest request) {
        SentimentResult result = new SentimentResult();
        String text = request.getText();

        if (text == null || text.isEmpty()) {
            result.setSentiment(CoreNLPProtos.Sentiment.NEUTRAL);
            return result;
        }

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, parse, sentiment");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        int sentimentScore = 0;
        int numOfSentences = 0;
        int wordCount = 0;


        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            sentimentScore += sentimentToScore(sentiment);
            numOfSentences++;

            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            for (CoreLabel token : tokens) {
                String word = token.get(CoreAnnotations.TextAnnotation.class).toLowerCase();
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

                if (!isPunctuation(pos) && !isStopword(word)) {
                    wordCount++;

                    if (wordList.containsKey(word)) {
//                        emotionalWordCount++;
                        int score = wordList.get(word);
                        if (score > 0) {
//                            positiveWordCount++;
                            sentimentScore += score;

                        } else if (score < 0) {
//                            negativeWordCount++;
                            sentimentScore += score;
                        }
                    }
                }
            }
        }

        if (numOfSentences > 0) {
            double sentimentAvg = (double) sentimentScore / numOfSentences;
            result.setSentiment(scoreToSentiment(sentimentAvg));
        } else {
            result.setSentiment(CoreNLPProtos.Sentiment.NEUTRAL);
        }

        result.setSentimentScore(sentimentScore);
        result.setWordCount(wordCount);


        logger.info("Analyzed sentiment for text: " + text);

        return result;
    }

    private boolean isPunctuation(String pos) {
        return pos.equals(".") || pos.equals(",") || pos.equals(":") || pos.equals(";") || pos.equals("!") || pos.equals("?");
    }

    private boolean isStopword(String word) {
        return stopWords.contains(word);
    }

    private int sentimentToScore(String sentiment) {
        switch (sentiment) {
            case "Very negative":
                return -2;
            case "Negative":
                return -1;
            case "Neutral":
                return 0;
            case "Positive":
                return 1;
            case "Very positive":
                return 2;
            default:
                return 0;
        }
    }

    private CoreNLPProtos.Sentiment scoreToSentiment(double score) {
        if (score <= -1.0) {
            return CoreNLPProtos.Sentiment.STRONG_NEGATIVE;
        } else if (score <= -0.5) {
            return CoreNLPProtos.Sentiment.WEAK_NEGATIVE;
        } else if (score <= 0.5) {
            return CoreNLPProtos.Sentiment.NEUTRAL;
        } else if (score <= 1.0) {
            return CoreNLPProtos.Sentiment.WEAK_POSITIVE;
        } else {
            return CoreNLPProtos.Sentiment.STRONG_POSITIVE;
        }
    }



    private static void loadWordList(String fileName) throws IOException {
        InputStream is = SentimentAnalyzer.class.getClassLoader().getResourceAsStream(fileName);
        Scanner scanner = new Scanner(is);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\t");
            if (parts.length >= 2) {
                String word = parts[0];
                int score = Integer.parseInt(parts[1]);
                wordList.put(word, score);
            }
        }
        scanner.close();
    }
}