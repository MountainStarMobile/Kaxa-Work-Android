package tw.bot.kaxanet.jn;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
 
public class Splash extends Activity {
        private final int SPLASH_DISPLAY_LENGHT = 1000;
        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle icicle) {
                super.onCreate(icicle);
                setContentView(R.layout.splash);
                // This configuration tuning is custom. You can tune every option, you may tune some of them, 
        		// or you can create default configuration by
        		//  ImageLoaderConfiguration.createDefault(this);
        		// method.
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(6)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(1500000) // 1.5 Mb
                .discCacheSize(50000000) // 50 Mb
                .httpReadTimeout(15000) // 15 s
                .denyCacheImageMultipleSizesInMemory()
                .build();
                
                // Initialize ImageLoader with configuration.
                ImageLoader.getInstance().init(config);
                ImageLoader.getInstance().enableLogging();
                new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run() {
                                Intent mainIntent = new Intent(Splash.this,KaxaActivity.class);
                                Splash.this.startActivity(mainIntent);
                                Splash.this.finish();
                        }
                }, SPLASH_DISPLAY_LENGHT);
        }
}
