//package edu.nwnu.ququzone.htmlextractor;

/**
 * block model.
 * 
 * @author Luowen
 */
public class Block {
    private int start;

    private int end;

    private String content;
    
    private int length;
    public int getlength()
    {
    	return length;
    }
    public void setlength(int a){
    	this.length=a;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
