package tw.bot.kaxanet.linway;

import java.util.List;

import tw.bot.kaxanet.linway.MyListView.OnRefreshListener;
import tw.bot.kaxanet.linway.model.InsideSubTitleListItem;
import android.content.Intent;
import android.os.Bundle;
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

public class InsideSubList extends RefActivity {
	BaseAdapter adapter;
	String url=null;
	MyListView listView;
	private int inside_item_idx;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inside_sub_list);
        inside_item_idx = getIntent().getExtras().getInt("inside_item_idx");
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
		
		l_btn.setText(ParseXml.insidelist.get(inside_item_idx).getTitle());
		l_btn.setOnClickListener(btnclk);
		//r_btn.setOnClickListener(btnclk);
		//新增SimpleAdapter
		adapter = new InsideAdapter(ParseXml.insidelist.get(inside_item_idx).getSubTitleList());
		//ListActivity設定adapter
		listView.setAdapter( adapter );
		
    	listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
    		public void onItemClick(AdapterView<?> parent,View view, int position, long id){
    			if(position>0){
    				if (getAccount().isAccountCanPost()){
	    				Intent intent = new Intent();
	    				//如果是國家etc
	    				if (ParseXml.insidelist.get(inside_item_idx).getId().equals("3")) {
	    					intent.putExtra("isCountry",true);
	    				}else {
	    					intent.putExtra("isCountry",false);
	    				}
	    				intent.putExtra("discussID",ParseXml.insidelist.get(inside_item_idx).getSubTitleList().get(position-1).getId());
	    				intent.setClass(InsideSubList.this, InsideDetail.class);
	    				startActivity(intent);
    				} else {
    					notsetting(InsideSubList.this);
    				}
    			}
    		}
    	});

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
	
	
	public class InsideAdapter extends BaseAdapter {
		public List<InsideSubTitleListItem> data;
		public InsideAdapter(List<InsideSubTitleListItem> d) {
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
    		holder.label_3.setText(data.get(position).getReleasedate());

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