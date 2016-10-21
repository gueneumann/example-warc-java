package test.gn;

import java.io.IOException;
import java.net.MalformedURLException;

import org.commoncrawl.examples.java_warc.IProcessWarcRecord;
import org.xml.sax.SAXException;

/**
 * a sample callback class for handling WARC record data by implementing IProcessWarcRecord interface
 */
public class MyProcessWarcRecord implements IProcessWarcRecord {
	private TextExtractorDemo extractor = new TextExtractorDemo();
	
	@Override
	public void process(String url, String content) {
		// simply extract title from content of url and print both.
		try {
			String title = this.extractor.extractTitle(content);
			System.out.println("title: " + title + "\n");
		    
		    String text = this.extractor.extractText(content);
		    System.out.println("text: " + text + "\n");
		    System.out.println("--------------------------------\n");
		    
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
