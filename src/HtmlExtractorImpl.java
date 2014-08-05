//package edu.nwnu.ququzone.htmlextractor;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * html extractor.
 * 
 * @author Luowen
 */
// @Component

public class HtmlExtractorImpl implements HtmlExtractor {

    public HtmlResult result = new HtmlResult();
    private int maxBytes = 1000000 / 2;
    public Pattern p;
    public Matcher m;
    public int link_num;
    public int row_num = 0;
    public String format_html; // 其实是非格式化，如果需要自己改动

    @Override
    public HtmlResult extractContent(String url) {
	String html = "";
	this.result.setUrl(url);
	String temptitle = "";
	link_num = 0;
	// 获取网页页面
	try {
	    html = fetchHtmlString(url, 30000);
	} catch (Exception e) {
	    System.out.println("获取网页异常,网络问题");
	    return new HtmlResult("fail", "获取网页异常,网络问题", url);
	}
	if (html.length() == 0) {
	    System.out.println("无法获取网页信息");
	    return new HtmlResult("fail", "无法获取源网页内容", url);
	}
	String str = html.toString();
	/*
	 * //获取文本页面 InputStreamReader inputReader = null; BufferedReader
	 * bufferReader = null; StringBuffer strBuffer = new StringBuffer(); try
	 * { InputStream inputStream = new FileInputStream(url); inputReader =
	 * new InputStreamReader(inputStream); bufferReader = new
	 * BufferedReader(inputReader);
	 * 
	 * // 读取一行 String line = null;
	 * 
	 * while ((line = bufferReader.readLine()) != null) {
	 * strBuffer.append(line); }
	 * 
	 * } catch (IOException e) { System.out.println("*******"+e); } String
	 * str=strBuffer.toString();
	 */
	// 直接赋值
	// String str;
	 //str = "<meta name="description" content=";
         str = str.toLowerCase();
	 str = str.replace("\t", ""); 
	 str = str.replace("\r", "\n"); 
	 str = str.replace("<br>", "\n");
	 str = str.replace("<br />", "\n");
	 str =str.replace("</p>", "</p>\n"); 
	 str =str.replace("</span>","</span>\n"); 
	 if (str != null) { 
	     p = Pattern.compile("\n\n");
	     m = p.matcher(str); 
	     str = m.replaceAll("\n"); // 去掉制表符和换行符 }
	 }
	
	if (str != null) {
	    p = Pattern.compile(".*<title>(.*?)</title>.*");
	    m = p.matcher(str);
	    while (m.find()) {
		temptitle= m.group(1);
	    }
	}
	if (str != null) // 去除style标签
	{
	    p = Pattern.compile("(?is)<style[^>]*?>.*?<\\/style>");
	    m = p.matcher(str);
	    str = m.replaceAll("");
	}
	if (str != null) // 去除html的转义字符
	{
	    p = Pattern.compile("&[a-z]*;");
	    m = p.matcher(str);
	    str = m.replaceAll("");
	}
	if (str != null) // 去除script代码
	{
	    p = Pattern.compile("(?is)<script[^>]*?>.*?<\\/script>");
	    m = p.matcher(str);
	    str = m.replaceAll("");
	}

	if(str!=null)
	{
	    int i=1;
	    StringBuilder sb=new StringBuilder(str);
	    p = Pattern.compile("</[a-z0-9]+>[^\n]");
	    m = p.matcher(str);
	    while (m.find()) {
		   sb.insert(m.end()+i-2, "\n");
		   i++;
		}
	    str=sb.toString();
	}
        
	format_html = str;
	//System.out.println(format_html);
	if (str != null) // 去除所有不带换行的标签
	{
	    p = Pattern.compile("<[^>\n]*>");
	    m = p.matcher(str);
	    str = m.replaceAll("");
	}
	if (str != null) // 去除所有带换行的标签
	{
	    p = Pattern.compile("<[^>\n]*\n|[^<\n]*>\n");
	    m = p.matcher(str);
	    str = m.replaceAll("\n");
	}
	
	//System.out.println(str);
	String[] str_blocks = str.split("\n");
        
	List<String> blocks = new ArrayList<String>();
	for (int i = 0; i < str_blocks.length; i++) {
	    blocks.add(str_blocks[i].trim());
	}
	int len = str_blocks.length / 40;
	if (len <18 )
	    len = 18;
	String[] titles = new String[len];
	titles[0] = temptitle;

	print_test(blocks);
	// 提取文章页信息
	extractMainText(blocks, titles, 4);

	return result;
    }

    private void extractMainText(List<String> blocks, String[] titles, int k) {
	int len = 0;
	int start, end;
	List<Block> candidates = new ArrayList<Block>();
	Block current = null;
	StringBuffer currentText = new StringBuffer("");
	for (int i = 0; i + k < blocks.size(); i++) {
	    len = 0;
	    for (int j = i; j < i + k; j++) {
		len = len + blocks.get(j).length();
	    }
	    current = new Block();
	    current.setStart(i);
	    current.setEnd(i + k - 1);
	    current.setlength(len);
	    candidates.add(current);
	    current = null;
	    // System.out.println(i+"第i个快的长度"+len);
	}
	if (candidates.size() == 0) {
	    result.setState("fail");
	    result.setMsg("行块无法获得");
	    return;
	} else {
	    Block longst = candidates.get(0);
	    for (Block b : candidates) {
		if (b.getlength() > longst.getlength()) {
		    longst = b;
		}
	    }
	    System.out.println( "***最长的行块是：" + longst.getStart());
	    for (start = longst.getStart(); start >= 0
		    && candidates.get(start).getlength() > 10; start--)
		;
	    for (end = longst.getEnd(); end < candidates.size()
		    && candidates.get(end).getlength() > 0; end++)
		;

	    for (int count = start + k; count < end; count++) {
		currentText.append(blocks.get(count));
		currentText.append("\n");
	    }
	    if (currentText.length() == 0) {
		result.setState("fail");
		result.setMsg("无法抽取正文");
	    } else {
		result.setText(currentText.toString());
	    }
            int empty_row=0;
            for(int i= start;i<end;i++)
            {
        	if(blocks.get(i).length() == 0)
        	{
        	    empty_row++;
        	}
            }
	    System.out.println("开始行号："+start + "******结束行号：" + end);
	    if (Is_Article(start, end, currentText.length(),empty_row)) {
		int i = start + k;
		int m; // 最终得到titles的实际长度
		for (m = 1; m < titles.length; m = m + 2) {
		    while (i < blocks.size() && blocks.get(i).length() == 0)
			i++;
		    if (i < blocks.size()) {
			titles[m] = blocks.get(i); // 获取离正文最近的一段title
		    } else {
			break;
		    }
		    i++;
		}
		m = m - 2;
		i = start;
		int l;
		for (l = 2; l < titles.length; l = l + 2) {
		    while (i >= 0 && blocks.get(i).length() == 0)
			i--;
		    if (i >= 0) {
			titles[l] = blocks.get(i); // 获取离正文最近的一段title
		    } else {
			break;
		    }
		    i--;
		}
		l = l - 2;
		// 注意在titles里面可以为空
		String abs_title = Get_Title(titles, m > l ? m : l);
		if (abs_title.length() != 0) {
		    result.setTitle(abs_title);
		}
	    }
	}
    }

    private String fetchHtmlString(String url, int timeout)
	    throws MalformedURLException, IOException {
	HttpURLConnection connection = createHttpConnection(url, timeout);
	connection.setInstanceFollowRedirects(true);
	String encoding = connection.getContentEncoding();

	InputStream is;
	if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
	    is = new GZIPInputStream(connection.getInputStream());
	} else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
	    is = new InflaterInputStream(connection.getInputStream(),
		    new Inflater(true));
	} else {
	    is = connection.getInputStream();
	}
	byte[] data = streamToData(is);
	String streamEncoding = detectEncoding(data);
	if (data == null || streamEncoding == null) {
	    System.out.println("streamEncoding is null");
	    return "";
	}
	return new String(data, streamEncoding);
    }

    private HttpURLConnection createHttpConnection(String url, int timeout)
	    throws MalformedURLException, IOException {
	HttpURLConnection connection = (HttpURLConnection) new URL(url)
		.openConnection(Proxy.NO_PROXY);
	connection
		.setRequestProperty(
			"User-Agent",
			"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
	connection
		.setRequestProperty("Accept",
			"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	connection.setRequestProperty("content-charset", "UTF-8");
	connection.setRequestProperty("Cache-Control", "max-age=0");
	connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
	connection.setConnectTimeout(timeout);
	connection.setReadTimeout(timeout);
	return connection;
    }

    private String detectEncoding(byte[] data) {
	UniversalDetector detector = new UniversalDetector(null);
	detector.handleData(data, 0, data.length);
	detector.dataEnd();
	String encoding = detector.getDetectedCharset();
	detector.reset();
	return encoding;
    }

    private byte[] streamToData(InputStream is) {
	BufferedInputStream in = null;
	try {
	    in = new BufferedInputStream(is, 2048);
	    ByteArrayOutputStream output = new ByteArrayOutputStream();

	    int bytesRead = output.size();
	    byte[] arr = new byte[2048];
	    while (true) {
		if (bytesRead >= maxBytes) {
		    break;
		}
		int n = in.read(arr);
		if (n < 0)
		    break;
		bytesRead += n;
		output.write(arr, 0, n);
	    }
	    return output.toByteArray();
	} catch (SocketTimeoutException e) {

	    return null;
	} catch (IOException e) {

	    return null;
	} finally {
	    if (in != null) {
		try {
		    in.close();
		} catch (Exception e) {
		}
	    }
	}
    }

    private String Get_Title(String[] titles, int len) {
	Pattern p;
	Matcher m;
	int[] weight = new int[len];
	for (int i = 0; i < len; i++) {
	    // System.out.println(titles[i]);
	    weight[i] = 100;
	}
	weight[0]+=2;
	for (int i = len - 1; i >= 0; i--) {
	    if (titles[i] == null || titles[i].length() == 0
		    || titles[i].length() > 100) {
		weight[i] = 0;
		continue;
	    }
	    if (titles[i].length() > 50 || titles[i].length() < 10) {
		weight[i] -= 2;
	    }
	    if( titles[i].contains("http://www"))
	    {
		weight[i] -= 5;
	    }
	    
	    p = Pattern.compile("[\\._|,:：，、]+");
	    m = p.matcher(titles[i]);
	    while (m.find()) {
		weight[i] -= 2;
	    }
		
	    if (titles[i].contains("时间") || titles[i].contains("日期")
		    || titles[i].contains("-") || titles[i].contains("/")
		    || titles[i].contains("年")) {
		p = Pattern
			.compile("[0-9]{2,4}[年/-][0-9]{1,2}[月/-][0-9]{1,4}[日]*");
		m = p.matcher(titles[i]);
		while (m.find()) {
		    result.setDate(m.group());
		    weight[i] -= 5;
		    break;
		}
	    }
	    if (titles[i].contains("作者") || titles[i].contains("笔者")
		    || titles[i].contains("编辑")) {
		p = Pattern.compile("(笔者|编辑|作者|记者)：?:?\\s*([^\\s\n]*)\\s*");
		m = p.matcher(titles[i]);
		while (m.find()) {
		    result.setAuth(m.group(2));
		    break;
		}
		weight[i] -= 2;
	    }
	    if (titles[i].contains("公司") || titles[i].contains("网")) {
		p = Pattern.compile("[\u4e00-\u9fa5]+(公司|网)");
		m = p.matcher(titles[i]);
		while (m.find()) {
		    result.setCompany(m.group());
		    break;
		}
		weight[i] -= 1;
	    }
	    if (titles[i].contains("来源")) {
		p = Pattern.compile("(来源|来源于)：?:?\\s*([^\\s]*)\\s*");
		m = p.matcher(titles[i]);
		while (m.find()) {
		    result.setFrom(m.group(2));
		    break;
		}
		weight[i] -= 1;
	    }
	    if (titles[i].contains("关键")) {
		p = Pattern.compile("(关键字|关键词)：?:?\\s*(.*)\\s*");
		m = p.matcher(titles[i]);

		int iskey = 0;
		while (m.find()) {
		    String temp = m.group();
		    String temp2 = m.group(1);
		    System.out.println(temp + temp2);
		    result.addkeywords(m.group(1));
		    iskey = 1;
		}
		if (iskey == 1)
		    weight[i] -= 5;
	    }
	    if (titles[i].contains("位置") || titles[i].contains("新闻")) {
		weight[i] -= 2;
	    }
	    if (titles[i].contains("标题")) {
		weight[i] += 5;
	    }
	}
	int max = 0;
	for (int i = 0; i < len; i++) {
	    //System.out.println(titles[i]);
	    //System.out.println(weight[i]);
	    if (weight[i] > weight[max])
		max = i;
	}
	return titles[max];
    }

    private boolean Is_Article(int start, int end, int word,int empty_row) 
    {
	if (format_html != null)
	{
	    p = Pattern.compile("\n");
	    m = p.matcher(format_html);
	    int startlink = 0, endlink = 0;
	    int i = 0;
	    while (m.find()) 
	    {
		if (i == start - 1)
		{
		    startlink = m.end();
		}
		if (i == end) 
		{
		    endlink = m.end();
		    break;
		}
		i++;
	    }
	    i = 0;
	    String temp_str=format_html.substring(startlink, endlink);
	    if (startlink != 0 && endlink != 0)
	    {
		p = Pattern.compile("<a\\s*[^>]*>");
		m = p.matcher(temp_str);
		while (m.find())
		{
		    i++;
		}
		System.out.println("内容段链接数："+i+"&&&&&&&&&内容段的空行数目："+empty_row);
		if(i!=0)
		{
		    if ( word/i >50 && (((float)end-start-empty_row)/i)>2)
		    {
			if(!Is_Tail(temp_str))
			{
			    result.setMsg("该页面很可能是内容页");
			    result.setState("ok");
			    return true;
			}
			else{
			    result.setMsg("该页面很可能是列表页");
		            result.setState("fail");
		            return false;
			}
		    } 
		    else {
			 result.setMsg("该页面很可能是列表页");
			 result.setState("fail");
			 return false;
		    }
		}
		else
		{
		    if(!Is_Tail(temp_str))
		    {
		        result.setMsg("该页面很可能是内容页");
		        result.setState("ok");
		        return true;
		    }
		    else {
			 result.setMsg("该页面很可能是列表页");
			 result.setState("fail");
			 return false;
		    }
		}
	    } 
	    else{
		result.setMsg("无法定位到开头和结尾");
		result.setState("fail");
		return false;
	    }
	}
	else {
	    result.setMsg("无法得到一个格式化的html");
	    result.setState("fail");
	    return false;
	}
    }

    private void print_test(List<String> blocks) {
	for (int i = 0; i < blocks.size(); i++) {
	    // System.out.println(i);
	    System.out.println(blocks.get(i));
	}
    }
    
    private boolean Is_Tail(String str){
	Pattern p;
	Matcher m;
	int weight=100;
	p = Pattern.compile("(电话|地址|传真|mail|邮编|邮箱|Tel|Fax|版权):?：?");
	m = p.matcher(str);
	while (m.find()) {
		weight -= 5;
	}
	System.out.println("是否为html尾部的权值："+weight);
	if(weight >80)
	    return false;
	else
	    return true;
    } 
}
