package test.gn;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
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
	SimpleSegmentizer segmentizer = new SimpleSegmentizer(false, false);
	GarbageFilter filter = new GarbageFilter();

	private void extractTextFromWarcStreams(DataInputStream inStream, BufferedWriter outStream) throws IOException {

		int recordCnt = 0;
		int mod = 10000;

		// use a callback class for handling WARC record data:
		MyProcessWarcRecord processor = new MyProcessWarcRecord();
		WarcRecord thisWarcRecord;

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
				String extractedString = processor.getExtractedText();

				// Segmentize: tokenization and sentence boundary recognition using morphix-reader style strategy :-)

				segmentizer.reset();
				segmentizer.scanText(extractedString);

				// for each extracted sentence (a token list) apply garbage filter 
				// and eventually write it to output stream
				for (List<String> sentence : segmentizer.getSentenceList()){
					String outputString = segmentizer.tokenListToString(sentence);

					if (!filter.isGarbageString(outputString)){
						outStream.write(outputString + "\n");
					}
				}
			}
		}
		System.out.println("WarcRecords processed: " + recordCnt);

		inStream.close();
		outStream.close();
		// done processing all WARC records:
		processor.done();
	}

	public void extractTextFromWarc(File inputWarcFile) throws IOException, CompressorException {

		// Local warc file
		GZIPInputStream gzInputStream=new GZIPInputStream(new FileInputStream(inputWarcFile));
		// Specify local .txt.bz2 text output file
		String outputTextFile = inputWarcFile.getAbsolutePath().split(".warc.gz")[0]+".txt.bz2";
		
		System.out.println("Processing warc file: " + inputWarcFile);
		System.out.println("To text file:         " + outputTextFile);
		
		// Make the streams
		DataInputStream inStream=new DataInputStream(gzInputStream);
		BufferedWriter outStream = Compressor.getBufferedWriterForTextFile(outputTextFile);

		long time1 = System.currentTimeMillis();

		// Extract and save the text parts of the Warc file
		this.extractTextFromWarcStreams(inStream, outStream);

		long time2 = System.currentTimeMillis();
		System.err.println("System time (msec): " + (time2-time1));

	}

	public static void main(String[] args) throws IOException, CompressorException {

		ReadTextFromWarcTester extractor = new ReadTextFromWarcTester();

		File localWarcDir = new File("/Users/gune00/data/cluebweb09-de/");
		File[] listOfFiles = localWarcDir.listFiles();

		for (File localWarcFile : listOfFiles){
			if (localWarcFile.isFile() &&
					localWarcFile.getName().endsWith(".warc.gz"))
				extractor.extractTextFromWarc(localWarcFile);
		}
	}
}
