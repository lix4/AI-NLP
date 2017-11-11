import java.util.ArrayList;
import java.util.List;

public class ParallelInfo implements IInfo {
    private IInfo subject;
    private List<IInfo> parallelObjectInfo;
    private List<IInfo> parallelPredicateInfo;

    public ParallelInfo() {
        this.subject = null;
        this.parallelObjectInfo = new ArrayList<>();
        this.parallelPredicateInfo = new ArrayList<>();
    }

    public IInfo getSubject() {
        return subject;
    }

    public void setSubject(IInfo subject) {
        this.subject = subject;
    }

    public void addNewObject(IInfo object) {
        this.parallelObjectInfo.add(object);
    }

    public void addNewPredicate(IInfo predicate) {
        this.parallelPredicateInfo.add(predicate);
    }

    public List<IInfo> getParallelObjectInfo() {
        return parallelObjectInfo;
    }

    public void setParallelObjectInfo(List<IInfo> parallelObjectInfo) {
        this.parallelObjectInfo = parallelObjectInfo;
    }

    public List<IInfo> getParallelPredicateInfo() {
        return parallelPredicateInfo;
    }

    public void setParallelPredicateInfo(List<IInfo> parallelPredicateInfo) {
        this.parallelPredicateInfo = parallelPredicateInfo;
    }

    public boolean parallel() {
        return this.parallelPredicateInfo.size() > 1 && this.parallelObjectInfo.size() > 1;
    }

    private String printPredicates() {
        StringBuilder sb = new StringBuilder();
        for (IInfo predicate : this.parallelPredicateInfo) {
            sb.append(" ");
            sb.append(predicate.toString());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "{Subject: " + subject + ", Predicates: " + printPredicates() +
                ", Objects: " + printObjects() + "}";
    }

    private String printObjects() {
        StringBuilder sb = new StringBuilder();
        for (IInfo object : this.parallelObjectInfo) {
            sb.append(" ");
            if (object != null) {
                sb.append(object.toString());
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(IInfo other) {
        return false;
    }
}
