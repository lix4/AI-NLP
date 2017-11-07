import java.util.Properties;

import edu.stanford.nlp.pipeline.*;


public class Search {

	public static boolean evalStatement(String statement) {
		// setup to parse the statement
		Properties props = new Properties();
        props.put("annotators", "tokenize, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		Annotation ann = new Annotation(statement);
		pipeline.annotate(ann);
		
		return false;
	}
}
