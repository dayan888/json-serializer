package net.in.dayan.json;

import java.util.ArrayList;
import java.util.List;

public class Wrapper<T> {
    protected final T value;
    protected List<String> excludeList = new ArrayList<String>();
    protected List<String> includeList = new ArrayList<String>();
    protected boolean isFinallyAllowed;

    public Wrapper(T value) {
        this.value = value;
    }

    public void addExclude(List<String> excludeList) {
        this.excludeList.addAll(excludeList);
    }

    public void addExclude(String exclude) {
        this.excludeList.add(exclude);
    }

    public void addInclude(List<String> includeList) {
        this.includeList.addAll(includeList);
    }

    public void addInclude(String include) {
        this.includeList.add(include);
    }

    public void setFinallyAllowed(boolean isFinallyAllowed) {
        this.isFinallyAllowed = isFinallyAllowed;
    }

}
