import edu.stanford.nlp.ling.IndexedWord;

// extending IndexedWord is an ugly way of getting a recursive structure, but hopefully it works.
public class Info extends IndexedWord {
    private IndexedWord subject;
    private IndexedWord predicate;
    private IndexedWord object;

    public Info(IndexedWord subject, IndexedWord predicate, IndexedWord object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public IndexedWord getSubject() {
        return subject;
    }

    public void setSubject(IndexedWord subject) {
        this.subject = subject;
    }

    public IndexedWord getPredicate() {
        return predicate;
    }

    public void setPredicate(IndexedWord predicate) {
        this.predicate = predicate;
    }

    public IndexedWord getObject() {
        return object;
    }

    public void setObject(IndexedWord object) {
        this.object = object;
    }
    
    public String toString() {
    	return "{Subject: " + subject + ", Predicate: " + predicate + 
    			", Object: " + object + "}";
    }
    
    public String originalText() {
    	return this.toString();
    }
}
