import edu.stanford.nlp.ling.IndexedWord;

public class Info {
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
}
