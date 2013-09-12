package tw.bot.kaxanet.linway;

import java.io.File;
import java.io.FileOutputStream;

import tw.bot.kaxanet.linway.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DecodingType;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoadingListener;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImagePagerActivity extends Activity {

	private ViewPager pager;

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private String[] imageUrls;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ac_image_pager);
		Bundle bundle = getIntent().getExtras();
		imageUrls = bundle.getStringArray("imageurls");
		int pagerPosition = bundle.getInt("pos", 0);

        options = new DisplayImageOptions.Builder()
		.showImageForEmptyUrl(R.drawable.default_photo)
		.cacheOnDisc()
		.decodingType(DecodingType.MEMORY_SAVING)
		.build();

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new ImagePagerAdapter(imageUrls));
		pager.setCurrentItem(pagerPosition);
	}
    protected void getOriginalImage(final String urlpath,final Bitmap bmImg) {
    	System.gc();
		pd = ProgressDialog.show(
				this, 
				null,
				"Loading..Please wait...."
		);
		new Thread(){ 
			@Override
			public void run(){
				File filename = null;
		        try {
		            String path = Environment.getExternalStorageDirectory().toString();
		            new File(path + "/kaxa/").mkdir();
		            filename = new File(path + "/kaxa/"+urlpath.hashCode()+".jpg");
		            FileOutputStream out = new FileOutputStream(filename);
		            bmImg.compress(Bitmap.CompressFormat.JPEG, 100, out);
		            out.flush();
		            out.close();
		            MediaStore.Images.Media.insertImage(
		            		getContentResolver(),
		                    filename.getAbsolutePath(), 
		                    filename.getName(),
		                    filename.getName()
		            );
		        } catch (Exception e) {
		            e.printStackTrace();
		        }finally{
		        	bmImg.recycle();
		        	System.gc();
		        	Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(filename),"image/*");
					startActivity(intent);
		        	Message m = new Message();
					m.what = 333;
					myHandler.sendMessage(m);
		        }
			}
		}.start();
	}
    ProgressDialog pd;
	Handler myHandler = new Handler() {  
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what) {
			case 333:
				pd.dismiss();
				break;
			}
		}   
	};
	private class ImagePagerAdapter extends PagerAdapter {

		private String[] images;
		private String[] loading;
		private LayoutInflater inflater;

		ImagePagerAdapter(String[] images) {
			this.images = images;
			this.loading = new String[images.length];
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return images.length;
		}
		@Override
		public Object instantiateItem(View view, final int position) {
			System.gc();
			final FrameLayout imageLayout = (FrameLayout) inflater.inflate(R.layout.item_pager_image, null);
			final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
			imageView.setDrawingCacheEnabled(true);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

			imageView.setOnClickListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
		        	if(loading[position].equals(("fine"))){
		        		getOriginalImage(images[position],imageView.getDrawingCache());
		        	}
		        }
		    });
			imageLoader.displayImage(images[position], imageView, options, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted() {
					loading[position]="fine";
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed() {
					loading[position]="fail";
					spinner.setVisibility(View.GONE);
					imageView.setImageResource(R.drawable.default_photo);
				}

				@Override
				public void onLoadingComplete() {
					spinner.setVisibility(View.GONE);
				}
			});

			((ViewPager) view).addView(imageLayout, 0);
			return imageLayout;
		}
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}

	}
}