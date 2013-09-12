package tw.bot.kaxanet.linway;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import tw.bot.kaxanet.linway.MyListView.OnRefreshListener;
import tw.bot.kaxanet.linway.model.DiscussContent;
import tw.bot.kaxanet.linway.model.DiscussReply;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class DetailsList extends RefActivity{
	private BaseAdapter adapter;
	private String discussID;
	private String tid;
	MyListView listView;
	
	private String detaillist_title;
	private String detaillist_date;
	private String detaillist_asker;
	private String detaillist_reply_date;
	private String detaillist_reply_from;
	private DiscussContent discussContent;
	
	private static final String _ENCODING = "UTF-8";
	private static final String _MIMETYPE = "text/html"; 
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.detailslist);
    	
    	detaillist_title = this.getString(R.string.detaillist_title);
    	detaillist_date = this.getString(R.string.detaillist_date);
    	detaillist_asker = this.getString(R.string.detaillist_asker);
    	detaillist_reply_date = this.getString(R.string.detaillist_reply_date);
    	detaillist_reply_from = this.getString(R.string.detaillist_reply_from);
    	
    	discussID=getIntent().getExtras().getString("discussID");
    	tid = getIntent().getExtras().getString("tid");
   		refresh_data(DetailsList.this);
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
    			ParseXml.discussReply(discussID);
    			discussContent = ParseXml.discussContent;
    			Message m = new Message();
    			m.what = 222;
    			myHandler.sendMessage(m);
    		}
    	}.start();
    }
	private void findview() {
		Button l_btn = (Button) findViewById(R.id.title_left_btn);
		Button r_btn = (Button) findViewById(R.id.title_right_btn);
		TextView tv1 = (TextView) findViewById(R.id.edu_course);
		TextView tv2 = (TextView) findViewById(R.id.dt_title);
		
		tv1.setText(ParseXml.themeMap.get(tid));
		tv2.setText(detaillist_title+discussContent.getTitle());

		l_btn.setOnClickListener(btnclk);
		r_btn.setOnClickListener(btnclk);
		listView = (MyListView) findViewById(R.id.listView);
		//新增SimpleAdapter
		adapter = new DetailsAdapter(ParseXml.discussReplylist);
		//ListActivity設定adapter
		listView.setAdapter( adapter );
		listView.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				refresh_data(DetailsList.this);
				listView.setVisibility(View.GONE);
				listView.onRefreshComplete();
				listView.setVisibility(View.VISIBLE);
			}
		});
	}
    private OnClickListener btnclk = new OnClickListener() {
    	public void onClick(View v){
    		switch(v.getId()) {
    			case R.id.title_left_btn:
    				Intent intent = new Intent();
    		    	intent.setClass(DetailsList.this, Ask.class);
    		    	intent.putExtra("seq",discussID);
    		    	intent.putExtra("action_type","a");
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
    File temp;
    Bitmap bmp;
    protected void getOriginalImage(final int position) {
    	System.gc();
    	temp = null;
    	bmp=null;
		pd = ProgressDialog.show(
				this, 
				null,
				"Loading..Please wait...."
		);
		new Thread(){ 
			@Override
			public void run(){
				try {
					System.gc();
					temp = new File(Environment.getExternalStorageDirectory() + "/kaxanet.jpg");
					bmp = ParseXml.getImage(ParseXml.answertlist.get(position).get("imageurl").toString(),800);
					BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(temp));
					bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
					out.flush();
					out.close();
				} catch (Exception e) {
						e.printStackTrace();
				}
				if(bmp!=null){
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(temp),"image/*");
					startActivity(intent);
				}
				Message m = new Message();
				m.what = 333;
				myHandler.sendMessage(m);
			}
		}.start();
	}
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
	public class DetailsAdapter extends BaseAdapter {	
		public List<DiscussReply> data;
		public DetailsAdapter(List<DiscussReply> d) {			
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
				view = getLayoutInflater().inflate(R.layout.detailslistlayout, null);
				holder = new ViewHolder();
				holder.dt_asker = (TextView) view.findViewById(R.id.dt_asker);
				holder.dt_date = (TextView) view.findViewById(R.id.dt_date);
				holder.webview1 = (WebView) view.findViewById(R.id.webView1);
				holder.webview1.getSettings().setPluginsEnabled(true);
				holder.webview1.getSettings().setJavaScriptEnabled(true);


				view.setTag(holder);        		
			} else {
				holder = (ViewHolder) view.getTag();
			} 
			if (position == 0 ){
				holder.dt_date.setText(detaillist_date+data.get(position).getPost_time());
				holder.dt_asker.setText(detaillist_asker+data.get(position).getNickname());
			}else {
				holder.dt_date.setText(detaillist_reply_date+data.get(position).getPost_time());
				holder.dt_asker.setText(detaillist_reply_from+data.get(position).getNickname());
			}
			holder.webview1.loadDataWithBaseURL(WebUtil.BASEURL,data.get(position).getContent(), _MIMETYPE, _ENCODING, null);
			return view;
		}
		class ViewHolder{
			TextView dt_asker;
			TextView dt_date;
			WebView webview1;
		}
	}
}
