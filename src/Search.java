import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;


public class Search {
	
	private static StanfordCoreNLP pipeline;

	public static boolean evalStatement(String statement, Set<Info> processedText) {
		Info processed = processStatement(statement);
		return evalStatement(processed, processedText);
	}
	
	public static boolean evalStatement(Info statement, Set<Info> processedText) {
        // perform the search
        for (Info info : processedText) {
        	if (statement.equals(info)) {
        		return true;
        	}
        }
		return false;
	}
	
	public static Info processStatement(String statement) {
		return processStatement(statement, pipeline);
	}
	
	public static Info processStatement(String statement, StanfordCoreNLP coreNLP) {
		Annotation ann = new Annotation(statement);
		coreNLP.annotate(ann);
		
		// run statement extractor
		List<CoreMap> sentences = ann.get(SentencesAnnotation.class);
		CoreMap sentence = sentences.get(0);
		SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
		IndexedWord root = dependencies.getFirstRoot();
        return NLP.processPhrase(dependencies, root);
	}
	
	public static void main(String[] args) {
		// setup to parse the statements
		Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        pipeline = new StanfordCoreNLP(props);
		
        Scanner scanner = new Scanner(System.in);
		String text = "q";
		Set<Info> processed = new HashSet<Info>();
		do {
			System.out.print("Enter a statement for the text (or Q to quit):\n");
			text = scanner.nextLine();
			if (!text.equalsIgnoreCase("q")) {
				Info processedText = processStatement(text);
				System.out.println(processedText);
				processed.add(processedText);
			}
		} while (!text.equalsIgnoreCase("q"));
		
		System.out.println("Enter a statement to search the text for:");
		String searchTest = scanner.nextLine();
		Info searchInfo = processStatement(searchTest);
		if (evalStatement(searchInfo, processed)) {
			System.out.println("Match successfully found");
		}
		else {
			System.out.println("No match found");
		}
		scanner.close();
	}
}
