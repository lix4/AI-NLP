import java.util.Scanner;


public class WordProcessor {

	/**
	 * Determines the stem of a given word using a partial implementation of
	 * Porter's algorithm.
	 * Currently assumes that input is composed purely of letters
	 * @param word
	 * @return the stem of word
	 */
	public static String getStem(String word) {
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
			System.out.print("Enter a word (or Q to quit):\n");
			input = scanner.nextLine();
			if (!input.equalsIgnoreCase("q")) {
				System.out.print("Stemmed input: " + getStem(input) + "\n");
			}
		} while (!input.equalsIgnoreCase("q"));
		
		scanner.close();
	}
}
