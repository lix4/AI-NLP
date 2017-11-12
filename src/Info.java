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
    
    /**
     * Initializes all fields as null.
     */
    public Info() {
    	this.subject = null;
    	this.object = null;
    	this.predicate = null;
    }

    public IInfo getSubject() {
        return subject;
    }

    public void setSubject(IndexedWord subject) {
        this.subject = new InfoLiteral(subject);
    }
    
    public void setSubject(IInfo subj) {
    	this.subject = subj;
    }

    public IInfo getPredicate() {
        return predicate;
    }

    public void setPredicate(IndexedWord predicate) {
        this.predicate = new InfoLiteral(predicate);
    }
    
    public void setPredicate(IInfo pred) {
    	this.predicate = pred;
    }

    public IInfo getObject() {
        return object;
    }

    public void setObject(IndexedWord object) {
        this.object = new InfoLiteral(object);
    }
    
    public void setObject(IInfo obj) {
    	this.object = obj;
    }

    @Override
    public String toString() {
        return "{Subject: " + subject + " Predicate: " + predicate +
                " Object: " + object + " }";
    }

    public String originalText() {
        return this.toString();
    }

    @Override
    // not necessarily equality, but more useful for searching
    public boolean equals(IInfo other) {
        if (other instanceof Info) {
	        Info info = (Info) other;
	        return this.subject.equals(info.getSubject()) && this.object.equals(info.getObject()) &&
	                this.predicate.equals(info.getPredicate());
        } else if (other instanceof ParallelInfo) {
        	ParallelInfo pInfo = (ParallelInfo) other;
        	return subject.equals(pInfo.getSubject()) && 
        			pInfo.getParallelPredicateInfo().contains(predicate) &&
        			pInfo.getParallelObjectInfo().contains(object);
        }
        return false;
    }
}
