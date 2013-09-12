package tw.bot.kaxanet.linway;

import java.util.ArrayList;
import java.util.List;

import tw.bot.kaxanet.linway.MyListView.OnRefreshListener;
import tw.bot.kaxanet.linway.model.CountryContent;
import tw.bot.kaxanet.linway.model.InsideContent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class InsideDetail extends RefActivity{
	private BaseAdapter adapter;
	private String discussID;
	private boolean isCountry;
	private int inside_item_idx;
	WebView webview1;

	private InsideContent insideContent;
	private CountryContent countryContent;
	
	private static final String _ENCODING = "UTF-8";
	private static final String _MIMETYPE = "text/html"; 
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.inside_detail);
    	
    	discussID=getIntent().getExtras().getString("discussID");
    	isCountry = getIntent().getExtras().getBoolean("isCountry");
    	inside_item_idx=getIntent().getExtras().getInt("inside_item_idx");
   		refresh_data(InsideDetail.this);
    }
    private void refresh_data(Context context) {
    	pd = ProgressDialog.show(
    			this, 
    			null,
    			"Loading..Please wait...."
    	);
    	new Thread(){ 
    		@Override
    		public void run(){
    			if (isCountry){
    				countryContent = ParseXml.getCountryContent(discussID);
    			}else{
    				insideContent = ParseXml.getInsideContent(discussID);
    			}
    			Message m = new Message();
    			m.what = 222;
    			myHandler.sendMessage(m);
    		}
    	}.start();
    }
    
	private void findview() {
		Button l_btn = (Button) findViewById(R.id.title_left_btn);
		Button r_btn = (Button) findViewById(R.id.title_right_btn);


		l_btn.setOnClickListener(btnclk);

		webview1 =  (WebView)findViewById(R.id.webView1);
		//·s¼WSimpleAdapter


		webview1.getSettings().setPluginsEnabled(true);
		webview1.getSettings().setJavaScriptEnabled(true);
		if (isCountry){
			webview1.loadDataWithBaseURL(WebUtil.BASEURL,countryContent.getContent(), _MIMETYPE, _ENCODING, null);
		}else{
			webview1.loadDataWithBaseURL(WebUtil.BASEURL,insideContent.getContent(), _MIMETYPE, _ENCODING, null);
		}

	}
    private OnClickListener btnclk = new OnClickListener() {
    	public void onClick(View v){
    		switch(v.getId()) {
    			case R.id.title_left_btn:
    				finish();
    				break;

    			default:
    				break;
    		}
    	}
    };

    @Override
    public void onResume() {
    	super.onResume();
    }
	@Override
    public void onPause() {
    	super.onPause();
    }
	@Override
    public void onDestroy(){
		imageLoader.stop();
    	super.onDestroy();
    }
    ProgressDialog pd;
	Handler myHandler = new Handler() {  
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what) {
			case 222:
				pd.dismiss();
				findview();
				break;
			case 333:
				pd.dismiss();
				break;
			}
		}   
	};
}
