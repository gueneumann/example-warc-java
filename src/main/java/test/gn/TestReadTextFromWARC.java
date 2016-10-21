package test.gn;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.commoncrawl.examples.java_warc.IProcessWarcRecord;

import edu.cmu.lemurproject.WarcRecord;
import edu.cmu.lemurproject.WarcHTMLResponseRecord;

/**
 * a sample callback class for handling WARC record data by implementing IProcessWarcRecord interface
 * This is basically the same as the one from Mark Watson SampleProcessWarcRecord.java .
 */
public class TestReadTextFromWARC {

	public static void main(String[] args) throws IOException {

		// use a callback class for handling WARC record data:
		IProcessWarcRecord processor = new MyProcessWarcRecord();

		// Local warc file
		String inputWarcFile="/Volumes/data2/cluebweb09-de/0001-78.warc.gz";
		GZIPInputStream gzInputStream=new GZIPInputStream(new FileInputStream(inputWarcFile));
		DataInputStream inStream=new DataInputStream(gzInputStream);

		WarcRecord thisWarcRecord;
		while ((thisWarcRecord=WarcRecord.readNextWarcRecord(inStream))!=null) {
			System.out.println("%% thisWarcRecord.getHeaderRecordType() = " + thisWarcRecord.getHeaderRecordType());
			if (thisWarcRecord.getHeaderRecordType().equals("response")) {
				WarcHTMLResponseRecord htmlRecord=new WarcHTMLResponseRecord(thisWarcRecord);
				String thisTargetURI=htmlRecord.getTargetURI();
				String thisContentUtf8 = htmlRecord.getRawRecord().getContentUTF8();

				// handle WARC record content:
				processor.process(thisTargetURI, thisContentUtf8);
			}
		}
		inStream.close();
		// done processing all WARC records:
		processor.done();
	}
}
