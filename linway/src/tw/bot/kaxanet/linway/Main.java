package tw.bot.kaxanet.linway;

import java.util.HashMap;
import java.util.List;

import tw.bot.kaxanet.linway.MyListView.OnRefreshListener;
import tw.bot.kaxanet.linway.model.DiscussListItem;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

public class Main extends RefActivity {
	private static String TAG = "tw.bot.kaxanet.linway";
	BaseAdapter adapter,adapter1;
	String url;
	MyListView listView,listView1;
	private TabHost thTab;
	Button l_btn,r_btn;
	TextView maintitle;
	Spinner main_title_spinner;
	ArrayAdapter<String> spinnerAdapter;
	String tid = "1";
	
	private LocalActivityManager localActivityManager;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        url = WebUtil.HOST+WebUtil.phone_home;
        ParseXml.themeXmlList();
        findview();
    }
    @Override
    public void onResume() {
    	super.onResume();
    	Log.d(TAG, "onResume");
    	if(this.network_ok&&thTab.getCurrentTab()==0&&ParseXml.newslist.isEmpty()) get_xml("News");
		if(this.network_ok&&thTab.getCurrentTab()==1&&ParseXml.newreplylist.isEmpty()) get_discussList();
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
   
	private void findview() {
		maintitle = (TextView)findViewById(R.id.maintitle);
		main_title_spinner = (Spinner) findViewById(R.id.main_title_spinner);
		l_btn = (Button) findViewById(R.id.title_left_btn);
		r_btn = (Button) findViewById(R.id.title_right_btn);
		l_btn.setVisibility(View.INVISIBLE);
		localActivityManager = new LocalActivityManager(this, false);

		thTab = (TabHost) findViewById(R.id.main_tab);
		thTab.setup(localActivityManager);
		thTab.addTab(thTab.newTabSpec("News").setIndicator(createTabView(this, this.getString(R.string.lastest_news))).setContent(R.id.listView1));
	    thTab.addTab(thTab.newTabSpec("All").setIndicator(createTabView(this, this.getString(R.string.question_list))).setContent(R.id.listView));
	    
	    spinnerAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, ParseXml.themeList);
	    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    
	    thTab.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
	      @Override
	      public void onTabChanged(String tabId) {
	        if("News".equals(tabId)) {
	        	Log.d(TAG, "onTabChanged");
	        	if(ParseXml.newslist.isEmpty())	get_xml("News");
	        	l_btn.setVisibility(View.INVISIBLE);
	        	maintitle.setVisibility(View.VISIBLE);
	        	main_title_spinner.setVisibility(View.GONE);
	        }else{
	        	if(ParseXml.discusslist.isEmpty())	get_discussList();
	        	l_btn.setVisibility(View.VISIBLE);
	        	r_btn.setVisibility(View.VISIBLE);
	        	maintitle.setVisibility(View.GONE);
	        	main_title_spinner.setVisibility(View.VISIBLE);
	           
	            main_title_spinner.setAdapter(spinnerAdapter);  	        	
	        }
	      }
	    });
		listView = (MyListView) findViewById(R.id.listView);
		listView1 = (MyListView) findViewById(R.id.listView1);
    	l_btn.setOnClickListener(btnclk);
    	r_btn.setOnClickListener(btnclk);
    	main_title_spinner.setOnItemSelectedListener(itemSelectedListener);
    	//新增SimpleAdapter
    	adapter = new MainAdapter(ParseXml.discusslist);
    	//ListActivity設定adapter
    	listView.setAdapter( adapter );
    	listView.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				DownloadAll dTask = new DownloadAll();
				dTask.execute();
			}
		});
    	listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
    		public void onItemClick(AdapterView<?> parent,View view, int position, long id){
    			if(position>0){
    				if (getAccount().isAccountCanPost()){
	    				Intent intent = new Intent();
	    				intent.putExtra("discussID",ParseXml.discusslist.get(position-1).getId());
	    				intent.putExtra("tid", tid);
	    				intent.setClass(Main.this, DetailsList.class);
	    				startActivity(intent);
    				} else {
    					notsetting(Main.this);
    				}
    			}
    		}
    	});
    	//新增SimpleAdapter
    	adapter1 = new NewsAdapter(ParseXml.newslist);
    	//ListActivity設定adapter
    	listView1.setAdapter( adapter1 );
    	listView1.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				DownloadNews dTask = new DownloadNews();
				dTask.execute();
			}
		});
    	listView1.setOnItemClickListener(new AdapterView.OnItemClickListener(){
    		public void onItemClick(AdapterView<?> parent,View view, int position, long id){
    			if(position>0){
    				Intent intent = new Intent();
    		    	intent.setClass(Main.this, About.class);
    		    	intent.putExtra("action_type","News");
    		    	intent.putExtra("title",ParseXml.newslist.get(position-1).get("title").toString()+"\n"+ParseXml.newslist.get(position-1).get("releasedate").toString());
    		    	intent.putExtra("content",ParseXml.newslist.get(position-1).get("content").toString());
    		    	startActivity(intent);
    			}
    		}
    	});
	}
	private static View createTabView(final Context context, final String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_top, null);
        TextView tv = (TextView) view.findViewById(R.id.tabText);
        tv.setText(text);
        return view;
    }
	class DownloadAll extends AsyncTask<Void, Integer, String>{
		@Override
		protected String doInBackground(Void... params) {
			ParseXml.discussList(tid);
			return "Done";
		}

		@Override
		protected void onPostExecute(String result) {
			adapter.notifyDataSetChanged();
			listView.onRefreshComplete();
		}
	}
	class DownloadNews extends AsyncTask<Void, Integer, String>{
		@Override
		protected String doInBackground(Void... params) {
			Log.d(TAG, "DownloadNews");
			ParseXml.homeList(url,"news");
			return "Done";
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d(TAG, "notifyDataSetChanged");
			adapter1.notifyDataSetChanged();
			listView1.onRefreshComplete();
		}
	}
	private OnClickListener btnclk = new OnClickListener() {
    	public void onClick(View v){
    		switch(v.getId()) {
    			case R.id.title_left_btn:
    				Intent intent = new Intent();
    		    	intent.setClass(Main.this, Ask.class);
    		    	intent.putExtra("action_type","q");
    		    	startActivity(intent);
    				break;
    			case R.id.title_right_btn:
    				finish();
    				break;
    			default:
    				break;
    		}
    	}
    };
    
    private OnItemSelectedListener itemSelectedListener = new Spinner.OnItemSelectedListener(){
    	@Override
    	public void onItemSelected(AdapterView adapterview, View arg1, int position, long id) {
    		Log.d(TAG, "Spinner onItemSelected");
    		String tid_old = tid;
    		tid = String.valueOf(position+1);
    		if (!tid_old.equals(tid)){
    			get_discussList();
    		}
    	}
    	@Override
    	public void onNothingSelected(AdapterView view) {

    	}
    };
    
    Handler myHandler = new Handler() {  
        public void handleMessage(Message msg) {
        	super.handleMessage(msg);
        	switch(msg.what) {
        	case 222:
        		adapter1.notifyDataSetChanged();
        		Main.this.cancelDialog.dismiss();
        		break;
        	case 333:
        		adapter.notifyDataSetChanged();
        		Main.this.cancelDialog.dismiss();
        		break;
        	}
        }   
	};
    protected void get_xml(final String s) {
    	if(!this.network_ok) return;
   		this.createCancelProgressDialog();
   		new Thread(){ 
   			@Override
   			public void run(){
   				Message m = new Message();
   				if(s=="News"){
   					ParseXml.homeList(url,"news");
   					m.what = 222;
   				}else{
   					ParseXml.homeList(url,"reply");
   					m.what = 333;
   				}
   				myHandler.sendMessage(m);
   			}
   		}.start();
	}
    
    protected void get_discussList() {
    	if(!this.network_ok) return;
   		this.createCancelProgressDialog();
   		new Thread(){ 
   			@Override
   			public void run(){
   				Message m = new Message();
   				ParseXml.discussList(tid);
   				m.what = 333;

   				myHandler.sendMessage(m);
   			}
   		}.start();
	}   
    
    public class MainAdapter extends BaseAdapter {	
    	public List<DiscussListItem> data;
		public MainAdapter(List<DiscussListItem> d) {
			data=d;
		}
    	public int getCount() {
    		return data.size();
    	}
    	public Object getItem(int position) {
    		return data.get(position);
    	}
    	public long getItemId(int position) {
    		return position;
    	}
    	@Override
    	public View getView(final int position, View view, ViewGroup parent) {
    		final ViewHolder holder;
    		if (view == null) {
    			view = getLayoutInflater().inflate(R.layout.searchlist, null);
    			holder = new ViewHolder();
//    			holder.pic = (ImageView) view.findViewById(R.id.imageView1);
//    			holder.wait = (ProgressBar) view.findViewById(R.id.loading);
    			holder.searchlist_linearly =(LinearLayout)view.findViewById(R.id.searchlist_linearly);
    			holder.label_1 = (TextView) view.findViewById(R.id.label_1);
    			holder.label_2 = (TextView) view.findViewById(R.id.label_2);
    			holder.label_3 = (TextView) view.findViewById(R.id.label_3);
    			holder.arrow = (ImageView) view.findViewById(R.id.arrow);
    			view.setTag(holder);        		
    		} else {
    			holder = (ViewHolder) view.getTag();
    		} 
			Log.d(TAG, "question position:"+position);
			if ( (position % 2) == 1){  				
				holder.searchlist_linearly.setBackgroundColor(getResources().getColor(R.color.list_odd_color));
			} else {
				holder.searchlist_linearly.setBackgroundColor(getResources().getColor(R.color.list_even_color));
			}   		
    		
    		holder.label_1.setText(data.get(position).getTitle());
    		holder.label_2.setText(data.get(position).getNickname());
    		holder.label_3.setText(data.get(position).getPost_time());

    		holder.arrow.setImageBitmap(BitmapFactory.decodeResource(getBaseContext().getResources(),R.drawable.arrow));
    		
    		return view;
    	}
    	
    	class ViewHolder{
    		LinearLayout searchlist_linearly;
    		ImageView pic;
    		//ProgressBar wait;
    		TextView label_1;
    		TextView label_2;
    		TextView label_3;
//    		TextView label_4;
//    		TextView label_5;
//    		TextView label_6;
    		ImageView arrow;
    	}
    }
    public class NewsAdapter extends BaseAdapter {
    	public List<HashMap<String, Object>> data;
		public NewsAdapter(List<HashMap<String, Object>> d) {
			data = d;
		}
    	public int getCount() {
    		return data.size();
    	}
    	public Object getItem(int position) {
    		return data.get(position);
    	}
    	public long getItemId(int position) {
    		return position;
    	}
    	@Override
    	public View getView(final int position, View view, ViewGroup parent) {
    		final ViewHolder holder;
    		if (view == null) {
    			view = getLayoutInflater().inflate(R.layout.newslist, null);
    			holder = new ViewHolder();
    			holder.searchlist_linearly =(LinearLayout)view.findViewById(R.id.searchlist_linearly);

    			holder.pic = (ImageView) view.findViewById(R.id.imageView1);
    			holder.label_1 = (TextView) view.findViewById(R.id.label_1);
    			holder.label_2 = (TextView) view.findViewById(R.id.label_2);
    			holder.arrow = (ImageView) view.findViewById(R.id.arrow);
    			view.setTag(holder);        		
    		} else {
    			holder = (ViewHolder) view.getTag();
    		} 
 
			Log.d(TAG, "news position:"+position);
			if ( (position % 2) == 1){  				
				holder.searchlist_linearly.setBackgroundColor(getResources().getColor(R.color.list_odd_color));
			} else {
				holder.searchlist_linearly.setBackgroundColor(getResources().getColor(R.color.list_even_color));
			} 
			
			
			holder.label_1.setText(data.get(position).get("title").toString());
			holder.label_2.setText(data.get(position).get("releasedate").toString());

    		holder.arrow.setBackgroundResource(R.drawable.arrow);
    		holder.pic.setBackgroundResource(R.drawable.spot);
    		return view;
    	}
    	class ViewHolder{
    		LinearLayout searchlist_linearly;
    		ImageView pic;
    		TextView label_1;
    		TextView label_2;
    		ImageView arrow;
    	}
    }
}
