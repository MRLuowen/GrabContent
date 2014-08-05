//package edu.nwnu.ququzone.htmlextractor;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * html result.
 * 
 * @author Luowen
 */
public class HtmlResult implements Serializable {
    private static final long serialVersionUID = -2323320480483953602L;

    private String state;
    private String msg;
    
    private String from;
    private String company; 
    private String date;
    private String auth;   
    private ArrayList<String> keywords;
    private String url;
    private String title;
    private String text;

    public HtmlResult() {
    	keywords = new ArrayList<String>();
    }
    
    public HtmlResult(String state, String msg, String url) {
        this.state = state;
        this.msg = msg;
        this.url = url;
    }
    public void addkeywords(String key){
    	keywords.add(key);
    }
    public ArrayList<String> getkeywords(){
    	return keywords;
    }
    public String getAuth() {
        return auth;
    }

    public void setCompany(String state) {
        this.company = state;
    }
    public String getCompany() {
        return company;
    }
    
    public String getFrom() {
        return from;
    }
    public void setFrom(String state) {
        this.from = state;
    }
    
    public void setAuth(String state) {
        this.auth = state;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String state) {
        this.date = state;
    }
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
