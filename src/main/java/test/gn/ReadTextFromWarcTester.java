package test.gn;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.compressors.CompressorException;

import com.gn.file.Compressor;
import com.gn.text.SimpleSegmentizer;

import edu.cmu.lemurproject.WarcRecord;
import edu.cmu.lemurproject.WarcHTMLResponseRecord;

/**
 * a sample callback class for handling WARC record data by implementing IProcessWarcRecord interface
 * This is basically the same as the one from Mark Watson SampleProcessWarcRecord.java .
 */
public class ReadTextFromWarcTester {
	
	
	public static void main(String[] args) throws IOException, CompressorException {
		
		SimpleSegmentizer segmentizer = new SimpleSegmentizer(false, false);

		int recordCnt = 0;
		
		int mod = 10000;
		// use a callback class for handling WARC record data:
		MyProcessWarcRecord processor = new MyProcessWarcRecord();

		// Local warc file
		String inputWarcFile="/Volumes/data2/cluebweb09-de/0001-78.warc.gz";
		GZIPInputStream gzInputStream=new GZIPInputStream(new FileInputStream(inputWarcFile));
		DataInputStream inStream=new DataInputStream(gzInputStream);
		// Specify local .txt.bz2 text output file
		String outputTextFile = inputWarcFile.split(".warc.gz")[0]+".txt.bz2";
		
		BufferedWriter outStream = Compressor.getBufferedWriterForTextFile(outputTextFile);

		WarcRecord thisWarcRecord;
		
		long time1 = System.currentTimeMillis();
		
		System.out.println("Processing warc file: " + inputWarcFile);
		
		while ((thisWarcRecord=WarcRecord.readNextWarcRecord(inStream))!=null) {
			recordCnt++;
			
			if ((recordCnt % mod) == 0){
				System.out.println("WarcRecords processed: " + recordCnt);
			}
			// outStream.write("%% thisWarcRecord.getHeaderRecordType() = " + thisWarcRecord.getHeaderRecordType() + "\n");
			if (thisWarcRecord.getHeaderRecordType().equals("response")) {
				WarcHTMLResponseRecord htmlRecord=new WarcHTMLResponseRecord(thisWarcRecord);
				String thisTargetURI=htmlRecord.getTargetURI();
				String thisContentUtf8 = htmlRecord.getRawRecord().getContentUTF8();

				// handle WARC record content:
				processor.process(thisTargetURI, thisContentUtf8);
				
				// TODO: segmentize, extract sentences ...
				String extractedString = processor.getExtractedText();
//				segmentizer.reset();
//				segmentizer.scanText(extractedString);
				
				outStream.write(extractedString + "\n");
				
			}
		}
		long time2 = System.currentTimeMillis();
		System.out.println("WarcRecords processed: " + recordCnt);
		System.err.println("System time (msec): " + (time2-time1));
		
		inStream.close();
		outStream.close();
		// done processing all WARC records:
		processor.done();
	}
}
