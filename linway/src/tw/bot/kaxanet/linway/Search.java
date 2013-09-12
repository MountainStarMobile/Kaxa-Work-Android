package tw.bot.kaxanet.linway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.nostra13.universalimageloader.core.ImageLoadingListener;

import tw.bot.kaxanet.linway.R;
import tw.bot.kaxanet.linway.MyListView.OnRefreshListener;
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
import android.widget.LinearLayout;
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
			public void onRefresh() {
				DownloadTask dTask = new DownloadTask();
				dTask.execute();
			}
		});
    	listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
    		public void onItemClick(AdapterView<?> parent,View view, int position, long id){
    			if(position>0){
    				if (getAccount().isAccountCanPost()){
	    				Intent intent = new Intent();
	    				if (ParseXml.searchlist.get(position-1).get("searchType").equals("d")){
		    				intent.putExtra("discussID",ParseXml.searchlist.get(position-1).get("discussID").toString());
		    				intent.setClass(Search.this, DetailsList.class);
	    				}
	    				if (ParseXml.searchlist.get(position-1).get("searchType").equals("i")){
	    					intent.putExtra("isCountry",false);
		    				intent.putExtra("discussID",ParseXml.searchlist.get(position-1).get("discussID").toString());
		    				intent.setClass(Search.this, InsideDetail.class);
	    				}    
	    				
	    				if (ParseXml.searchlist.get(position-1).get("searchType").equals("c")){
	    					intent.putExtra("isCountry",true);
		    				intent.putExtra("discussID",ParseXml.searchlist.get(position-1).get("discussID").toString());
		    				intent.setClass(Search.this, InsideDetail.class);
	    				}      				
	    				
	    				startActivity(intent);
    				} else {
    					notsetting(Search.this);
    				}
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
        builder.setTitle(this.getString(R.string.enter_query));  
        builder.setView(layout);  
        builder.setPositiveButton(this.getString(R.string.confirm),  
        	new DialogInterface.OnClickListener() {  
	           	public void onClick(DialogInterface dialog, int whichButton) {
	           		String str = edtInput.getText().toString();
	           		param = new ArrayList<NameValuePair>();
	           		param.add(new BasicNameValuePair("kw",str));
	           		Theme themeSelected = ParseXml.themeXmlList.get(Long.valueOf(theme.getSelectedItemId()).intValue());
//	           		param.add(new BasicNameValuePair("dg",themeSelected.getDegreeID()));
//	           		param.add(new BasicNameValuePair("sub",themeSelected.getSubjectID()));
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
	    builder.setNegativeButton(this.getString(R.string.cancel),  
	    	new DialogInterface.OnClickListener() {  
	        	public void onClick(DialogInterface dialog, int whichButton) {
	            }  
	        });  
	    builder.show();  
	}
    private OnClickListener btnclk = new OnClickListener() {
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
    			holder.searchlist_linearly =(LinearLayout)view.findViewById(R.id.searchlist_linearly);
    			if ( (position % 2) == 1){  				
    				holder.searchlist_linearly.setBackgroundColor(getResources().getColor(R.color.list_odd_color));
    			}     			
    			holder.pic = (ImageView) view.findViewById(R.id.imageView1);
    			holder.label_1 = (TextView) view.findViewById(R.id.label_1);
    			holder.label_2 = (TextView) view.findViewById(R.id.label_2);
    			holder.label_3 = (TextView) view.findViewById(R.id.label_3);
    			holder.arrow = (ImageView) view.findViewById(R.id.arrow);
    			view.setTag(holder);        		
    		} else {
    			holder = (ViewHolder) view.getTag();
    		} 
    		holder.label_1.setText(data.get(position).get("title").toString());//顯示文字說明
    		holder.label_2.setText("");
    		holder.label_3.setText(data.get(position).get("releasedate").toString());

    		return view;
    	}
    	
    	class ViewHolder{
    		LinearLayout searchlist_linearly;
    		ImageView pic;
    		TextView label_1;
    		TextView label_2;
    		TextView label_3;
    		ImageView arrow;
    	}
    }
}
