import java.util.Scanner;


public class WordProcessor {
	
	/**
	 * Performs stemming on each word in a phrase
	 * @param sentence
	 * @return the given phrase with each word replaced with its stem
	 */
	public static String stemPhrase(String sentence) {
		// split the sentence into words
		String[] words = sentence.split(" ");
		
		for (int i=0; i < words.length; i++) {
			//temporarily remove non-letter symbols from the ends of the word
			String pre = "", suff = "", word = words[i];
			while (word.length() > 0 && !Character.isAlphabetic(word.charAt(0))) {
				pre += word.substring(0, 1);
				word = word.substring(1);
			}
			while (word.length() > 0 && !Character.isAlphabetic(word.charAt(word.length()-1))) {
				suff += word.substring(word.length()-1);
				word = word.substring(0, word.length()-1);
			}
			
			String stem = getStem(word);
			words[i] = pre + stem + suff;
		}
		
		// reconstruct the sentence
		String out = words[0];
		for (int i=1; i < words.length; i++) {
			out += " " + words[i];
		}
		return out;
	}

	/**
	 * Determines the stem of a given word using a partial implementation of
	 * Porter's algorithm.
	 * Currently assumes that input is composed purely of letters
	 * @param word
	 * @return the stem of word
	 */
	public static String getStem(String word) {
		String stem = word;
		stem = step1Stemming(stem);
		stem = step2Stemming(stem);
		return stem;
	}
	
	/**
	 * Performs step 1 of Porter's algorithm
	 * @param word
	 * @return
	 */
	public static String step1Stemming(String word) {
		String stem = word;
		// step 1a
		if (suffixIgnoreCase(stem, "sses")) {
			stem = stem.substring(0, stem.length()-2);
		} else if (suffixIgnoreCase(stem, "ies")) {
			stem = stem.substring(0, stem.length()-2);
		} else if (suffixIgnoreCase(stem, "s") && !suffixIgnoreCase(stem, "ss")) {
			stem = stem.substring(0, stem.length()-1);
		}
		
		// preprocess to make calculating measures easier
		boolean[] vowels = new boolean[stem.length()];
		for (int i=0; i < stem.length(); i++) {
			vowels[i] = vowelAt(stem, i);
		}
		
		// step 1b
		boolean extraStep = false;
		if (suffixIgnoreCase(stem, "eed")) {
			if (positiveMeasure(vowels, stem.length()-3)) {
				stem = stem.substring(0, stem.length()-1);
			}
		} else if (suffixIgnoreCase(stem, "ed") && hasVowel(vowels, stem.length()-2)) {
			stem = stem.substring(0, stem.length()-2);
			extraStep = true;
		} else if (suffixIgnoreCase(stem, "ing") && hasVowel(vowels, stem.length()-3)) {
			stem = stem.substring(0, stem.length()-3);
			extraStep = true;
		}
		// prevent out-of-bounds from too-short input
		if (stem.length() == 0) return stem;
		
		if (extraStep) {
			if (suffixIgnoreCase(stem, "at") || suffixIgnoreCase(stem, "bl") ||
					suffixIgnoreCase(stem, "iz")) {
				stem = stem.concat("e");
				vowels[stem.length()-1] = true;
			} else {
				boolean doubleSuff = doubleConsonant(stem);
				char last = stem.charAt(stem.toLowerCase().length()-1);
				if (doubleSuff && last!='l' && last!='s' && last!='z') {
					stem = stem.substring(0, stem.length()-1);
				} else if (getMeasure(vowels, stem.length()) == 1 && endsCVC(stem)) {
					stem = stem.concat("e");
					vowels[stem.length()-1] = true;
				}
			}
		}
		
		// step 1c
		if (suffixIgnoreCase(stem, "y") && hasVowel(vowels, stem.length()-1)) {
			stem = stem.substring(0, stem.length()-1) + "i";
			vowels[stem.length()-1] = true;
		}
		
		return stem;
	}
	
	/**
	 * Performs step 2 of Porter's algorithm
	 * @param word
	 * @return
	 */
	public static String step2Stemming(String word) {
		int len = word.length();
		
		if (len <= 4) return word;
		
		// preprocess to make calculating measures easier
		boolean[] vowels = new boolean[word.length()];
		for (int i=0; i < word.length(); i++) {
			vowels[i] = vowelAt(word, i);
		}
		
		char penultimate = word.toLowerCase().charAt(word.length()-2);
		if (penultimate == 'a') {
			if (suffixIgnoreCase(word, "ational") && 
					positiveMeasure(vowels, word.length()-7)) {
				return word.substring(0, word.length()-5) + "e";
			} else if (suffixIgnoreCase(word, "tional") && positiveMeasure(vowels, word.length()-6)) {
				return word.substring(0, word.length()-2);
			}
		} else if (penultimate == 'c') {
			if (suffixIgnoreCase(word, "enci") && 
					positiveMeasure(vowels, word.length()-4)) {
				return word.substring(0, word.length()-1) + "e";
			} else if (suffixIgnoreCase(word, "anci") && 
					positiveMeasure(vowels, word.length()-4)) {
				return word.substring(0, word.length()-1) + "e";
			}
		} else if (penultimate == 'e') {
			if (suffixIgnoreCase(word, "izer") && 
					positiveMeasure(vowels, len-4)) {
				return word.substring(0, len-1);
			}
		} else if (penultimate == 'l') {
			if (suffixIgnoreCase(word, "abli") && 
					positiveMeasure(vowels, word.length()-4)) {
				return word.substring(0, word.length()-1) + "e";
			} else if (suffixIgnoreCase(word, "alli") && 
					positiveMeasure(vowels, word.length()-4)) {
				return word.substring(0, word.length()-2);
			} else if (suffixIgnoreCase(word, "entli") && 
					positiveMeasure(vowels, word.length()-5)) {
				return word.substring(0, word.length()-2);
			} else if (suffixIgnoreCase(word, "eli") && 
					positiveMeasure(vowels, len-3)) {
				return word.substring(0, len-2);
			} else if (suffixIgnoreCase(word, "ousli") && 
					positiveMeasure(vowels, len-5)) {
				return word.substring(0, len-2);
			}
		} else if (penultimate == 'o') {
			if (suffixIgnoreCase(word, "ization") && 
					positiveMeasure(vowels, len-7)) {
				return word.substring(0, len-5) + "e";
			} else if (suffixIgnoreCase(word, "ation") && 
					positiveMeasure(vowels, len-5)) {
				return word.substring(0, len-3) + "e";
			} else if (suffixIgnoreCase(word, "ator") && 
					positiveMeasure(vowels, len-4)) {
				return word.substring(0, len-2) + "e";
			}
		} else if (penultimate == 's') {
			if (suffixIgnoreCase(word, "alism") && 
					positiveMeasure(vowels, len-5)) {
				return word.substring(0, len-3);
			} else if (suffixIgnoreCase(word, "iveness") && 
					positiveMeasure(vowels, len-7)) {
				return word.substring(0, len-4);
			} else if (suffixIgnoreCase(word, "fulness") && 
					positiveMeasure(vowels, len-7)) {
				return word.substring(0, len-4);
			} else if (suffixIgnoreCase(word, "ousness") && 
					positiveMeasure(vowels, len-7)) {
				return word.substring(0, len-4);
			}
		} else if (penultimate == 't') {
			if (suffixIgnoreCase(word, "aliti") && 
					positiveMeasure(vowels, len-5)) {
				return word.substring(0, len-3);
			} else if (suffixIgnoreCase(word, "iviti") && 
					positiveMeasure(vowels, len-5)) {
				return word.substring(0, len-3) + "e";
			} else if (suffixIgnoreCase(word, "biliti") && 
					positiveMeasure(vowels, len-6)) {
				return word.substring(0, len-5) + "le";
			}
		}
		return word;
	}
	
	/**
	 * Tests if the given string ends consonant-vowel-consonant,
	 * where the final consonant is not w, x, or y.
	 * @param str
	 * @return
	 */
	private static boolean endsCVC(String str) {
		if (str.length() < 3 || vowelAt(str, str.length()-3) || 
				consonantAt(str, str.length()-2) || vowelAt(str, str.length()-1)) {
			return false;
		}
		char last = str.charAt(str.toLowerCase().length()-1);
		return last != 'w' && last != 'x' && last != 'y';
	}
	

	/**
	 * Tests if the given string ends in a double consonant.
	 * @param str
	 * @return 
	 */
	private static boolean doubleConsonant(String str) {
		return str.length() >= 2 && consonantAt(str, str.length()-1) &&
				str.charAt(str.length()-1) == str.charAt(str.length()-2);
	}

	private static boolean hasVowel(boolean[] vowels, int len) {
		for (int i=0; i < len; i++) {
			if (vowels[i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * An efficient way to test for nonzero measure.
	 * Tests for the existence of any vowel followed by a consonant.
	 * @param vowels
	 * @param len
	 * @return 
	 */
	private static boolean positiveMeasure(boolean[] vowels, int len) {
		for (int i=0; i < len-1; i++) {
			if (vowels[i] && !vowels[i+1]) {
				return true;
			}
		}
		return false;
	}
	
	private static int getMeasure(boolean[] vowels, int len) {
		int m = 0;
		for (int i=0; i < len-1; i++) {
			if (vowels[i] && !vowels[i+1]) {
				m++;
			}
		}
		return m;
	}

	private static boolean suffixIgnoreCase(String word, String suffix) {
		return word.length() >= suffix.length() &&
				word.substring(word.length()-suffix.length()).equalsIgnoreCase(suffix);
	}
	
	private static boolean vowelAt(String str, int pos) {
		String lower = str.toLowerCase();
		char c = lower.charAt(pos);
		return c=='a' || c=='e' || c=='i' || c=='o' || c=='u' ||
				(c=='y' && pos > 0 && consonantAt(lower, pos-1));
	}
	
	private static boolean consonantAt(String str, int pos) {
		return !vowelAt(str, pos);
	}
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String input = "q";
		do {
			System.out.print("Enter a sentence (or Q to quit):\n");
			input = scanner.nextLine();
			if (!input.equalsIgnoreCase("q")) {
				System.out.print("Stemmed input: " + stemPhrase(input) + "\n");
			}
		} while (!input.equalsIgnoreCase("q"));
		
		scanner.close();
	}
}
