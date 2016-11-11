package test.gn;

import java.io.BufferedWriter;
import java.io.DataInputStream;
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
public class ReadTextFromWarc {
	SimpleSegmentizer segmentizer = new SimpleSegmentizer(false, false);
	GarbageFilter filter = new GarbageFilter();
	MyProcessWarcRecord processor = new MyProcessWarcRecord();

	private void extractTextFromWarcStreams(DataInputStream inStream, BufferedWriter outStream) throws IOException {

		int recordCnt = 0;
		int mod = 10000;

		// use a callback class for handling WARC record data:

		WarcRecord thisWarcRecord;

		while ((thisWarcRecord = WarcRecord.readNextWarcRecord(inStream))!=null) {
			recordCnt++;

			if ((recordCnt % mod) == 0){
				System.out.println("WarcRecords processed: " + recordCnt);
			}
			// outStream.write("%% thisWarcRecord.getHeaderRecordType() = " + thisWarcRecord.getHeaderRecordType() + "\n");
			String extractedText = this.extractTextFromWarcRecord(thisWarcRecord);
			
			if (extractedText != null){
				outStream.write(extractedText);
			}
		}
		System.out.println("WarcRecords processed: " + recordCnt);

		inStream.close();
		outStream.close();
		// done processing all WARC records:
		processor.done();
	}

	public String extractTextFromWarcRecord(WarcRecord thisWarcRecord){
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
			StringBuilder outStream = new StringBuilder();
			for (List<String> sentence : segmentizer.getSentenceList()){
				String outputString = segmentizer.tokenListToString(sentence);

				if (!filter.isGarbageString(outputString)){
					outStream.append(outputString + "\n");
				}
			}
			return outStream.toString();
		}
		else
			return null;
	}


	/**
	 * <p> This method processes a compressed Warc file inputWarcFile with usual suffix .warc.gz
	 * <p> extracts text, tokenizes and sentence splits the text
	 * <p> and writes each sentence to file bz2 compressed text file outputTextFile with suffix .txt.bz2
	 * <p>
	 * <p> NOTE: to run the method inputWarcFile and outputTextFile need to be absolute file names
	 * @param inputWarcFile
	 * @param outputTextFile
	 * @throws IOException
	 * @throws CompressorException
	 */
	public void extractTextFromWarcToTxtFile(String inputWarcFile, String outputTextFile) throws IOException, CompressorException {

		// Local warc file
		GZIPInputStream gzInputStream=new GZIPInputStream(new FileInputStream(inputWarcFile));

		System.out.println("Processing warc file: " + inputWarcFile);
		System.out.println("To text file:         " + outputTextFile);

		DataInputStream inStream = new DataInputStream(gzInputStream);
		BufferedWriter outStream = Compressor.getBufferedWriterForTextFile(outputTextFile);

		long time1 = System.currentTimeMillis();

		this.extractTextFromWarcStreams(inStream, outStream);

		long time2 = System.currentTimeMillis();
		System.err.println("System time (msec): " + (time2-time1));

	}

	public static void main(String[] args) throws IOException, CompressorException {

		ReadTextFromWarc extractor = new ReadTextFromWarc();

		extractor.extractTextFromWarcToTxtFile(args[0], args[1]);
	}
}
