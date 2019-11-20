package net.in.dayan.json.test;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Child {
    public int id;
    public String name;
    public URL url;
    public Date date;
    public Timestamp timestamp;
    public Master master;
}