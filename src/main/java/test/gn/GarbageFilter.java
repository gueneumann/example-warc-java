package test.gn;

public class GarbageFilter {

	private int longStringSize = 350;
	private int numSpacesSize = 5;

	public int getLongStringSize() {
		return longStringSize;
	}
	public void setLongStringSize(int longStringSize) {
		this.longStringSize = longStringSize;
	}
	public int getNumSpacesSize() {
		return numSpacesSize;
	}
	public void setNumSpacesSize(int numSpacesSize) {
		this.numSpacesSize = numSpacesSize;
	}

	// Removes series of extraneous whitespace in the input sentence
	// On error simply returns the input
	private String removeWhiteSpace (String input){
		String after = input.trim().replaceAll(" +", " ");
		return after;
	}

	// Returns true if input contains HTML content, false otherwise.
	private Boolean containsHtml (String input){
		return input.startsWith("HTTP/1.1") ||  // is an HTTP request
				input.contains("</") ||        // closing tags
				input.contains("/>") ||        // self-closing tags
				input.contains("<a href=") ||  // anchor tags
				input.matches(".*<.{1,4}>.*") || // opening tags, 1-4 chars
				input.contains("text/html")		||
				input.contains("Connection : close")  ||
				input.contains(": PHP/5")
				;
	}

	// Returns true if input contains less than six words (detected by the
	// number of spaces in input)
	private Boolean tooShort (String input){
		boolean flag = true;
		int numSpaces = 0;		
		for (int i = 0; i < input.length(); i++){
			if (input.charAt(i) == ' ') numSpaces++;
			if (numSpaces >= numSpacesSize) {
				flag = false; break;
			}
		}
		return flag;
	}


	// returns true if the input is too long: more than longStringSize chars.
	private Boolean tooLong (String input){
		return (input.length() > longStringSize);		
	}

	private boolean startsWithLetterChar(String string){
		return Character.isLetter(string.charAt(0));
	}

	public boolean isGarbageString(String sentence){
		String trimmedSentence = this.removeWhiteSpace(sentence);
		boolean flag = (
				trimmedSentence.isEmpty() ||
				(
						!this.startsWithLetterChar(trimmedSentence) &&
						(
								this.tooShort(trimmedSentence) ||
								this.tooLong(trimmedSentence) ||
								this.containsHtml(trimmedSentence)
								)
						)
				);

		return flag;
	}


	public static void main(String[] args) throws Exception {
		GarbageFilter filter = new GarbageFilter();

		String testString = "4.2 DAV/2 X-Powered-By : PHP/5 . ";

		System.err.println(testString.length());

		System.out.println("B:"+filter.removeWhiteSpace(testString)+":E");

		System.out.println(filter.containsHtml(testString));

		System.out.println(filter.tooShort(filter.removeWhiteSpace(testString)));

		System.out.println(filter.tooLong(testString));

		System.out.println(filter.isGarbageString(testString));

	}
}
