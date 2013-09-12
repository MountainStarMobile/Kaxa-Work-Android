package tw.bot.kaxanet.mathpower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.nostra13.universalimageloader.core.ImageLoadingListener;

import tw.bot.kaxanet.mathpower.MyListView.OnRefreshListener;

import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

public class MyPage extends RefActivity {
	BaseAdapter adapter,adapter1,adapter2;
	final List<NameValuePair> param = new ArrayList<NameValuePair>();
	String email=null;
	String passwd=null;
	String userid=null;
	MyListView listView,listView1,listView2;
	private TabHost thTab;
	private LocalActivityManager localActivityManager;
	Button l_btn,r_btn;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);
        findview();
    }
    
    private void notsetting() {
    	new AlertDialog.Builder(this)
        .setMessage("帳號未設定!!")
        .setPositiveButton("前往設定",
            new DialogInterface.OnClickListener(){
                @Override
				public void onClick(
                    DialogInterface dialoginterface, int i){
                	Intent intent = new Intent();
    		    	intent.setClass(MyPage.this, AccountSetting.class);
    		    	startActivity(intent);
                    }
                })
        .setNegativeButton("離開",
            new DialogInterface.OnClickListener(){
                @Override
				public void onClick(
                    DialogInterface dialoginterface, int i){
	                	ParseXml.questionlist.clear();
	                	ParseXml.replylist.clear();
	                	ParseXml.subscribelist.clear();
	                	adapter.notifyDataSetChanged();
	        			adapter1.notifyDataSetChanged();
	        			adapter2.notifyDataSetChanged();
                    }
                })
        .show();
	}

	@Override
    public void onResume(){
    	super.onResume();
    	SharedPreferences settings = getSharedPreferences("setting", 0);
    	email=settings.getString("email","");
    	passwd = settings.getString("passwd","");
    	userid = settings.getString("userid","");
   		param.add(new BasicNameValuePair("uid",userid));
    	if(email==""||passwd==""){
    		notsetting();
    	}else{
    		if(this.network_ok&&thTab.getCurrentTab()==0&&ParseXml.questionlist.isEmpty()) account_validate("question");
    		if(this.network_ok&&thTab.getCurrentTab()==1&&ParseXml.replylist.isEmpty()) account_validate("reply");
    		if(this.network_ok&&thTab.getCurrentTab()==2&&ParseXml.subscribelist.isEmpty()) account_validate("Subscribe");
    	}
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
    protected void account_validate(final String tag) {
    	if(!this.network_ok) return;
    	this.createCancelProgressDialog();
   		new Thread(){ 
   			@Override
   			public void run(){
   				ParseXml.questionList(param,tag);
   				Message m = new Message();
   				if(tag.equals("question")) m.what = 111;
   				if(tag.equals("reply")) m.what = 222;
   				if(tag.equals("Subscribe")) m.what = 333;
   				myHandler.sendMessage(m);
   			}
   		}.start();
	}

	private void findview() {
		localActivityManager = new LocalActivityManager(this, false);

		thTab = (TabHost) findViewById(R.id.mypage_tab);
		thTab.setup(localActivityManager);
		thTab.addTab(thTab.newTabSpec("question").setIndicator(createTabView(this, "我的問題")).setContent(R.id.listView));
		thTab.addTab(thTab.newTabSpec("reply").setIndicator(createTabView(this, "我的回覆")).setContent(R.id.listView1));
	    thTab.addTab(thTab.newTabSpec("Subscribe").setIndicator(createTabView(this, "我的訂閱")).setContent(R.id.listView2));
	    thTab.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
		      @Override
		      public void onTabChanged(String tabId) {
		    	  if("question".equals(tabId)) {
		    		  if(ParseXml.questionlist.isEmpty())	account_validate("question");
		    	  }
		    	  if("reply".equals(tabId)) {
		    		  if(ParseXml.replylist.isEmpty()) account_validate("reply");
		    	  }
		    	  if("Subscribe".equals(tabId)) {
		    		  if(ParseXml.subscribelist.isEmpty())	account_validate("Subscribe");
		    	  }
		      }
		    });
		listView = (MyListView) findViewById(R.id.listView);
		listView1 = (MyListView) findViewById(R.id.listView1);
		listView2 = (MyListView) findViewById(R.id.listView2);
    	r_btn = (Button) findViewById(R.id.title_right_btn);
    	l_btn = (Button) findViewById(R.id.title_left_btn);
    	r_btn.setOnClickListener(btnclk);
    	l_btn.setOnClickListener(btnclk);
    	//新增SimpleAdapter
    	adapter = new MyPageAdapter(ParseXml.questionlist,1);
    	//ListActivity設定adapter
    	listView.setAdapter( adapter );
    	listView.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				DownloadTask dTask = new DownloadTask("question");
				dTask.execute();
			}
		});
		listView.setOnItemClickListener(lvclk);
    	//新增SimpleAdapter
    	adapter1 = new MyPageAdapter(ParseXml.replylist,2);
    	//ListActivity設定adapter
    	listView1.setAdapter( adapter1 );
    	listView1.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				DownloadTask dTask = new DownloadTask("reply");
				dTask.execute();
			}
		});
    	listView1.setOnItemClickListener(lvclk);
    	//新增SimpleAdapter
    	adapter2 = new MyPageAdapter(ParseXml.subscribelist,1);
    	//ListActivity設定adapter
    	listView2.setAdapter( adapter2 );
    	listView2.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				DownloadTask dTask = new DownloadTask("Subscribe");
				dTask.execute();
			}
		});
    	listView2.setOnItemClickListener(lvclk);
	}
	private static View createTabView(final Context context, final String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_top, null);
        TextView tv = (TextView) view.findViewById(R.id.tabText);
        tv.setText(text);
        return view;
    }
	private OnClickListener btnclk = new OnClickListener() {
    	@Override
		public void onClick(View v){
    		switch(v.getId()) {
    			case R.id.title_right_btn:
    				finish();
    				break;
    			case R.id.title_left_btn:
    				Intent intent = new Intent();
    		    	intent.setClass(MyPage.this, Ask.class);
    		    	intent.putExtra("action_type","q");
    		    	startActivity(intent);
    				break;
    			default:
    				break;
    		}
    	}
    };
    private OnItemClickListener lvclk = new AdapterView.OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent,View view, int position, long id){
			if(position<=0)return;
			Intent intent = new Intent();
			switch(parent.getId()) {
				case R.id.listView:
    				intent.putExtra("discussID",ParseXml.questionlist.get(position-1).get("discussID").toString());
					break;
				case R.id.listView1:
					intent.putExtra("discussID",ParseXml.replylist.get(position-1).get("discussID").toString());
					break;
				case R.id.listView2:
					intent.putExtra("discussID",ParseXml.subscribelist.get(position-1).get("discussID").toString());
					break;
			}
	    	intent.setClass(MyPage.this, DetailsList.class);
	    	startActivity(intent);
		}
	};
	DownloadTask dTask;
    Handler myHandler = new Handler() {  
        @Override
		public void handleMessage(Message msg) {
        	super.handleMessage(msg);
        	switch(msg.what) {
        	case 111:
        		adapter.notifyDataSetChanged();
        		MyPage.this.cancelDialog.dismiss();
        		break;
        	case 222:
        		adapter1.notifyDataSetChanged();
        		MyPage.this.cancelDialog.dismiss();
        		break;
        	case 333:
        		adapter2.notifyDataSetChanged();
        		MyPage.this.cancelDialog.dismiss();
        		break;
        	}
        }   
	};
	class DownloadTask extends AsyncTask<Void, Integer, String>{
		String tag;
		DownloadTask(String t){
			this.tag = t;
		}
		@Override
		protected String doInBackground(Void... params) {
    		ParseXml.questionList(param,tag);
			return "Done";
		}

		@Override
		protected void onPostExecute(String result) {
			if(tag.equals("question")){
				adapter.notifyDataSetChanged();
				listView.onRefreshComplete();
			}
			if(tag.equals("reply")){
				adapter1.notifyDataSetChanged();
				listView1.onRefreshComplete();
			}
			if(tag.equals("Subscribe")){
				adapter2.notifyDataSetChanged();
				listView2.onRefreshComplete();
			}
		}
	}
	public class MyPageAdapter extends BaseAdapter {
		public List<HashMap<String, Object>> data;
		public int idx;
		public MyPageAdapter(List<HashMap<String, Object>> d,int type) {
			this.data=d;
			this.idx=type;
		}
    	@Override
		public int getCount() {
    		return data.size();
    	}
    	@Override
		public Object getItem(int position) {
    		return data.get(position);
    	}
    	@Override
		public long getItemId(int position) {
    		return position;
    	}
    	@Override
    	public View getView(final int position, View view, ViewGroup parent) {
    		final ViewHolder holder;
    		if (view == null) {
    			view = getLayoutInflater().inflate(R.layout.searchlist, null);
    			holder = new ViewHolder();
    			holder.pic = (ImageView) view.findViewById(R.id.imageView1);
    			holder.wait = (ProgressBar) view.findViewById(R.id.loading);
    			holder.label_1 = (TextView) view.findViewById(R.id.label_1);
    			holder.label_2 = (TextView) view.findViewById(R.id.label_2);
    			holder.label_3 = (TextView) view.findViewById(R.id.label_3);
    			holder.label_4 = (TextView) view.findViewById(R.id.label_4);
    			holder.label_5 = (TextView) view.findViewById(R.id.label_5);
    			holder.label_6 = (TextView) view.findViewById(R.id.label_6);
    			holder.arrow = (ImageView) view.findViewById(R.id.arrow);
    			view.setTag(holder);        		
    		} else {
    			holder = (ViewHolder) view.getTag();
    		}
    		holder.label_1.setText(data.get(position).get("title").toString());//顯示文字說明
    		holder.label_2.setText(data.get(position).get("subject").toString());
    		holder.label_3.setText(data.get(position).get("theme").toString());
    		holder.label_4.setText(data.get(position).get("degree").toString());
    		holder.label_5.setText(data.get(position).get("date").toString());
    		if(idx==1){
    			holder.label_6.setText(data.get(position).get("asker").toString());
    		}
    		if(idx==2){
        		holder.label_6.setText(data.get(position).get("replier").toString());
    		}
    		holder.arrow.setImageBitmap(BitmapFactory.decodeResource(getBaseContext().getResources(),tw.bot.kaxanet.mathpower.R.drawable.arrow));
    		String url = data.get(position).get("imageurl").toString();
			if(url == null || url.length() == 0){
				holder.pic.setImageResource(R.drawable.default_photo);
				holder.wait.setVisibility(View.GONE);
				holder.pic.setVisibility(View.VISIBLE);
			}else {
				imageLoader.displayImage(url, holder.pic, options,new ImageLoadingListener() {
					@Override
					public void onLoadingStarted() {
						holder.pic.setVisibility(View.INVISIBLE);
						holder.wait.setVisibility(View.VISIBLE);
					}
					
					@Override
					public void onLoadingFailed() {
						holder.wait.setVisibility(View.GONE);
						holder.pic.setVisibility(View.VISIBLE);
					}
					
					@Override
					public void onLoadingComplete() {
						holder.wait.setVisibility(View.GONE);
						holder.pic.setVisibility(View.VISIBLE);
					}
				});
			}
    		return view;
    	}
    	
    	class ViewHolder{
    		ImageView pic;
    		ProgressBar wait;
    		TextView label_1;
    		TextView label_2;
    		TextView label_3;
    		TextView label_4;
    		TextView label_5;
    		TextView label_6;
    		ImageView arrow;
    	}
    }
}