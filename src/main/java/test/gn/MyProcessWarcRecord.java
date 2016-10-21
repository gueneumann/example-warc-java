package test.gn;

import java.io.IOException;
import java.net.MalformedURLException;

import org.commoncrawl.examples.java_warc.IProcessWarcRecord;
import org.xml.sax.SAXException;

/**
 * a sample callback class for handling WARC record data by implementing IProcessWarcRecord interface
 */
public class MyProcessWarcRecord implements IProcessWarcRecord {
	private TextExtractor extractor = new TextExtractor();
	
	private String url = "";
	private String extractedText = "";
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getExtractedText() {
		return extractedText;
	}
	public void setExtractedText(String extractedText) {
		this.extractedText = extractedText;
	}

	@Override
	public void process(String url, String content) {
		// simply extract title and text from content of url and print both.
		try {
			this.url = url;
		    
		    this.extractedText = this.extractor.extractText(content);
		    
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void done() {
		// place any code hear to save data, etc.
	}
}
