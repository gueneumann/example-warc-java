package test.gn;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
 
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

/**
 * Demonstrates how to use Boilerpipe, to get the main content from the website.
 * Example from:
 * http://www.treselle.com/blog/boilerpipe-web-content-extraction-without-boiler-plates/
 * 
 */
class ArticleExtractorDemo {
 
    private final static Logger LOGGER      = Logger.getLogger(ArticleExtractorDemo.class.getName());
 
    private static String       output_file = "/Volumes/data2/cluebweb09-de/main-content.html";
 
    public static void main(String[] args) throws Exception {
 
        try {
 
            String urlString = "http://www.faz.net/aktuell/wirtschaft/";
            PrintWriter writer = new PrintWriter(output_file);
            URL url = new URL(urlString);
            String article = ArticleExtractor.INSTANCE.getText(url);
            writer.println(article);
            LOGGER.info("Content extracted successfully ");
            writer.close();
 
        } catch (MalformedURLException e) {
            LOGGER.severe("Exception thrown for invalid url " + e);
        } catch (BoilerpipeProcessingException e) {
            LOGGER.severe("Exception thrown during scraping process " + e);
        } catch (IOException e) {
            LOGGER.severe("Exception thrown" + e);
        }
 
    }
}
