package test.gn;

public class GarbageFilter {
	
	private int longStringSize = 500;
	private int numSpacesSize = 4;
	
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
	public String removeWhiteSpace (String input){
		String after = input.trim().replaceAll(" +", " ");
		return after;
	}

	// Returns true if input contains HTML content, false otherwise.
	public Boolean containsHtml (String input){
		return input.startsWith("HTTP/1.1") ||  // is an HTTP request
				input.contains("</") ||        // closing tags
				input.contains("/>") ||        // self-closing tags
				input.contains("<a href=") ||  // anchor tags
				input.matches(".*<.{1,4}>.*")  // opening tags, 1-4 chars
				;
	}

	// Returns true if input contains less than six words (detected by the
	// number of spaces in input)
	public Boolean tooShort (String input){
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
	public Boolean tooLong (String input){
		return (input.length() > longStringSize);		
	}
	
	public static void main(String[] args) throws Exception {
		GarbageFilter filter = new GarbageFilter();
		
		String testString = " I am       King  Kong. ";
		
		System.err.println(testString.length());
		
		System.out.println(filter.removeWhiteSpace(testString));
		
		System.out.println(filter.tooShort(filter.removeWhiteSpace(testString)));

		
		System.out.println(filter.tooLong(testString));
	}
}
