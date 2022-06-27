package net.in.dayan.json.test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class Base extends Parent {
    public int id;
    public String name;
    public Long lVal;
    public Boolean bVal;
    public Float fVal;
    public Double dVal;
    private URL url;
    public JsonNode jsonNode;
    public Byte bt;
    public Character c;
    public Date date;
    public Timestamp timestamp;
    public Fruit fruit;

    public transient String noOutput1 = "Error1";

    @JsonIgnore
    public String noOutput2 = "Error2";

    public void setUrl(URL url) {
        this.url = url;
    }

    public enum Fruit {
        Banana,
        Apple,
        Grape
    };

    public String getTimestamp() {
        SimpleDateFormat sf = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss");
        return sf.format(timestamp);
    }

    public Map<String, Object> map;
    public Integer[] intArr;

    public Child[] childArray;
    public Set<Child> childSet = new HashSet<Child>();
    public List<Child> childList = new ArrayList<Child>();

    public Master master;
}