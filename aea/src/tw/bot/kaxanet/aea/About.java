package tw.bot.kaxanet.aea;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class About extends Activity {
	String action_type;
	String newstitle;
	String content;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        action_type = getIntent().getExtras().getString("action_type");
        newstitle = getIntent().getExtras().getString("title");
        content = getIntent().getExtras().getString("content");
        findview();
    }

	private void findview() {
		Button r_btn = (Button) findViewById(R.id.title_right_btn);
		LinearLayout versionbar = (LinearLayout) findViewById(R.id.about);
		TextView title = (TextView) findViewById(R.id.maintitle);
		TextView version = (TextView) findViewById(R.id.version);
		TextView versionno = (TextView) findViewById(R.id.versionno);
		TextView about_1 = (TextView) findViewById(R.id.about_1);
		TextView about_2 = (TextView) findViewById(R.id.about_2);
		if(action_type.equals("App")){
			title.setText(this.getString(R.string.title_app));
			about_2.setVisibility(View.GONE);
			version.setText("Version");
			try {
				versionno.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
				about_1.setText(R.string.about_app_text);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		if(action_type.equals("Us")){
			title.setText(this.getString(R.string.about_kaxa));
			version.setText("KaxaNet");
			version.setTextColor(getResources().getColor(R.color.ioscolor));
			versionno.setText("");
			about_1.setText(R.string.about_app_text1);
			about_2.setText(R.string.about_app_text2);
		}
		if(action_type.equals("News")){
			title.setText("");
			versionbar.setVisibility(View.GONE);
			about_1.setText(newstitle);
			about_2.setText(content);
		}
    	r_btn.setOnClickListener(btnclk);
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
}
