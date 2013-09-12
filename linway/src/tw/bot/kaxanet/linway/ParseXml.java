package tw.bot.kaxanet.linway;

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
import java.util.Map;

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
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import tw.bot.kaxanet.linway.model.CountryContent;
import tw.bot.kaxanet.linway.model.DiscussContent;
import tw.bot.kaxanet.linway.model.DiscussListItem;
import tw.bot.kaxanet.linway.model.DiscussReply;
import tw.bot.kaxanet.linway.model.InsideContent;
import tw.bot.kaxanet.linway.model.InsideSubTitleListItem;
import tw.bot.kaxanet.linway.model.InsidelistItem;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ParseXml {
	private static String TAG = "tw.bot.kaxanet.linway";
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
	public static ArrayList<DiscussListItem> discusslist=new ArrayList<DiscussListItem>();
	public static DiscussContent discussContent = null;
	public static ArrayList<DiscussReply> discussReplylist=new ArrayList<DiscussReply>();
	public static ArrayList<InsidelistItem> insidelist  = new ArrayList<InsidelistItem>();
	public static InsideContent insideContent = null;
	public static CountryContent countryContent = null;
	
	public static HashMap<String,Object> question_details=new HashMap<String,Object>();

    public static String[] themeList = null;
    public static Map<String,String> themeMap  = new HashMap<String,String>();
	private static final int TIMEOUT = 180000;
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static void questionandanswer(List<NameValuePair> params) {
		int iElementLength = 0;
		question_details.clear();
		answertlist.clear();
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
      		answertlist.add(item);
			//get answers
			elements = document.getElementsByTagName("answer");
			iElementLength=elements.getLength();
			if (iElementLength ==0 ) {
				answerimageurls=(String[]) alist.toArray(new String[0]);
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
	      		answertlist.add(item);
			}
			answerimageurls=(String[]) alist.toArray(new String[0]);
		}catch (Exception e) {
			System.out.println("error message:" + e.getMessage());
		}
	}
	public static void searchList(List<NameValuePair> params){
		int iElementLength = 0;
		try{
			HttpPost hp = new HttpPost(WebUtil.HOST+WebUtil.search_service);
			hp.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			HttpResponse hr = new DefaultHttpClient().execute(hp);
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(hr.getEntity().getContent());      
			document.getDocumentElement().normalize();  
			NodeList elements = document.getElementsByTagName("search");
			iElementLength=elements.getLength();
			if (iElementLength == 0 ) {
				searchlist.clear();
				return;
			}
			for (int i = 0; i < iElementLength ; i++) {
				Element element = (Element) elements.item(i);
				element.normalize();
				String discussID=parsesearch(element,"id");
				String searchType=parsesearch(element,"searchType");
				String title=parsesearch(element,"title");
				String releasedate=parsesearch(element,"releasedate");


				HashMap<String,Object> item = new HashMap<String,Object>();
	      		item.put( "discussID", discussID);
	      		item.put( "searchType", searchType);
	      		item.put( "title", title);
	      		item.put( "releasedate", releasedate);
	      		
	      		if(searchlist.size()<=i){
	      			searchlist.add(item);
	      		}else {
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
			if(tag=="question") questionlist.clear();
			if(tag=="reply") replylist.clear();
			if(tag=="Subscribe") subscribelist.clear();
			
			HttpPost hp = new HttpPost(WebUtil.HOST+WebUtil.phone_questionlist);
			hp.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			HttpResponse hr = new DefaultHttpClient().execute(hp);
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(hr.getEntity().getContent());      
			document.getDocumentElement().normalize();  
			   
			NodeList elements = document.getElementsByTagName(tag);
			int iElementLength=elements.getLength();
			if (iElementLength ==0 ) return;
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
		      		questionlist.add(item);
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
		      		if(exists==0) replylist.add(item);
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
					subscribelist.add(item);
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
				Log.d(TAG, "get newslist");
				URL urlUpdate = new URL(serviceurl);
				documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				InputStream is =getis(urlUpdate.toString());
				document = documentBuilder.parse(is);
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
					}else {
						newslist.set(i, item);
					}
				}
				while (newslist.size()>iElementLength){
					newslist.remove(newslist.size()-1);
				}
				try {
					is.close();
				} catch (Exception e) {
					Log.e(TAG, "InputStream close Exception",e);
				}
				Log.d(TAG, String.valueOf(newslist.size()));
				Log.d(TAG, "get newslist done");
			}
			//for newreplylist
			if(tag=="reply") {
				newreplylist.clear();
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
				if (iElementLength == 0 ) return;
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
		      		newreplylist.add(item);
				}
			}
		}catch (Exception e) {
			Log.e(TAG, "homelist error",e);
		}
	}
	
	//取得themelist
	public static void themeXmlList(){
		if (themeXmlList.size()==0){
			themeXmlList.clear();
			try{
				URL url = new URL(WebUtil.HOST+WebUtil.theme_list);
				List<Theme> list = mapper.readValue(url, new TypeReference<List<Theme>>() {
				});
				themeXmlList.addAll(list);
				themeList = new String[list.size()];
				for (int i = 0; i<list.size() ; i++) {
					themeList[i] = list.get(i).getTitle();
					themeMap.put(list.get(i).getId(), list.get(i).getTitle());
				}
			}catch (Exception e) {
				e.printStackTrace();
				System.out.println("error message:" + e.getMessage());
			}
		}
	}
	

	/**
	 * 根據tid取得文章列表
	 * @param tid
	 */
	public static void discussList(String tid){		
		try{
			URL url = new URL(WebUtil.HOST+WebUtil.phone_discusslist+"?tid="+tid);
			List<Map<String,Object>> list = mapper.readValue(url, List.class);
			DiscussListItem item = null;
			for (int i=0;i<list.size();i++){
				item = new DiscussListItem();
				item.setId(list.get(i).get("id").toString());
				item.setNickname(list.get(i).get("nickname").toString());
				item.setPost_time(list.get(i).get("post_time").toString());
				item.setTheme_id(list.get(i).get("theme_id").toString());
				item.setTitle(list.get(i).get("title").toString());
				item.setReplyCount(list.get(i).get("ReplyCount").toString());
				if (discusslist.size()<=i){
					discusslist.add(item);
				}else {
					discusslist.set(i, item);
				}
			}
			while (discusslist.size()>list.size()){
				discusslist.remove(discusslist.size()-1);
			}
			
			list = null;
		}catch (Exception e) {
			Log.e(TAG, "get discussList error",e);
		}
	}
	
	/**
	 * 取得文章內容
	 * @param did
	 * @return
	 */
	public static DiscussContent getDiscussContent(String did){
		discussContent = null;
		try{
			Map<String,Object> map = null;
			URL url = new URL(WebUtil.HOST+WebUtil.phone_discusscontent+"?did="+did);
			map = mapper.readValue(url, Map.class);
			discussContent = new DiscussContent();
			discussContent.setDisID(map.get("DisID").toString());
			discussContent.setMbid(map.get("Mbid").toString());
			discussContent.setMbNickname(map.get("MbNickname").toString());
			discussContent.setMbAvatar(map.get("MbAvatar").toString());
			discussContent.setMbGender(map.get("MbGender").toString());
			discussContent.setTitle(map.get("Title").toString());
			discussContent.setContent(map.get("Content").toString());
			discussContent.setDisEnb(map.get("DisEnb").toString());
			discussContent.setDisRpyEnb(map.get("DisRpyEnb").toString());
			discussContent.setPostTime(map.get("PostTime").toString());		
			map = null;
		}catch (Exception e) {
			Log.e(TAG, "get discussContent error",e);
		}		
		
		return discussContent;
	}
	
	
	public static void discussReply(String did){		

		try{
			getDiscussContent(did);
			DiscussReply item = null;
			//把發問加進LIST
			item = new DiscussReply();
			item.setContent(discussContent.getContent());
			item.setNickname(discussContent.getMbNickname());
			item.setPost_time(discussContent.getPostTime());
			if (discussReplylist.size()>0){
				discussReplylist.set(0, item);
			}else {
				discussReplylist.add(item);
			}
			

			URL url = new URL(WebUtil.HOST+WebUtil.phone_discussreply+"?did="+did);
			List<Map<String,Object>> list =  null;
			
			try {
				list = mapper.readValue(url, List.class);
				
				for (int i=0;i<list.size();i++){
					item = new DiscussReply();
					item.setId(list.get(i).get("id").toString());
					item.setAvatar(list.get(i).get("Avatar").toString());
					item.setNickname(list.get(i).get("Nickname").toString());
					item.setPost_time(list.get(i).get("post_time").toString());
					item.setGender(list.get(i).get("Gender").toString());
					item.setContent(list.get(i).get("content").toString());
					if(discussReplylist.size()<=i+1){
						discussReplylist.add(item);
					}else{
						discussReplylist.set(i+1, item);
					}
				}
				while (discussReplylist.size()>list.size()+1){
					discussReplylist.remove(discussReplylist.size()-1);
				}				
				
			} catch (Exception e) {
				Log.e(TAG, "no reply!!!");
			}
			
			if (list == null){
				while (discussReplylist.size()>1){
					discussReplylist.remove(discussReplylist.size()-1);
				}
			}
			
			list = null;
			
		}catch (Exception e) {
			Log.e(TAG, "get discussReply error",e);
		}
	}
	
	public static void getInsidelist(){
		
		try{
			URL url = new URL(WebUtil.HOST+WebUtil.phone_insidelist);
			List<Map<String,Object>> list = mapper.readValue(url.openStream(), List.class);
			InsidelistItem insidelistItem = null;
			InsideSubTitleListItem subListItem = null;
			List<InsideSubTitleListItem>  subTitleList = null;
			for (int i=0;i<list.size();i++){
				insidelistItem = new InsidelistItem();
				insidelistItem.setId(list.get(i).get("id").toString());
				insidelistItem.setStatus(list.get(i).get("status").toString());
				insidelistItem.setTitle(list.get(i).get("title").toString());
				
				List<Map<String,Object>> sublist = (List<Map<String,Object>>)list.get(i).get("SubTitleList");
				subTitleList = new ArrayList<InsideSubTitleListItem>();
				for (int j=0;j<sublist.size();j++){
					subListItem = new InsideSubTitleListItem();
					subListItem.setId(sublist.get(j).get("id").toString());
					subListItem.setReleasedate(sublist.get(j).get("releasedate").toString());
					subListItem.setTheme_id(sublist.get(j).get("theme_id").toString());
					subListItem.setTitle(sublist.get(j).get("title").toString());
					subTitleList.add(subListItem);
				}
				
				insidelistItem.setSubTitleList(subTitleList);
				if (insidelist.size()<=i){
					insidelist.add(insidelistItem);
				}else {
					insidelist.set(i, insidelistItem);
				}
			}
			while (insidelist.size()>list.size()){
				insidelist.remove(insidelist.size()-1);
			}
			
		}catch (Exception e) {
			Log.e(TAG, "get insidelistItem error",e);
		}
		
	}
	
	public static InsideContent getInsideContent(String id) {
		
		try{
			Map<String,Object> map = null;
			URL url = new URL(WebUtil.HOST+WebUtil.phone_insidecontent+"?id="+id);
			insideContent = mapper.readValue(url, InsideContent.class);

		}catch (Exception e) {
			Log.e(TAG, "get discussContent error",e);
		}		
		
		return insideContent;
	}
	
	public static CountryContent getCountryContent(String id) {
		
		try{
			Map<String,Object> map = null;
			URL url = new URL(WebUtil.HOST+WebUtil.phone_countrycountent+"?id="+id);
			countryContent = mapper.readValue(url, CountryContent.class);

		}catch (Exception e) {
			Log.e(TAG, "get discussContent error",e);
		}		
		
		return countryContent;
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
				//mailValidate
				String mailValidate =  parsesearch(document,"mailValidate");
				//accountLock
				String accountLock =  parsesearch(document,"accountLock");
				//permission
				String permission =  parsesearch(document,"permission");
				
				logininfo.clear();
				logininfo.put( "userid", userid);
				logininfo.put( "pushid", pushid);
				logininfo.put( "mailValidate", mailValidate);
				logininfo.put( "accountLock", accountLock);		
				logininfo.put( "permission", permission);	
				
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