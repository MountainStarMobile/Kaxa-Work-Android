package tw.bot.kaxanet;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gcm.GCMRegistrar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AccountSetting extends RefActivity{
	private EditText email;
	private EditText passwd;
	static final String SENDER_ID = "821425774566";
	private String regId = "";
	boolean login_check;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accountsetting);
        findview();
    }
    private void findview() {
		// TODO Auto-generated method stub
		Button setting = (Button) findViewById(R.id.setting);
		Button reset = (Button) findViewById(R.id.reset);
		Button r_btn = (Button) findViewById(R.id.title_right_btn);
    	r_btn.setOnClickListener(btnclk);
    	email = (EditText) findViewById(R.id.email);
		passwd = (EditText) findViewById(R.id.passwd);
		setting.setOnClickListener(btnclk);
		reset.setOnClickListener(btnclk);
		SharedPreferences settings = getSharedPreferences("setting", 0);
		email.setText(settings.getString("email",""));
	    passwd.setText(settings.getString("passwd",""));
	}
	private OnClickListener btnclk = new OnClickListener() {
    	public void onClick(View v){
    		switch(v.getId()) {
    			case R.id.setting:
    				account_validate();
    				break;
    			case R.id.reset:
    				reset();
    				break;
    			case R.id.title_right_btn:
    				finish();
    				break;
    			default:
    				break;
    		}
    	}

    };

    protected void account_validate() {
		if(!this.network_ok) return;
		
		//GCM
		GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
          GCMRegistrar.register(this, SENDER_ID);
        } else {
          Log.v("GCM", "Already registered : " + regId);
        }
		final List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("acc",email.getText().toString()));
		params.add(new BasicNameValuePair("pass",passwd.getText().toString()));
		params.add(new BasicNameValuePair("pty","android"));
		params.add(new BasicNameValuePair("pid",regId));
		this.createCancelProgressDialog();
		new Thread(){ 
   			@Override
   			public void run(){
   				login_check = ParseXml.LoginCheck(params);
   				Message m = new Message();
   				m.what = 222;
   				myHandler.sendMessage(m);
   			}
   		}.start(); //開始執行執行緒
	}
    Handler myHandler = new Handler() {  
        public void handleMessage(Message msg) {
        	super.handleMessage(msg);
        	switch(msg.what) {
        	case 222:
        		AccountSetting.this.cancelDialog.dismiss();
        		if(login_check) {
        			SharedPreferences settings = getSharedPreferences ("setting", 0);
        			SharedPreferences.Editor PE = settings.edit();
        			PE.putString("email", email.getText().toString());
        			PE.putString("passwd", passwd.getText().toString());
        			PE.putString("userid", ParseXml.logininfo.get("userid").toString());
        			PE.putString("pushid", ParseXml.logininfo.get("pushid").toString());
        			PE.commit();
        			Toast.makeText(getBaseContext(), "帳號驗證成功!!", Toast.LENGTH_LONG).show();
        			finish();
        		}else{
        			AlertDialog.Builder builder = new AlertDialog.Builder(AccountSetting.this);
        			builder.setMessage("帳號驗證錯誤!!");
        			builder.setCancelable(false);
        			builder.setPositiveButton("重新輸入", new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int id) {
        				}
        			});
        			builder.setNegativeButton("離開", new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int id) {
        					reset();
        					finish();
        				}

        			});   
        			AlertDialog alert = builder.create();
        			alert.show();
        		}
        		break;
        	}
        }   
	}; 
	private void reset() {
		// TODO Auto-generated method stub
		SharedPreferences settings = getSharedPreferences ("setting", 0);
		SharedPreferences.Editor PE = settings.edit();
		PE.putString("email", "");
		PE.putString("passwd", "");
		PE.putString("userid", "");
		PE.putString("pushid", "");
		PE.commit();
		ParseXml.logininfo.clear();
		email.setText("");
		passwd.setText("");
	}
}
