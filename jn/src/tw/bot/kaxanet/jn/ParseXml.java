package tw.bot.kaxanet.jn;

import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ParseXml {
	//login info
	public static HashMap<String,Object> logininfo=new HashMap<String,Object>();
	
	public static ArrayList<HashMap<String,Object>> searchlist=new ArrayList<HashMap<String,Object>>();
	public static ArrayList<HashMap<String,Object>> answertlist=new ArrayList<HashMap<String,Object>>();
	public static ArrayList<HashMap<String,Object>> questionlist=new ArrayList<HashMap<String,Object>>();
	public static ArrayList<HashMap<String,Object>> subscribelist=new ArrayList<HashMap<String,Object>>();
	public static String[] answerimageurls;
	public static ArrayList<HashMap<String,Object>> replylist=new ArrayList<HashMap<String,Object>>();
	//for Main Page
	public static ArrayList<HashMap<String,Object>> newreplylist=new ArrayList<HashMap<String,Object>>();
	public static ArrayList<HashMap<String,Object>> newslist=new ArrayList<HashMap<String,Object>>();
	public static ArrayList<Theme> themeXmlList = new ArrayList<Theme>();
	
	public static HashMap<String,Object> question_details=new HashMap<String,Object>();
	final static String[] educationList=new String[] {"全部","國小", "國中","高中","大專"};
    final static String[] courseList=new String[] {"全部","國文", "英文","數學","物理","化學","理化","地科＆生物"};
    public static String[] themeList = null;
	private static final int TIMEOUT = 180000;
	
	public static void questionandanswer(List<NameValuePair> params) {
		int iElementLength = 0;
		question_details.clear();
		//answertlist.clear();
		try{
			HttpPost hp = new HttpPost(WebUtil.HOST+WebUtil.question_detail);
			hp.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			HttpResponse hr = new DefaultHttpClient().execute(hp);
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(hr.getEntity().getContent());      
			document.getDocumentElement().normalize();  
			   
			//get question
			NodeList elements = document.getElementsByTagName("question");
			iElementLength=elements.getLength();
			if (iElementLength != 1 ) return;
			Element element = (Element) elements.item(0);
			element.normalize();
			String type=parsesearch(element,"type");//問題或回答
			String discussId = parsesearch(element,"discussId");
			String title=parsesearch(element,"title");//標題
			String content=parsesearch(element,"content");//內容
			String degree=parsesearch(element,"degree");//年級
			String subject=parsesearch(element,"subject");//科目
			String date=parsesearch(element,"date");//日期
			String status=parsesearch(element,"status");//進行中或已終止
			String askerId=parsesearch(element,"askerId");
			String asker=parsesearch(element,"asker");
			String imagelist=parsesearch(element,"image");//圖片名稱
			ArrayList<String> blist = new ArrayList<String>();
			String imageurl = "";
			String [] temp = null; 
			blist.clear();
			if (!imagelist.equals(" ")){
				temp = imagelist.split(",");
				imageurl=WebUtil.IMAGEHOST+askerId+"/"+temp[0];
				for( String tmp:temp){
					blist.add(WebUtil.IMAGEHOST+askerId+"/"+tmp);
				}
			}else{
				blist.add(imageurl);
			}
			
			question_details.put( "type", type);
			question_details.put( "discussId", discussId);
			question_details.put( "title", title);
			question_details.put( "content", content);
			question_details.put( "degree", degree);
			question_details.put( "subject", subject);
			question_details.put( "date", date);
			question_details.put( "status", status);
			question_details.put( "imageurl", imageurl);
			ArrayList<String> alist = new ArrayList<String>();
			alist.add(imageurl);
			HashMap<String,Object> item = new HashMap<String,Object>();
			item.put( "seq", "0");
      		item.put( "type", type);
      		item.put( "content", content);
      		item.put( "date", date);
      		item.put( "imageurl", imageurl);
      		item.put( "asker", asker);
      		item.put( "images", blist.toArray(new String[0]));
      		if(answertlist.size()>0){
      			answertlist.set(0, item);
      		}else{
      			answertlist.add(item);
      		}
			//get answers
			elements = document.getElementsByTagName("answer");
			iElementLength=elements.getLength();
			if (iElementLength ==0 ) {
				answerimageurls=(String[]) alist.toArray(new String[0]);
				while (answertlist.size()>iElementLength+1){
					answertlist.remove(answertlist.size()-1);
				}				
				return;
			}
			for (int i = 0; i < iElementLength ; i++) {
				element = (Element) elements.item(i);
				element.normalize();
				String seq=parsesearch(element,"seq");//回答序號
				type=parsesearch(element,"type");//發問或回答
				content=parsesearch(element,"content");//回答內容
				date=parsesearch(element,"date");//日期
				imagelist=parsesearch(element,"image");//圖片名稱
				askerId=parsesearch(element,"replierId");
				asker = parsesearch(element,"replier");
				imageurl = "";
				blist.clear();
				temp = null;  
				if (!imagelist.equals(" ")){
					temp = imagelist.split(",");
					imageurl=WebUtil.IMAGEHOST+askerId+"/"+temp[0];
					for( String tmp:temp){
						blist.add(WebUtil.IMAGEHOST+askerId+"/"+tmp);
					}
				}else{
					blist.add(imageurl);
				}
				alist.add(imageurl);
				item = new HashMap<String,Object>();
	      		item.put( "seq", seq);
	      		item.put( "type", type);
	      		item.put( "content", content);
	      		item.put( "imageurl", imageurl);
	      		item.put( "date", date);
	      		item.put( "asker", asker);
	      		item.put( "images", blist.toArray(new String[0]));
	      		if(answertlist.size()<=i+1){
	      			answertlist.add(item);
	      		}else{
	      			answertlist.set(i+1, item);
	      		}
			}
			while (answertlist.size()>iElementLength+1){
				answertlist.remove(answertlist.size()-1);
			}
			answerimageurls=(String[]) alist.toArray(new String[0]);
		}catch (Exception e) {
			System.out.println("error message:" + e.getMessage());
		}
	}
	public static void searchList(List<NameValuePair> params){
		int iElementLength = 0;
		//searchlist.clear();
		try{
			HttpPost hp = new HttpPost(WebUtil.HOST+WebUtil.search_service);
			hp.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			HttpResponse hr = new DefaultHttpClient().execute(hp);
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(hr.getEntity().getContent());      
			document.getDocumentElement().normalize();  
			NodeList elements = document.getElementsByTagName("search");
			iElementLength=elements.getLength();
			if (iElementLength == 0) {
				searchlist.clear();
				return;
			}
			for (int i = 0; i < iElementLength ; i++) {
				Element element = (Element) elements.item(i);
				element.normalize();
				String discussID=parsesearch(element,"discussID");
				String theme=parsesearch(element,"theme");
				String subject=parsesearch(element,"subject");
				String degree=parsesearch(element,"degree");
				String title=parsesearch(element,"title");
				String asker=parsesearch(element,"asker");
				String date=parsesearch(element,"date");
				//取得imageurl
			    String imageurl = getimageurl(discussID);
				HashMap<String,Object> item = new HashMap<String,Object>();
	      		item.put( "discussID", discussID);
	      		item.put( "theme", theme);
	      		item.put( "subject", subject);
	      		item.put( "degree", degree);
	      		item.put( "title", title);
	      		item.put( "asker", asker);
	      		item.put( "date", date);
	      		item.put( "imageurl", imageurl);
	      		if (searchlist.size()<=i){
	      			searchlist.add(item);
	      		} else{
	      			searchlist.set(i, item);
	      		}
			}
			while (searchlist.size()>iElementLength){
				searchlist.remove(searchlist.size()-1);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("error message:" + e.getMessage());
		}
	}
	private static String getimageurl(String discussID) {
		final List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("qid",discussID));
		String imagelist = "";
		String askerId = "";
		String rtn = "";
		try {
			HttpPost hp = new HttpPost(WebUtil.HOST+WebUtil.question_detail);
			hp.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			HttpResponse hr = new DefaultHttpClient().execute(hp);
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(hr.getEntity().getContent());      
			document.getDocumentElement().normalize();  
			NodeList elements = document.getElementsByTagName("question");
			if (elements.getLength() == 0 ) return rtn;
			Element em = (Element) elements.item(0);
			em.normalize();
			imagelist=parsesearch(em,"image");
			askerId=parsesearch(em,"askerId");
			if (imagelist.equals(" ")) return rtn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		String [] temp = null;  
		if (!imagelist.equals(null)){
			temp = imagelist.split(",");
			return WebUtil.IMAGEHOST+askerId+"/"+temp[0];
		}
		return rtn;
	}
	public static InputStream getis(final String url) throws IOException {
	    HttpClient httpClient = new DefaultHttpClient();
	    HttpParams httpParams = httpClient.getParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
	    HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT);
	    HttpPost httpget = new HttpPost(url);
	    HttpResponse httpResponse = httpClient.execute(httpget);
	    StatusLine statusLine = httpResponse.getStatusLine();
	    if(! statusLine.getReasonPhrase().equals("OK")) {
	        throw new IOException(String.format("Request failed with %s", statusLine));
	    }
	    HttpEntity entity = httpResponse.getEntity();
	    return entity.getContent();
	}
	public static void questionList(List<NameValuePair> params,String tag){
		try{
			
			HttpPost hp = new HttpPost(WebUtil.HOST+WebUtil.phone_questionlist);
			hp.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			HttpResponse hr = new DefaultHttpClient().execute(hp);
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(hr.getEntity().getContent());      
			document.getDocumentElement().normalize();  
			   
			NodeList elements = document.getElementsByTagName(tag);
			int iElementLength=elements.getLength();
			if (iElementLength ==0 ) {
				if(tag=="question") questionlist.clear();
				if(tag=="reply") replylist.clear();
				if(tag=="Subscribe") subscribelist.clear();
				return;
			}
			for (int i = 0; i < iElementLength ; i++) {
				Element element = (Element) elements.item(i);
				element.normalize();
				if(tag=="question"){
					String discussID=parsesearch(element,"discussID");
					String theme=parsesearch(element,"theme");
					String subject=parsesearch(element,"subject");
					String degree=parsesearch(element,"degree");
					String title=parsesearch(element,"title");
					String asker=parsesearch(element,"asker");
					String date=parsesearch(element,"date");
					//取得imageurl
				    String imageurl = getimageurl(discussID);
					HashMap<String,Object> item = new HashMap<String,Object>();
		      		item.put( "discussID", discussID);
		      		item.put( "theme", theme);
		      		item.put( "subject", subject);
		      		item.put( "degree", degree);
		      		item.put( "title", title);
		      		item.put( "asker", asker);
		      		item.put( "date", date);
		      		item.put( "imageurl", imageurl);
		      		if (questionlist.size()<=i){
		      			questionlist.add(item);
		      		} else{
		      			questionlist.set(i, item);
		      		}
				}
				if(tag=="reply"){
					int exists=0;
					String replyID=parsesearch(element,"replyID");
					String discussID=parsesearch(element,"discussID");
					String theme=parsesearch(element,"theme");
					String subject=parsesearch(element,"subject");
					String degree=parsesearch(element,"degree");
					String title=parsesearch(element,"title");
					String replier=parsesearch(element,"replier");
					String date=parsesearch(element,"date");
					//取得imageurl
				    String imageurl = getimageurl(discussID);
					HashMap<String,Object> item = new HashMap<String,Object>();
					item.put( "replyID", replyID);
		      		item.put( "discussID", discussID);
		      		item.put( "theme", theme);
		      		item.put( "subject", subject);
		      		item.put( "degree", degree);
		      		item.put( "title", title);
		      		item.put( "replier", replier);
		      		item.put( "date", date);
		      		item.put( "imageurl", imageurl);
		      		for(HashMap<String,Object> item2:replylist) {
		      			if(item2.get("discussID").equals(discussID)){
		      				exists=1;
		      				continue;
		      			}
		      		}
		      		if(exists==0) {
			      		if (replylist.size()<=i){
			      			replylist.add(item);
			      		} else{
			      			replylist.set(i, item);
			      		}
		      		}
				}
				if(tag=="Subscribe"){
					String discussID=parsesearch(element,"discussID");
					String theme=parsesearch(element,"theme");
					String subject=parsesearch(element,"subject");
					String degree=parsesearch(element,"degree");
					String title=parsesearch(element,"title");
					String asker=parsesearch(element,"asker");
					String date=parsesearch(element,"date");
					//取得imageurl
				    String imageurl = getimageurl(discussID);
					HashMap<String,Object> item = new HashMap<String,Object>();
		      		item.put( "discussID", discussID);
		      		item.put( "theme", theme);
		      		item.put( "subject", subject);
		      		item.put( "degree", degree);
		      		item.put( "title", title);
		      		item.put( "asker", asker);
		      		item.put( "date", date);
		      		item.put( "imageurl", imageurl);
		      		if (subscribelist.size()<=i){
		      			subscribelist.add(item);
		      		} else{
		      			subscribelist.set(i, item);
		      		}					
				}
			}

			if (tag == "question") {
				while (questionlist.size() > iElementLength) {
					questionlist.remove(questionlist.size() - 1);
				}
			}

			if (tag == "reply") {
				while (replylist.size() > iElementLength) {
					replylist.remove(replylist.size() - 1);
				}
			}
			if (tag == "Subscribe") {
				while (subscribelist.size() > iElementLength) {
					subscribelist.remove(subscribelist.size() - 1);
				}
			}
			
		}catch (Exception e) {
			System.out.println("error message:" + e.getMessage());
		}
	}
	public static void homeList(String serviceurl,String tag){
		DocumentBuilder documentBuilder = null;  
		Document document = null;
		NodeList elements = null;
		int iElementLength = 0;
		try{
			if(tag=="news") {
				URL urlUpdate = new URL(serviceurl);
				documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				document = documentBuilder.parse(getis(urlUpdate.toString()));
				document.getDocumentElement().normalize();  
				elements = document.getElementsByTagName(tag);
				iElementLength=elements.getLength();
				if (iElementLength ==0 ) {
					newslist.clear();
					return;
				}
				for (int i = 0; i < iElementLength ; i++) {
					Element element = (Element) elements.item(i);
					element.normalize();
					String id=parsesearch(element,"id");
					String title=parsesearch(element,"title");
					String releasedate=parsesearch(element,"releasedate");
					String content=getnewscontent(id);
					HashMap<String,Object> item = new HashMap<String,Object>();
					item.put( "id", id);
					item.put( "title", title);
					item.put( "releasedate", releasedate);
					item.put( "content", content);
		      		if (newslist.size()<=i){
		      			newslist.add(item);
		      		} else{
		      			newslist.set(i, item);
		      		}					
				}
				while (newslist.size() > iElementLength) {
					newslist.remove(newslist.size() - 1);
				}				
			}
			//for newreplylist
			if(tag=="reply") {
				final List<NameValuePair> params = new ArrayList<NameValuePair>();
           		params.add(new BasicNameValuePair("kw",""));
           		params.add(new BasicNameValuePair("dg","1"));
           		params.add(new BasicNameValuePair("sub","0"));
				HttpPost hp = new HttpPost(WebUtil.HOST+WebUtil.search_service);
				hp.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
				HttpResponse hr = new DefaultHttpClient().execute(hp);
				documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				document = documentBuilder.parse(hr.getEntity().getContent());
				document.getDocumentElement().normalize();  
				elements = document.getElementsByTagName("search");
				iElementLength = elements.getLength();
				if (iElementLength == 0 ) {
					newreplylist.clear();
					return;
				}
				for (int i = 0; i < iElementLength ; i++) {
					Element element = (Element) elements.item(i);
					element.normalize();
					//for newreplylist
					String discussID=parsesearch(element,"discussID");
					String theme=parsesearch(element,"theme");
					String subject=parsesearch(element,"subject");
					String degree=parsesearch(element,"degree");
					String title=parsesearch(element,"title");
					String asker=parsesearch(element,"asker");
					String date=parsesearch(element,"date");
					//取得imageurl
				    String imageurl = getimageurl(discussID);
					HashMap<String,Object> item = new HashMap<String,Object>();
		      		item.put( "discussID", discussID);
		      		item.put( "theme", theme);
		      		item.put( "subject", subject);
		      		item.put( "degree", degree);
		      		item.put( "title", title);
		      		item.put( "asker", asker);
		      		item.put( "date", date);
		      		item.put( "imageurl", imageurl);
		      		if (newreplylist.size()<=i){
		      			newreplylist.add(item);
		      		} else{
		      			newreplylist.set(i, item);
		      		}			      		
				}
				while (newreplylist.size() > iElementLength) {
					newreplylist.remove(newreplylist.size() - 1);
				}					
			}
		}catch (Exception e) {
			System.out.println("error message:" + e.getMessage());
		}
	}
	
	//取得themelist
	public static void themeXmlList(){
		int iElementLength = 0;
		themeXmlList.clear();
		try{
			HttpPost hp = new HttpPost(WebUtil.HOST+WebUtil.theme_list);
			HttpResponse hr = new DefaultHttpClient().execute(hp);
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(hr.getEntity().getContent());      
			document.getDocumentElement().normalize();  
			NodeList elements = document.getElementsByTagName("theme");
			iElementLength=elements.getLength();
			if (iElementLength == 0 ) return;
			
			Theme object = null;
			themeList = new String[iElementLength];
			String themeID = null; 
			String subjectID = null;
			String degreeID = null;
			String themeTit = null;
			for (int i = 0; i < iElementLength ; i++) {
				Element element = (Element) elements.item(i);
				element.normalize();
				themeID=parsesearch(element,"themeID");
				subjectID=parsesearch(element,"subjectID");
				degreeID=parsesearch(element,"degreeID");
				themeTit=parsesearch(element,"themeTit");				
				object = new Theme();
				object.setThemeID(themeID);
				object.setSubjectID(subjectID);
				object.setDegreeID(degreeID);
				object.setThemeTit(themeTit);
				themeXmlList.add(object);
				themeList[i] = themeTit;
			}
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("error message:" + e.getMessage());
		}
	}
	
	private static String getnewscontent(String id) {
		String rtn = null;
		final List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("nid",id));
		try {
			HttpPost hp = new HttpPost(WebUtil.HOST+WebUtil.phone_newscontent);
			hp.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			HttpResponse hr = new DefaultHttpClient().execute(hp);
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(hr.getEntity().getContent());
			document.getDocumentElement().normalize();  
			NodeList elements = document.getElementsByTagName("news");
			int iElementLength = elements.getLength();
			if (iElementLength != 1 ) return rtn;
			Element element = (Element) elements.item(0);
			element.normalize();
			rtn = parsesearch(document,"content");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtn;
	}
	private static String parsesearch(Element element, String string) {
		String rtn=" ";
		try {
			if(string.equals("imageurl")) {
				rtn = imageurl(element,string);
			}else{
				rtn = element.getElementsByTagName(string).item(0).getFirstChild().getNodeValue();
			}
		}catch(Exception e){
			return rtn;
		}
		return rtn;
	}
	private static String parsesearch(Document dcm, String string) {
		String rtn=" ";
		try {
				rtn = dcm.getElementsByTagName(string).item(0).getFirstChild().getNodeValue();
		}catch(Exception e){
			return rtn;
		}
		return rtn;
	}

	private static String imageurl(Element element, String string) {
		String url = element.getElementsByTagName(string).item(0).getFirstChild().getNodeValue();
		String id = element.getElementsByTagName(string).item(0).getAttributes().getNamedItem("id").getNodeValue();
		String act = element.getElementsByTagName(string).item(0).getAttributes().getNamedItem("act").getNodeValue();
		String type = element.getElementsByTagName(string).item(0).getAttributes().getNamedItem("type").getNodeValue();
		StringBuilder str = new StringBuilder();
		str.append(WebUtil.HOST).append(url).append("id=").append(id).append("&act=").append(act).append("&type=").append(type);
		return str.toString();
	}
	public static Boolean LoginCheck(List<NameValuePair> params) {
		DocumentBuilder documentBuilder = null;  
		Document document = null;
		NodeList elements = null;
		int iElementLength = 0;
		try{
			//改httppost
			HttpPost hp = new HttpPost(WebUtil.HOST+WebUtil.phone_login);
			hp.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
            HttpResponse hr = new DefaultHttpClient().execute(hp);
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			document = documentBuilder.parse(hr.getEntity().getContent());
			document.getDocumentElement().normalize();  
			elements = document.getElementsByTagName("status");
			iElementLength=elements.getLength();
			if (iElementLength != 1 ) return false;
			Element element = (Element) elements.item(0);
			element.normalize();
			String check = parsesearch(document,"status");
			if (check.equals("0")){
				//取得姓名
				String userid=parsesearch(document,"userID");
				//取得pushid
				String pushid=parsesearch(document,"pushID");
				
				logininfo.clear();
				logininfo.put( "userid", userid);
				logininfo.put( "pushid", pushid);
				return true;
			}
			else return false;
		}catch (Exception e) {
			System.out.println("error message:" + e.getMessage());
			return false;
		}
	}
	public static boolean register(List<NameValuePair> params) {
		DocumentBuilder documentBuilder = null;  
		Document document = null;
		NodeList elements = null;
		int iElementLength = 0;
		try{
			HttpPost hp = new HttpPost(WebUtil.HOST+WebUtil.phone_register);
			hp.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			HttpResponse hr = new DefaultHttpClient().execute(hp);
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			document = documentBuilder.parse(hr.getEntity().getContent());      
			document.getDocumentElement().normalize();  
			elements = document.getElementsByTagName("status");
			iElementLength=elements.getLength();
			if (iElementLength != 1 ) return false;
			Element element = (Element) elements.item(0);
			element.normalize();
			
			String check = element.getFirstChild().getNodeValue().toString();
			if (check.equals("0")) return true;
			else return false;
		}catch (Exception e) {
			System.out.println("error message:" + e.getMessage());
			return false;
		}
	}
	public static Bitmap photo;
	public static Bitmap arrow;
	public static ByteArrayOutputStream stream = new ByteArrayOutputStream();
	public static void setDefault(Context context) {
		photo=BitmapFactory.decodeResource(context.getResources(),R.drawable.default_photo);
		photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);
		arrow=BitmapFactory.decodeResource(context.getResources(),R.drawable.arrow);
	}
	public static Bitmap getImage(String imageurl,int bound){
		Bitmap image=null;
		InputStream is=null;
		try {
			image=null;
			System.gc();
			is = fetch(imageurl);
			if(is.equals(null)) return null;
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPurgeable = true;
			options.inInputShareable = true;
			image = BitmapFactory.decodeStream(is,null,options);
			is.close();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		if(image instanceof Bitmap){
			image = getResizedBitmap(image,bound);
			image.compress(Bitmap.CompressFormat.JPEG, 75, ParseXml.stream);
		}
		return image;
	}
	public static Bitmap getImage(String imageurl){
		Bitmap image=null;
		InputStream is=null;
		try {
			is = fetch(imageurl);
			if(is.equals(null)) return null;
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (MalformedURLException e1) {
			return null;
		} catch (Exception e1) {
			return null;
		}
		return image;
	}
	public static InputStream fetch(String address) throws MalformedURLException,IOException {
	    HttpGet httpRequest = new HttpGet(URI.create(address) );
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
	    if(response.getStatusLine().getStatusCode()!=200) return null;
	    InputStream is = response.getEntity().getContent();
	    return is;
	}

	public static Bitmap getResizedBitmap(Bitmap bitmap, int bound) {

        int originWidth  = bitmap.getWidth();
        int originHeight = bitmap.getHeight();

        // no need to resize
        if (originWidth < bound && originHeight < bound) {
            return bitmap;
        }

        int width  = originWidth;
        int height = originHeight;

        if (originWidth > originHeight) {
            width = bound;

            double i = originWidth * 1.0 / originHeight;
            height = (int) Math.floor(width / i);
        } else {
        	height = bound;

            double i = originHeight* 1.0 / originWidth;
            width = (int) Math.floor(height / i);
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return bitmap;
    }
	
	public static void showXML(HttpPost hp){
		//
		try {
			System.out.println(new String(EntityUtils.toString(new DefaultHttpClient().execute(hp).getEntity()).getBytes("ISO-8859-1"), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}