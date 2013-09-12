package tw.bot.kaxanet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.nostra13.universalimageloader.core.ImageLoadingListener;

import tw.bot.kaxanet.MyListView.OnRefreshListener;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DetailsList extends RefActivity{
	private BaseAdapter adapter;
	private String discussID;
	MyListView listView;
	private List<NameValuePair> param;
	
	private String detaillist_title;
	private String detaillist_date;
	private String detaillist_asker;
	private String detaillist_reply_date;
	private String detaillist_reply_from;
	private String detaillist_content;
	
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
    	detaillist_content = this.getString(R.string.detaillist_content);
    	
    	discussID=getIntent().getExtras().getString("discussID");
    	param = new ArrayList<NameValuePair>();
   		param.add(new BasicNameValuePair("qid",discussID));
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
    			ParseXml.questionandanswer(param);
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
		tv1.setText(ParseXml.question_details.get("degree").toString()+"-"+ParseXml.question_details.get("subject").toString());
		tv2.setText(detaillist_title+ParseXml.question_details.get("title").toString());

		l_btn.setOnClickListener(btnclk);
		r_btn.setOnClickListener(btnclk);
		listView = (MyListView) findViewById(R.id.listView);
		//新增SimpleAdapter
		adapter = new DetailsAdapter(ParseXml.answertlist);
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
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
    		public void onItemClick(AdapterView<?> parent,View view, int position, long id){
    			if(position>0){
    				System.gc();
    				imageLoader.clearMemoryCache();
    				imageLoader.clearDiscCache();
    				Intent intent = new Intent(DetailsList.this, ImagePagerActivity.class);
    				intent.putExtra("imageurls", (String[]) ParseXml.answertlist.get(position-1).get("images"));
    				intent.putExtra("pos", 0);
    				startActivity(intent);
    			}
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
		public List<HashMap<String, Object>> data;
		public DetailsAdapter(List<HashMap<String, Object>> d) {			
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
				holder.pic = (ImageView) view.findViewById(R.id.dt_image);
				holder.wait = (ProgressBar) view.findViewById(R.id.loading);
				holder.dt_asker = (TextView) view.findViewById(R.id.dt_asker);
				holder.dt_date = (TextView) view.findViewById(R.id.dt_date);
				holder.dt_content = (TextView) view.findViewById(R.id.dt_content);
				view.setTag(holder);        		
			} else {
				holder = (ViewHolder) view.getTag();
			} 
			if(position==0){
				holder.dt_date.setText(detaillist_date+data.get(position).get("date").toString());
				holder.dt_asker.setText(detaillist_asker+data.get(position).get("asker").toString());
			}else{
				holder.dt_date.setText(detaillist_reply_date+data.get(position).get("date").toString());
				holder.dt_asker.setText(detaillist_reply_from+data.get(position).get("asker").toString());
			}
			holder.dt_content.setText(detaillist_content+"\n\t\t"+data.get(position).get("content").toString());
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
			TextView dt_asker;
			TextView dt_date;
			TextView dt_content;
		}
	}
}
