import edu.stanford.nlp.ling.IndexedWord;

public class Info implements IInfo {
    private IInfo subject;
    private IInfo predicate;
    private IInfo object;

    public Info(IndexedWord subject, IndexedWord predicate, IndexedWord object) {
        this.subject = new InfoLiteral(subject);
        this.predicate = new InfoLiteral(predicate);
        this.object = new InfoLiteral(object);
    }

    public Info(IInfo subj, IInfo pred, IInfo obj) {
        this.subject = subj;
        this.predicate = pred;
        this.object = obj;
    }

    public IInfo getSubject() {
        return subject;
    }

    public void setSubject(IndexedWord subject) {
        this.subject = new InfoLiteral(subject);
    }

    public IInfo getPredicate() {
        return predicate;
    }

    public void setPredicate(IndexedWord predicate) {
        this.predicate = new InfoLiteral(predicate);
    }

    public IInfo getObject() {
        return object;
    }

    public void setObject(IndexedWord object) {
        this.object = new InfoLiteral(object);
    }

    @Override
    public String toString() {
        return "{Subject: " + subject + ", Predicate: " + predicate +
                ", Object: " + object + "}";
    }

    public String originalText() {
        return this.toString();
    }

    @Override
    public boolean equals(IInfo other) {
        if (!(other instanceof Info)) {
            return false;
        }
        Info info = (Info) other;
        return this.subject.equals(info.getSubject()) && this.object.equals(info.getObject()) &&
                this.predicate.equals(info.getPredicate());
    }
}
