package tw.bot.kaxanet.aea;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.gcm.GCMRegistrar;

public class AccountCreate extends RefActivity{
	static final int DATE_DIALOG_ID = 0;
	static final String SENDER_ID = "821425774566";
	String regId = "";
	
	private EditText email;
	private EditText passwd;
	private EditText repasswd;
	private EditText name;
	private EditText nickname;
	private EditText birthdate;
	private EditText phoneno;
	boolean login_check;
	
	private String succes_regiest;
	private String setting_now;
	private String exit;
	private String error_regiest;
	private String error_enter_again;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accountcreate); 
        
        succes_regiest = this.getString(R.string.succes_regiest);
        setting_now = this.getString(R.string.setting_now);
        exit = this.getString(R.string.exit);
        error_regiest = this.getString(R.string.error_regiest);
        error_enter_again  = this.getString(R.string.error_enter_again);
        
    	findview();
    }
    private void findview() {
		Button register = (Button) findViewById(R.id.register);
		Button r_btn = (Button) findViewById(R.id.title_right_btn);
    	r_btn.setOnClickListener(btnclk);
    	email = (EditText) findViewById(R.id.email);
		passwd = (EditText) findViewById(R.id.passwd);
		repasswd = (EditText) findViewById(R.id.repasswd);
		name = (EditText) findViewById(R.id.name);
		nickname = (EditText) findViewById(R.id.nickname);
		birthdate = (EditText) findViewById(R.id.birthdate);
		phoneno = (EditText) findViewById(R.id.phoneno);
		birthdate.setOnTouchListener(bd);
		register.setOnClickListener(btnclk);
	}
    private OnTouchListener bd = new OnTouchListener(){ 
        public boolean onTouch(View v, MotionEvent event) { 
            if(v == birthdate)
                showDialog(DATE_DIALOG_ID);
            return false;              
        }
    };
    
	private OnClickListener btnclk = new OnClickListener() {
    	public void onClick(View v){
    		switch(v.getId()) {
    			case R.id.register:
    				account_register();
    				break;
    			case R.id.title_right_btn:
    				finish();
    				break;
    			default:
    				break;
    		}
    	}

    };
    
	protected void account_register() {
		if(!this.network_ok) return;
		if(!isEmailValid(email.getText().toString())){
			this.errorlog(this.getString(R.string.error),this.getString(R.string.error_email),this.getString(R.string.error_continue));
			return;
		}
		if(!passwd.getText().toString().equals(repasswd.getText().toString())){
			this.errorlog(this.getString(R.string.error),this.getString(R.string.error_pwd),this.getString(R.string.error_continue));
			return;
		}
		
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
		params.add(new BasicNameValuePair("name",name.getText().toString()));
		params.add(new BasicNameValuePair("nickName",nickname.getText().toString()));
		params.add(new BasicNameValuePair("bdy",birthdate.getText().toString()));
		params.add(new BasicNameValuePair("phoNum",phoneno.getText().toString()));
		params.add(new BasicNameValuePair("pty","android"));
		params.add(new BasicNameValuePair("pid",regId));

		this.createCancelProgressDialog();
		new Thread(){ 
   			@Override
   			public void run(){
   				login_check = ParseXml.register(params);
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
        		AccountCreate.this.cancelDialog.dismiss();
        		if(login_check) {
        			AlertDialog.Builder builder = new AlertDialog.Builder(AccountCreate.this);
        			builder.setMessage(succes_regiest);
        			builder.setCancelable(false);
        			builder.setPositiveButton(setting_now, new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					SharedPreferences settings = getSharedPreferences ("setting", 0);
    					SharedPreferences.Editor PE = settings.edit();
    					PE.putString("email", email.getText().toString());
    					PE.putString("passwd", passwd.getText().toString());
    					PE.commit();
    					finish();
    					}
        			});
        			builder.setNegativeButton(exit, new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
    					finish();
    				}
        			});   
        			AlertDialog alert = builder.create();
        			alert.show();
        		}else{
        			AlertDialog.Builder builder = new AlertDialog.Builder(AccountCreate.this);
        			builder.setMessage(error_regiest);
        			builder.setCancelable(false);
        			builder.setPositiveButton(error_enter_again, new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int id) {
        				}
        			});
        			builder.setNegativeButton(exit, new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int id) {
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
	protected Dialog onCreateDialog(int id) {
        Calendar c = Calendar.getInstance();
        int cyear = c.get(Calendar.YEAR);
        int cmonth = c.get(Calendar.MONTH);
        int cday = c.get(Calendar.DAY_OF_MONTH);
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,  mDateSetListener,  cyear, cmonth, cday);
        }
        return null;
    }
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        // onDateSet method
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        	//目前時間
        	Date date = new Date();
        	date.setYear(year-1900);
        	date.setMonth(monthOfYear);
        	date.setDate(dayOfMonth);
        	//設定日期格式
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	String date_selected = sdf.format(date);
            birthdate.setText(date_selected);
        }
    };
    private static boolean isEmailValid(String email) {
	    boolean isValid = false;

	    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    CharSequence inputStr = email;

	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches()) {
	        isValid = true;
	    }
	    return isValid;
	}
}
