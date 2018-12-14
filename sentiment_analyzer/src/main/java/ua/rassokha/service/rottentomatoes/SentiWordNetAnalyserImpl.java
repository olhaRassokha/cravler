package ua.rassokha.service.rottentomatoes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.rassokha.domain.Review;
import ua.rassokha.domain.Sentiment;
import ua.rassokha.service.SentiWordNetAnalyser;
import ua.rassokha.util.Configuration;
import ua.rassokha.util.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The SentiWordNetAnalyserImpl program implements an application that,
 * analyzes the text and score the words from the text forming the
 * general score of the vhole text.
 *
 * @author Olha Rassokha
 * @version 1.0
 * @since 2018-04-25
 */
public class SentiWordNetAnalyserImpl implements SentiWordNetAnalyser {
    private static final Logger logger = LogManager.getLogger();
    private static Map<String, Double> dictionary;
    private static Set<String> stopWords;

    static {
        dictionary = new HashMap<>();
        stopWords = new TreeSet<>();
        String[] current;
        String word;
        Double weith;
        try (Scanner scanner = new Scanner(new File(Configuration.getConfiguration().getFileLocation().getDICTIONARY()));
             Scanner stopWordsScanner = new Scanner(new File(Configuration.getConfiguration().getFileLocation().getSTOP_WORDS()))) {
            while (stopWordsScanner.hasNext()) {
                stopWords.add(stopWordsScanner.nextLine().toLowerCase());
            }
            while (scanner.hasNext()) {
                current = scanner.nextLine().split("\t");
                word = current[2];
                weith = Double.parseDouble(current[0]) - Double.parseDouble(current[1]);
                dictionary.put(word, weith);
            }
        } catch (FileNotFoundException e) {
            logger.error("Can not read file");
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * This method is used to conpute the sentiment score of the word.
     *
     * @param word This is the word, needs to be analyzed
     * @return double This returns the score of the word.
     */
    private double analyseWord(String word) {
        double score;
        try {
            score = dictionary.get(word);
        } catch (NullPointerException e) {
            score = 0.0; //if the word is not found in dictionary the score will be equals 0.0
        }
        return score;
    }

    /**
     * This method is used to compute the sentiment score of the text.
     *
     * @param text This is the text, needs to be analyzed
     * @return double This returns the general score of the text.
     */
    @Override
    public double analyseText(String text) {
        String[] tokinizedText = text.replaceAll("\\.", "").toLowerCase().split(" ");
        List<Double> scores = Arrays.stream(tokinizedText)
                .filter(word -> !stopWords.contains(word))
                .map(this::analyseWord).collect(Collectors.toList());
        double sum = 0;
        for (int i = 0; i < scores.size(); i++) {
            sum += scores.get(i) * i / scores.size();
        }
        return sum;
    }

    /**
     * This method is used to compute the sentiment score of the text and  represents it as Sentiment.
     *
     * @param text This is the text, needs to be analyzed
     * @return Sentiment This returns the Sentiment associating with it's score.
     */
    @Override
    public Sentiment analyzereviewText(String text) {
        double score = new SentiWordNetAnalyserImpl().analyseText(text);
        if (score < Constants.BAD_STEP) {
            return Sentiment.BAD;
        }
        if (score < Constants.NORMAL_STEP) {
            return Sentiment.NEUTRAL;
        }
        return Sentiment.GOOD;
    }

    /**
     * This method is used to compute the sentiment score of the text and  represents it as Sentiment.
     *
     * @param reviews This List<Review>, containing reviews, needs to be analyzed
     * @return List<Review> with setted sentiment field.
     */
    @Override
    public List<Review> analyzeReviews(List<Review> reviews) {
        for (Review review : reviews) {
            review.setSentiment(analyzereviewText(review.getReviewText()));
        }
        return reviews;
    }
}
