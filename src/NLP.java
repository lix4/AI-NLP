import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

//import com.sun.org.apache.bcel.internal.generic.IINC;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.simple.*;

public class NLP {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Map<String, IInfo> infoMap = new HashMap<>();

        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
//        props.put("annotators", "tokenize, ssplit, pos, lemma");


        StanfordCoreNLP coreNLP = new StanfordCoreNLP(props);
//        File foo = new File("src/lincoln.txt");
//        Collection<File> files = new ArrayList<File>();
//        files.add(foo);
//        try {
//            System.out.println("Processing");
//            coreNLP.processFiles(files, false);
//            System.out.println("Finished");
//        } catch (IOException e) {
////            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }


//        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // read some text in the text variable
//        String text = "Pick up that block.";
//        String text = "In 1921, Einstein received the Nobel Prize for his original work on the photoelectric effect.";
//        String text = "Did Einstein receive the Nobel Prize?";
//        String text = "Mary saw a ring through the window and asked John for it.";

        // create an empty Annotation just with the given text
        //Annotation document = new Annotation(text);
//        System.out.println(text);

        long startTime = System.currentTimeMillis();
        InputStream is = null;
        try {
            is = new FileInputStream("src/lincoln.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));
        String line = null;
        try {
            line = buf.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        while (line != null) {
            sb.append(line).append("\n");
            try {
                line = buf.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String text = sb.toString();

        Document doc = new Document(text);
        int totalSentNum = doc.sentences().size();
        int count = 0;
        for (Sentence sent : doc.sentences()) {
            System.out.println();
            System.out.println("current sentence:");
            System.out.println(sent.text());
            Annotation document = new Annotation(sent.text());

            // run all Annotators on this text
            coreNLP.annotate(document);

            // these are all the sentences in this document
            // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
            List<CoreMap> sentences = document.get(SentencesAnnotation.class);
//            System.out.println(sentences.size());
            for (CoreMap sentence : sentences) {
                // traversing the words in the current sentence
                // a CoreLabel is a CoreMap with additional token-specific methods
                for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                    // this is the text of the token
                    String word = token.get(TextAnnotation.class);
                    // this is the POS tag of the token
                    String pos = token.get(PartOfSpeechAnnotation.class);
                    // this is the NER label of the token
                    String ne = token.get(NamedEntityTagAnnotation.class);
//            System.out.println("word " + word + " ,pos: " + pos + " ,ne: " + ne);
                }

//          // this is the parse tree of the current sentence
                Tree tree = sentence.get(TreeAnnotation.class);
                System.out.println();
                System.out.println(tree);

//          // this is the Stanford dependency graph of the current sentence
                SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
                System.out.println();
                System.out.println(dependencies);

                // get root of parse graph
                IndexedWord root = dependencies.getFirstRoot();

                IInfo extracted = processPhrase(dependencies, root);
                if (extracted != null) {
                    count++;
                }
                infoMap.put(sent.text(), extracted);


            }

            // This is the coreference link graph
            // Each chain stores a set of mentions that link to each other,
            // along with a method for getting the most representative mention
            // Both sentence and token offsets start at 1!
//            Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);
//            System.out.println();
//            System.out.println(graph);
        }

        writeIntoDoc(infoMap, count, totalSentNum);
        long finishTime = System.currentTimeMillis();
        long elapsed = finishTime - startTime;
        long minutes = elapsed / 60000;
        long seconds = (elapsed % 60000) / 1000;
        long millis = elapsed % 1000;
        System.out.println("Elapsed time: " + minutes + ":" + seconds + ":" + millis);
    }

    public static void writeIntoDoc(Map<String, IInfo> map, int count, int totalSentNum) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter("result.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        pw.println("Result");
        pw.println();

        Set<Map.Entry<String, IInfo>> entrySet = map.entrySet();
        for (Map.Entry<String, IInfo> entry : entrySet) {
            pw.println("--------------------------");
            pw.println("current sentence: " + entry.getKey());
            if (entry.getValue() != null) {
                System.out.println(entry.getValue().toString());
                pw.println(entry.getValue().toString());
            } else {
                pw.println("Cannot handle this sentence");
            }
            pw.println();
        }

        pw.println(String.format("Total Sentence Number: %d", totalSentNum));
        pw.println(String.format("Parsed Sentence Number: %d", count));
        double percent = count / totalSentNum;
        pw.println(String.format("You have parsed %.2f %%", percent * 100));
        pw.close();
    }
    
    public static Map<String, IInfo> readFromDoc(String fileName) throws IOException {
    	BufferedReader reader = new BufferedReader(new FileReader(fileName));
    	
    	Map <String, IInfo> map = new HashMap<String, IInfo>();
    	String sentence = "";
    	String line = reader.readLine();
    	while (line != null) {
    		if (line.startsWith("current sentence: ")) {
    			sentence = line.substring(18);
    		}
    		else if (line.startsWith("{")) {
    			map.put(sentence, readInfo(line));
    		}
    	}
    	
    	reader.close();
    	return map;
    }

    // currently assumes that there actually is a properly encoded IInfo here
    public static IInfo readInfo(String line) {
		String[] words = line.split(" ");
		Info info = new Info();
		ParallelInfo pInfo = new ParallelInfo();
		boolean parallel;
		
		IInfo subj;
		if (!words[1].startsWith("{")) {
			// subject is not a clause
			subj = new InfoLiteral(words[1]);
		} else {
			String subject = line.replaceFirst(words[0] + " ", "");
			subject = subject.substring(0, subject.indexOf("}") + 1);
			subj = readInfo(subject);
		}
		line = line.substring(line.indexOf("Predicate"));
		words = line.split(" ");
		
		if (words[0].equals("Predicates:")) {
			// this is a ParallelInfo
			parallel = true;
			pInfo.setSubject(subj);
			for (int i=1; !words[i].startsWith("Objects:"); i++) {
				pInfo.addNewPredicate(new InfoLiteral(words[i]));
			}
		} else {
			parallel = false;
			info.setSubject(subj);
			info.setPredicate(new InfoLiteral(words[1]));
		}
		line = line.substring(line.indexOf("Object"));
		words = line.split(" ");
		
		if (parallel) {
			for (int i=1; i < words.length-1; i++) {
				if (words[i].startsWith("{")) {
					// clausal object
					int j;
					String object = "";
					for (j=i; !words[j].startsWith("}"); j++) {
						object +=" " + words[j];
					}
					pInfo.addNewObject(readInfo(object + " }"));
					i = j;
				} else {
					pInfo.addNewObject(new InfoLiteral(words[i]));
				}
			}
			return pInfo;
		} else {
			if (!words[1].startsWith("{")) {
				// object is not a clause
				info.setObject(new InfoLiteral(words[1]));
			} else {
				String object = line.replaceFirst(words[0] + " ", "");
				object = object.substring(0, object.indexOf("}") + 1);
				info.setObject(readInfo(object));
			}
			return info;
		}
	}


    public static IInfo processPhrase(SemanticGraph dependencies, IndexedWord root) {
        // type of root
        String type = root.tag();
        System.out.println("type: " + type);
        String generalType = type.substring(0, 2);
        switch (generalType) {
            case "VB":
                return processVerbPhrase(dependencies, root);
            case "NN":
                return processNounPhrase(dependencies, root);
            case "DT":
                return processDeterminer(dependencies, root);
            case "JJ":
                return processAdjectivePhrase(dependencies, root);
            default:
                System.out.println("Cannot identify sentence structure.");
                return null;
        }
    }

    private static IInfo processAdverbPhrase(SemanticGraph dependencies, IndexedWord root) {
        List<Pair<GrammaticalRelation, IndexedWord>> s = dependencies.childPairs(root);
        return null;
    }

    static public IInfo processAdjectivePhrase(SemanticGraph dependencies, IndexedWord root) {
        List<Pair<GrammaticalRelation, IndexedWord>> s = dependencies.childPairs(root);

        IInfo subject = null;
        IInfo predicate = null;
        IInfo predicative = new InfoLiteral(root);

        for (Pair<GrammaticalRelation, IndexedWord> item : s) {
            if (item.first.toString().equals("cop")) {
                System.out.println("Predicate: " + item.second.originalText());
                predicate = new InfoLiteral(item.second);
            }

            // noun subject
            if (item.first.toString().startsWith("nsubj")) {
                subject = new InfoLiteral(item.second);
                System.out.println("Subject: " + item.second.originalText());
            }

            // clausal subject
            if (item.first.toString().startsWith("csubj")) {
                subject = processPhrase(dependencies, item.second);
            }
        }

        System.out.println("subject: " + subject);
        System.out.println("predicate: " + predicate);
        System.out.println("predicative: " + predicative);
        return new Info(subject, predicate, predicative);
    }

    // Processes: {This, that} one?
    static public IInfo processDeterminer(SemanticGraph dependencies, IndexedWord root) {
        List<Pair<GrammaticalRelation, IndexedWord>> s = dependencies.childPairs(root);

        System.out.println("Identity of object: " + root.originalText().toLowerCase());
        return null;
    }

    //Processes: {That, this, the} {block, sphere}
    static public IInfo processNounPhrase(SemanticGraph dependencies, IndexedWord root) {
//        List<Pair<GrammaticalRelation, IndexedWord>> s = dependencies.childPairs(root);


        // next step, need to identify further components of sentence
        String quantifier = null;
//        Boolean goodToGo = false;
        Boolean TopLevelNegation = false;
        Boolean PredicateLevelNegation = false;

        IInfo subject = null;
        IInfo predicate = null;
        IInfo object = new InfoLiteral(root);

        List<Pair<GrammaticalRelation, IndexedWord>> p = dependencies.childPairs(root);
        System.out.println("Identity of object: " + root.originalText().toLowerCase());
        System.out.println("Type of object: " + p.get(0).second.originalText().toLowerCase());

        for (Pair<GrammaticalRelation, IndexedWord> item : p) {
            if (item.first.toString().equals("cop")) {
                System.out.println("Predicate: " + item.second.originalText());
                predicate = new InfoLiteral(item.second);
            }

            // noun subject
            if (item.first.toString().startsWith("nsubj")) {
                subject = new InfoLiteral(item.second);
                System.out.println("Subject: " + item.second.originalText());
            }

            // clausal subject
            if (item.first.toString().startsWith("csubj")) {
                subject = processPhrase(dependencies, item.second);
            }
        }

        if (subject == null) {
            System.out.println("There is no subject");
            return new Info(subject, predicate, object);
        } else {
//            List<Pair<GrammaticalRelation, IndexedWord>> t = dependencies.childPairs(subject);
//            for (Pair<GrammaticalRelation, IndexedWord> item : t) {
//                if (item.first.toString().equals("det")) {
//                    quantifier = item.second.lemma().toString();
//                }
//                if (item.first.toString().equals("neg")) {
//                    TopLevelNegation = true;
//                }
//            }
        }

        return new Info(subject, predicate, object);
    }

    // Processes: {Pick up, put down} {that, this} {block, sphere}
    static public IInfo processVerbPhrase(SemanticGraph dependencies, IndexedWord root) {
        List<Pair<GrammaticalRelation, IndexedWord>> s = dependencies.childPairs(root);
        Pair<GrammaticalRelation, IndexedWord> prt = s.get(0);
//        Pair<GrammaticalRelation, IndexedWord> dobj = s.get(1);

//        List<Pair<GrammaticalRelation, IndexedWord>> newS = dependencies.childPairs(dobj.second);


//        System.out.println("Action: " + root.originalText().toLowerCase() + prt.second.originalText().toLowerCase());
//        System.out.println("Type of object: " + dobj.second.originalText().toLowerCase());
//        System.out.println("Identity of object: " + newS.get(0).second.originalText().toLowerCase());

        IInfo object = null;
        IInfo subject = null;
        IInfo predicate = new InfoLiteral(root);
        // will only be used if the sentence has multiple predicates
        ParallelInfo pi = new ParallelInfo();

        String quantifier = null;
        Boolean TopLevelNegation = false;
        Boolean PredicateLevelNegation = false;

        for (Pair<GrammaticalRelation, IndexedWord> item : s) {
            // noun subject
            if (item.first.toString().startsWith("nsubj")) {
                subject = new InfoLiteral(item.second);
            }

            // parallel predicates
            if (item.first.toString().startsWith("conj:and")) {
                pi.addNewPredicate(new InfoLiteral(item.second));
                List<Pair<GrammaticalRelation, IndexedWord>> sub = dependencies.childPairs(item.second);
                for (Pair<GrammaticalRelation, IndexedWord> subItem : sub) {
                    if (subItem.first.toString().startsWith("dobj")) {
                        pi.addNewObject(new InfoLiteral(subItem.second));
                    }
                }
            }

            // clausal subject
            if (item.first.toString().startsWith("csubj")) {
                subject = processPhrase(dependencies, item.second);
            }

            if (item.first.toString().equals("neg")) {
                PredicateLevelNegation = true;
            }

            // noun object
            if (item.first.toString().equals("dobj") || item.first.toString().equals("iobj")) {
                object = new InfoLiteral(item.second);
            }

            // adjective object
            if (object == null && (item.first.toString().equals("acomp") ||
                    item.first.toString().equals("xcomp"))) {
                object = new InfoLiteral(item.second);
            }

            // clausal object
            if (item.first.toString().equals("ccomp")) {
                object = processPhrase(dependencies, item.second);
            }

            // modifier as object
            if (object == null && item.first.toString().startsWith("nmod")) {
                object = new InfoLiteral(item.second);
            }
        }

        pi.setSubject(subject);
        pi.addNewPredicate(predicate);
        pi.addNewObject(object);


        if (pi.parallel()) {
            return pi;
        }
        System.out.println("Subject: " + subject);
        System.out.println("Predicate: " + predicate);
        System.out.println("Object: " + object);
        return new Info(subject, predicate, object);
    }


}