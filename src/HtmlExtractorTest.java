//package edu.nwnu.ququzone.htmlextractor;


/**
 * Html extractor test.
 * 
 * @author Luowen
 */
public class HtmlExtractorTest {
    //@Test
    public static void main(String[] args) {
    	HtmlExtractorImpl e = new HtmlExtractorImpl();
        HtmlResult r = e
                .extractContent("http://www.hjxf.net/2012/1217/10536.html");
        if(r.getState() == "ok")
        {
            System.out.println("时间是："+r.getDate());
            System.out.println("作者是："+r.getAuth());
            System.out.println("来源是："+r.getFrom());
            System.out.println("公司是："+r.getCompany());
            System.out.println("标题是："+r.getTitle());
            System.out.println(r.getText());
        }
        else{
            
        	System.out.println(r.getState());
        	System.out.println(r.getMsg());
        	System.out.println(r.getText());
        }
    }

}
