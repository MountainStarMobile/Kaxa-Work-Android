package tw.bot.kaxanet.linway;

import java.util.List;

import tw.bot.kaxanet.linway.MyListView.OnRefreshListener;
import tw.bot.kaxanet.linway.model.InsidelistItem;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class InsideList extends RefActivity {
	BaseAdapter adapter;
	String url=null;
	MyListView listView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insidelist);
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

		Button r_btn = (Button) findViewById(R.id.title_right_btn);

		r_btn.setOnClickListener(btnclk);
		//新增SimpleAdapter
		adapter = new InsideAdapter(ParseXml.insidelist);
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
    			if (position > 0){
					Intent intent = new Intent();
					intent.putExtra("inside_item_idx",position-1);
					intent.setClass(InsideList.this, InsideSubList.class);
					startActivity(intent);
    			}
    		}
    	});
    	if (ParseXml.insidelist.size() == 0) {
    		refresh_data(InsideList.this);
    	}
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
    			ParseXml.getInsidelist();
    			Message m = new Message();
    			m.what = 222;
    			myHandler.sendMessage(m);
    		}
    	}.start();
    }	
	
    private OnClickListener btnclk = new OnClickListener() {
    	public void onClick(View v){
    		switch(v.getId()) {
    			case R.id.title_right_btn:
    				finish();
    				break;
    			default:
    				break;
    		}
    	}

    };	
	
    DownloadTask dTask;
    ProgressDialog pd;
	Handler myHandler = new Handler() {  
        public void handleMessage(Message msg) {
        	super.handleMessage(msg);
        	switch(msg.what) {
        	case 222:
        		pd.dismiss();
       			adapter.notifyDataSetChanged();
        		break;
        	}
        }   
	}; 
	class DownloadTask extends AsyncTask<Void, Integer, String>{
		@Override
		protected String doInBackground(Void... params) {
				ParseXml.getInsidelist();
			return "Done";
		}

		@Override
		protected void onPostExecute(String result) {
			adapter.notifyDataSetChanged();
			listView.onRefreshComplete();
		}
	}
	
	public class InsideAdapter extends BaseAdapter {
		public List<InsidelistItem> data;
		public InsideAdapter(List<InsidelistItem> d) {
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
    			holder.pic = (ImageView) view.findViewById(R.id.imageView1);
    			holder.wait = (ProgressBar) view.findViewById(R.id.loading);
    			holder.label_1 = (TextView) view.findViewById(R.id.label_1);
    			holder.label_2 = (TextView) view.findViewById(R.id.label_2);
    			holder.label_3 = (TextView) view.findViewById(R.id.label_3);
    			holder.arrow = (ImageView) view.findViewById(R.id.arrow);
    			view.setTag(holder);        		
    		} else {
    			holder = (ViewHolder) view.getTag();
    		} 
    		
			if ( (position % 2) == 1){  				
				holder.searchlist_linearly.setBackgroundColor(getResources().getColor(R.color.list_odd_color));
			} else {
				holder.searchlist_linearly.setBackgroundColor(getResources().getColor(R.color.list_even_color));
			} 
			
    		holder.label_1.setText(data.get(position).getTitle());//顯示文字說明
    		holder.label_2.setText("");
    		holder.label_3.setText("");

    		return view;
    	}
    	
    	class ViewHolder{
    		LinearLayout searchlist_linearly;
    		ImageView pic;
    		ProgressBar wait;
    		TextView label_1;
    		TextView label_2;
    		TextView label_3;
    		ImageView arrow;
    	}
    }	
	
}
