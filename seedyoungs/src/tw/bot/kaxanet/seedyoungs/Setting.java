package tw.bot.kaxanet.seedyoungs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Setting extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        findview();
    }

	private void findview() {
		Button r_btn = (Button) findViewById(R.id.title_right_btn);
		Button account_setting = (Button) findViewById(R.id.account_setting);
    	Button create_account = (Button) findViewById(R.id.create_account);
    	Button about_app = (Button) findViewById(R.id.about_app);
    	Button about_us = (Button) findViewById(R.id.about_us);
    	Button contact = (Button) findViewById(R.id.contact);
    	r_btn.setOnClickListener(btnclk);
    	account_setting.setOnClickListener(btnclk);
    	create_account.setOnClickListener(btnclk);
    	about_app.setOnClickListener(btnclk);
    	about_us.setOnClickListener(btnclk);
    	contact.setOnClickListener(btnclk);
	}
	private OnClickListener btnclk = new OnClickListener() {
		Intent intent;
    	public void onClick(View v){
    		switch(v.getId()) {
    			case R.id.title_right_btn:
    				finish();
    				break;
    			case R.id.account_setting:
    				intent = new Intent();
    		    	intent.setClass(Setting.this, AccountSetting.class);
    		    	startActivity(intent);
    				break;
    			case R.id.create_account:
    				intent = new Intent();
    		    	intent.setClass(Setting.this, AccountCreate.class);
    		    	startActivity(intent);
    				break;
    			case R.id.about_app:
    				intent = new Intent();
    		    	intent.setClass(Setting.this, About.class);
    		    	intent.putExtra("action_type","App");
    		    	startActivity(intent);
    				break;
    			case R.id.about_us:
    				intent = new Intent();
    		    	intent.setClass(Setting.this, About.class);
    		    	intent.putExtra("action_type","Us");
    		    	startActivity(intent);
    				break;
    			case R.id.contact:
    				Intent i = new Intent(Intent.ACTION_SEND);  
    				i.setType("message/rfc822") ;
    				i.putExtra(Intent.EXTRA_EMAIL, new String[]{"kaxanote@gmail.com"});  
    				i.putExtra(Intent.EXTRA_SUBJECT,"KaxaNote Android App");  
    				i.putExtra(Intent.EXTRA_TEXT,"Dear KaxaNote\n\n\n\n");  
    				startActivity(Intent.createChooser(i, "Select email application."));
    				break;
    			default:
    				break;
    		}
    	}

    };
}
