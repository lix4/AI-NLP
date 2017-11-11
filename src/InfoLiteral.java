import edu.stanford.nlp.ling.IndexedWord;


public class InfoLiteral implements IInfo {
	private String word;
	
	public InfoLiteral(String word) {
		this.word = WordProcessor.stemPhrase(word);
	}
	
	public InfoLiteral(IndexedWord iWord) {
		this(iWord.originalText());
	}
	
	public String getWord() {
		return word;
	}
	
	public void setWord(String word) {
		this.word = word;
	}

	@Override
	public String toString() {
		return this.word;
	}

	@Override
	public boolean equals(IInfo other) {
		if (!(other instanceof InfoLiteral)) {
			return false;
		}
		InfoLiteral info = (InfoLiteral) other;
		return this.word.equalsIgnoreCase(info.getWord());
	}
}
