package tw.bot.kaxanet;

import android.app.TabActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

import android.widget.TabHost.TabSpec;

public class KaxaActivity extends TabActivity {
	TabHost tabHost;
	int tabselected;
 @Override
 public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.tab);
     
     tabHost = getTabHost();  
     TabSpec spec;
     Intent intent;
     
     intent = new Intent(getBaseContext(),Main.class);
     spec = tabHost.newTabSpec("main")
     .setIndicator("main")
     .setContent(intent);
     tabHost.addTab(spec);
     
     intent = new Intent(getBaseContext(),MyPage.class);
     spec = tabHost.newTabSpec("mypage")
     .setIndicator("mypage")
     .setContent(intent);
     tabHost.addTab(spec);
     
     intent = new Intent(getBaseContext(),Search.class);
     spec = tabHost.newTabSpec("search")
     .setIndicator("search")
     .setContent(intent);
     tabHost.addTab(spec);
     
     intent = new Intent(getBaseContext(),Setting.class);
     spec = tabHost.newTabSpec("setting")
     .setIndicator("setting")
     .setContent(intent);
     tabHost.addTab(spec);
     
     RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_main_btns);
     radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
    	 public void onCheckedChanged(RadioGroup group, int checkedId) {  
    		 switch (checkedId) {  
    		 	case R.id.main:  
    		 		tabHost.setCurrentTabByTag("main");  
    		 		break;  
    		 	case R.id.mypage:  
    		 		tabHost.setCurrentTabByTag("mypage");  
    		 		break;  
    		 	case R.id.search:  
    		 		tabHost.setCurrentTabByTag("search");  
    		 		break; 
    		 	case R.id.setting:  
    		 		tabHost.setCurrentTabByTag("setting");  
    		 		break; 
     }  
 }  
});  
     tabselected = 0;
     tabHost.setCurrentTab(tabselected);
 }
}