package tw.bot.kaxanet.linway;

import tw.bot.kaxanet.linway.R;
import tw.bot.kaxanet.linway.model.Account;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

public class RefActivity extends Activity{
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	public boolean network_ok = false;
	public DisplayImageOptions options;
	public ImageLoaderConfiguration config;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        network_ok = CheckNetwork();
        //check network
        if(!CheckNetwork()){
        	networkisnotok();
        }
	}
	public void networkisnotok(){
		new AlertDialog.Builder(this)
    	.setMessage(this.getString(R.string.internet_not_ready))
    	.setPositiveButton(this.getString(R.string.go_setting),
    			new DialogInterface.OnClickListener(){
    		public void onClick(DialogInterface dialoginterface, int i){
    			Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
    			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
    			startActivity(intent);
    		}
    	})
    	.setNegativeButton(this.getString(R.string.exit),
    			new DialogInterface.OnClickListener(){
    		public void onClick(DialogInterface dialoginterface, int i){
    			finish();
    		}
    	})
    	.show();
	}
	
	@Override
    public void onResume() {
    	super.onResume();
    	config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        .threadPoolSize(6)
        .threadPriority(Thread.NORM_PRIORITY - 2)
        .memoryCacheSize(1500000) // 1.5 Mb
        .discCacheSize(50000000) // 50 Mb
        .httpReadTimeout(15000) // 15 s
        .denyCacheImageMultipleSizesInMemory()
        .build();
    	ImageLoader.getInstance().init(config);
        options = new DisplayImageOptions.Builder()
		.showImageForEmptyUrl(R.drawable.default_photo)
		.showStubImage(R.drawable.default_photo)
		.cacheInMemory()
		.cacheOnDisc()
		.build();
    }
	private boolean CheckNetwork(){
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); 
    	NetworkInfo info=connManager.getActiveNetworkInfo();
    	if (info == null || !info.isConnected()){
    		return false;
    	}else{
    		if (!info.isAvailable()){
    			return false;
    		}else{
    			return true;
    		}
    	}
    }
	ProgressDialog cancelDialog;
	Builder builder;
	public void errorlog(String title, String message, String buttonText)	{
		builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(buttonText, new DialogInterface.OnClickListener() 
	    {
	        public void onClick(DialogInterface dialog, int which) 
	        {
	            return;
	        }
	    });
		builder.show();
	}
	public void createCancelProgressDialog(String title, String message, String buttonText)	{
		cancelDialog = new ProgressDialog(this);
	    cancelDialog.setTitle(title);
	    cancelDialog.setMessage(message);
	    cancelDialog.setButton(buttonText, new DialogInterface.OnClickListener() 
	    {
	        public void onClick(DialogInterface dialog, int which) 
	        {
	            return;
	        }
	    });
	    cancelDialog.show();
	}
	public void createCancelProgressDialog(){
		String message = "Loading...Please wait...";
		cancelDialog = new ProgressDialog(this);
	    cancelDialog.setMessage(message);
	    cancelDialog.setButton("Cancel", new DialogInterface.OnClickListener() 
	    {
	        public void onClick(DialogInterface dialog, int which) 
	        {
	            return;
	        }
	    });
	    cancelDialog.show();
	}
	
	public Account getAccount(){
		Account account = new Account();
    	SharedPreferences settings = getSharedPreferences("setting", 0);
    	account.setEmail(settings.getString("email",""));
    	account.setPasswd(settings.getString("passwd",""));
    	account.setUserid(settings.getString("userid", ""));
    	account.setMailValidate(settings.getString("mailValidate",""));
    	account.setAccountLock(settings.getString("accountLock",""));
    	account.setPermission(settings.getString("permission", ""));
    	return account;
	}
	
	public boolean isLogin(){
		Account account = getAccount();
		if (account.getEmail().equals("")||account.getPasswd().equals("")){
			return false;
		}else {
			return true;
		}
	}
	
	public void notsetting(Context context) {
		final Context packageContext = context;
		if (!isLogin()) {
			new AlertDialog.Builder(this)
					.setMessage(this.getString(R.string.not_setting))
					.setPositiveButton(this.getString(R.string.go_setting),
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
									Intent intent = new Intent();
									intent.setClass(packageContext,
											AccountSetting.class);
									startActivity(intent);
								}
							})
					.setNegativeButton(this.getString(R.string.exit),
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
								}
							}).show();
		} else {
			new AlertDialog.Builder(this)
			.setMessage(this.getString(R.string.not_validate))
			.setPositiveButton(this.getString(R.string.go_setting),
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialoginterface, int i) {
							Intent intent = new Intent();
							intent.setClass(packageContext,
									AccountSetting.class);
							startActivity(intent);
						}
					})
			.setNegativeButton(this.getString(R.string.exit),
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialoginterface, int i) {
						}
					}).show();			
		}
	}
}
