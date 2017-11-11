import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

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
        Map<String, Info> infoMap = new HashMap<>();

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
        System.out.println(doc.sentences().size());
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

                Info extracted = processPhrase(dependencies, root);
                infoMap.put(sent.text(), extracted);


            }

            // This is the coreference link graph
            // Each chain stores a set of mentions that link to each other,
            // along with a method for getting the most representative mention
            // Both sentence and token offsets start at 1!
            Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);
            System.out.println();
            System.out.println(graph);
        }

        writeIntoDoc(infoMap);
    }

    public static void writeIntoDoc(Map<String, Info> map) {
        File file = new File("C:/Users/Me/Desktop/directory/file.txt");
        PrintWriter pw = null;
        try {
            pw = new PrintWriter("result.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        pw.println("Result");
        pw.println();

        Set<Map.Entry<String, Info>> entrySet = map.entrySet();
        for (Map.Entry<String, Info> entry : entrySet) {
            pw.println("--------------------------");
            pw.println("current sentence: " + entry.getKey());
            pw.println(entry.getValue().toString());
            pw.println();
        }

        pw.close();
    }

    public static Info processPhrase(SemanticGraph dependencies, IndexedWord root) {
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
//                processDeterminer(dependencies, root);
                break;
            case "JJ":
//                processAdjectivePhrase();
                break;
            default:
                System.out.println("Cannot identify sentence structure.");
                return null;
        }
        return null;
    }

    static public void processAdjectivePhrase(SemanticGraph dependencies, IndexedWord root) {
        List<Pair<GrammaticalRelation, IndexedWord>> s = dependencies.childPairs(root);

    }

    // Processes: {This, that} one?
    static public void processDeterminer(SemanticGraph dependencies, IndexedWord root) {
        List<Pair<GrammaticalRelation, IndexedWord>> s = dependencies.childPairs(root);

        System.out.println("Identity of object: " + root.originalText().toLowerCase());
    }

    //Processes: {That, this, the} {block, sphere}
    static public Info processNounPhrase(SemanticGraph dependencies, IndexedWord root) {
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
    static public Info processVerbPhrase(SemanticGraph dependencies, IndexedWord root) {
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

        String quantifier = null;
        Boolean TopLevelNegation = false;
        Boolean PredicateLevelNegation = false;

        for (Pair<GrammaticalRelation, IndexedWord> item : s) {
            // noun subject
            if (item.first.toString().startsWith("nsubj")) {
                subject = new InfoLiteral(item.second);
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

        System.out.println("Subject: " + subject);
        System.out.println("Action: " + predicate);
        System.out.println("Object: " + object);
        return new Info(subject, predicate, object);
    }


}