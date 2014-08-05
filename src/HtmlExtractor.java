//package edu.nwnu.ququzone.htmlextractor;

/**
 * html main body extractor.
 * 
 * @author Luowen
 */
public interface HtmlExtractor {
    /**
     * extract main body.
     * 
     * @param url
     * @return
     */
    public HtmlResult extractContent(String url);
}
