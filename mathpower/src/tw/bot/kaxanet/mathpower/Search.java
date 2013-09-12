package tw.bot.kaxanet.mathpower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.nostra13.universalimageloader.core.ImageLoadingListener;

import tw.bot.kaxanet.mathpower.MyListView.OnRefreshListener;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class Search extends RefActivity {
	BaseAdapter adapter;
	String url=null;
	List<NameValuePair> param;
	MyListView listView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        ParseXml.themeXmlList();
        findview();
    }

	@Override
    public void onResume() {
    	super.onResume();
    }
	@Override
    public void onDestroy(){
		imageLoader.stop();
    	super.onDestroy();
    }
	private void findview() {
		listView = (MyListView) findViewById(R.id.listView);
		Button l_btn = (Button) findViewById(R.id.title_left_btn);
		Button r_btn = (Button) findViewById(R.id.title_right_btn);
		l_btn.setOnClickListener(btnclk);
		r_btn.setOnClickListener(btnclk);
		//新增SimpleAdapter
		adapter = new SearchAdapter(ParseXml.searchlist);
		//ListActivity設定adapter
		listView.setAdapter( adapter );
    	listView.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				DownloadTask dTask = new DownloadTask();
				dTask.execute();
			}
		});
    	listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
    		@Override
			public void onItemClick(AdapterView<?> parent,View view, int position, long id){
    			if(position>0){
    				Intent intent = new Intent();
    				intent.putExtra("discussID",ParseXml.searchlist.get(position-1).get("discussID").toString());
    				intent.setClass(Search.this, DetailsList.class);
    				startActivity(intent);
    			}
    		}
    	});
	}
	private void search_dialog(Context context) {
		if(!this.network_ok) return;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.searchdialog,(ViewGroup) findViewById(R.id.layout_root));
	    final EditText edtInput=(EditText)layout.findViewById(R.id.editText1);
//	    final Spinner education = (Spinner)layout.findViewById(R.id.spinner1);
//	    final Spinner course = (Spinner)layout.findViewById(R.id.spinner2);
//    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
//                android.R.layout.simple_spinner_item, ParseXml.educationList);
//    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        education.setAdapter(adapter);
//        adapter = new ArrayAdapter<String>(this, 
//                android.R.layout.simple_spinner_item, ParseXml.courseList);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        course.setAdapter(adapter);
	    
	    final Spinner theme  = (Spinner)layout.findViewById(R.id.spinner1);
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
    			android.R.layout.simple_spinner_item, ParseXml.themeList);	    
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	theme.setAdapter(adapter);	    
	    
	    AlertDialog.Builder builder = new AlertDialog.Builder(context);  
        builder.setCancelable(false);  
        builder.setTitle("請輸入查詢資訊");  
        builder.setView(layout);  
        builder.setPositiveButton("確認",  
        	new DialogInterface.OnClickListener() {  
	           	@Override
				public void onClick(DialogInterface dialog, int whichButton) {
	           		String str = edtInput.getText().toString();
	           		param = new ArrayList<NameValuePair>();
	           		param.add(new BasicNameValuePair("kw",str));
	           		Theme themeSelected = ParseXml.themeXmlList.get(Long.valueOf(theme.getSelectedItemId()).intValue());
	           		param.add(new BasicNameValuePair("dg",themeSelected.getDegreeID()));
	           		param.add(new BasicNameValuePair("sub",themeSelected.getSubjectID()));
					Search.this.createCancelProgressDialog();
					new Thread(){ 
	           			@Override
	           			public void run(){
	           				ParseXml.searchList(param);
	           				Message m = new Message();
	           				m.what = 222;
	           				myHandler.sendMessage(m);
	           			}
	           		}.start(); //開始執行執行緒
	           	}  
	        }
        );  
	    builder.setNegativeButton("取消",  
	    	new DialogInterface.OnClickListener() {  
	        	@Override
				public void onClick(DialogInterface dialog, int whichButton) {
	            }  
	        });  
	    builder.show();  
	}
    private OnClickListener btnclk = new OnClickListener() {
    	@Override
		public void onClick(View v){
    		switch(v.getId()) {
    			case R.id.title_left_btn:
    				search_dialog(Search.this);
    				break;
    			case R.id.title_right_btn:
    				finish();
    				break;
    			default:
    				break;
    		}
    	}

    };
    DownloadTask dTask;
	Handler myHandler = new Handler() {  
        @Override
		public void handleMessage(Message msg) {
        	super.handleMessage(msg);
        	switch(msg.what) {
        	case 222:
       			adapter.notifyDataSetChanged();
        		Search.this.cancelDialog.dismiss();
        		break;
        	}
        }   
	}; 
	class DownloadTask extends AsyncTask<Void, Integer, String>{
		@Override
		protected String doInBackground(Void... params) {
				ParseXml.searchList(param);
			return "Done";
		}

		@Override
		protected void onPostExecute(String result) {
			adapter.notifyDataSetChanged();
			listView.onRefreshComplete();
		}
	}
	public class SearchAdapter extends BaseAdapter {
		public List<HashMap<String, Object>> data;
		public SearchAdapter(List<HashMap<String, Object>> d) {
			data=d;
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
    		holder.label_6.setText(data.get(position).get("asker").toString());
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
