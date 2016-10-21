package test.gn;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.xml.sax.SAXException;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;

/**
 * Demonstrates how to use Boilerpipe, to get the title from the article page.
 * Example basis:
 * http://www.treselle.com/blog/boilerpipe-web-content-extraction-without-boiler-plates/
 * 
 */
public class TextExtractor {

	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	final BoilerpipeExtractor extractor = CommonExtractors.ARTICLE_EXTRACTOR;
	// final BoilerpipeExtractor extractor = CommonExtractors.DEFAULT_EXTRACTOR;
	// final BoilerpipeExtractor extractor = CommonExtractors.CANOLA_EXTRACTOR;
	// final BoilerpipeExtractor extractor = CommonExtractors.LARGEST_CONTENT_EXTRACTOR;

	public String extractTitle(String htmlPageContent) throws SAXException, MalformedURLException, IOException{
		String title = "";
		try {
			// make a new HTMLDocument form the String htmlPageContent determined via 
			// edu.cmu.lemurproject.WarcHTMLResponseRecord.WarcHTMLResponseRecord(WarcRecord)
			final HTMLDocument htmlDoc = new HTMLDocument(htmlPageContent);
			final TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
			title = doc.getTitle();


		} catch (BoilerpipeProcessingException e) {
			logger.severe("Exception thrown during scraping process " + e);
		}
		return title;
	}

	public String extractText(String htmlPageContent) throws SAXException, MalformedURLException, IOException{
		String text = "";
		try {
			// make a new HTMLDocument form the String htmlPageContent determined via 
			// edu.cmu.lemurproject.WarcHTMLResponseRecord.WarcHTMLResponseRecord(WarcRecord)
			final HTMLDocument htmlDoc = new HTMLDocument(htmlPageContent);
			final TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();

			text = extractor.getText(doc);


		} catch (BoilerpipeProcessingException e) {
			logger.severe("Exception thrown during scraping process " + e);
		}
		return text;

	}

	public static void main(String[] args) throws Exception {

		try {

			String urlString = "http://www.dfki.de/~neumann/";
			URL url = new URL(urlString);
			final HTMLDocument htmlDoc = HTMLFetcher.fetch(url);
			final TextDocument doc = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
			logger.info("Page title: " + doc.getTitle());

		} catch (MalformedURLException e) {
			logger.severe("Exception thrown for invalid url " + e);
		} catch (BoilerpipeProcessingException e) {
			logger.severe("Exception thrown during scraping process " + e);
		} catch (IOException e) {
			logger.severe("Exception thrown" + e);
		}

	}
}
