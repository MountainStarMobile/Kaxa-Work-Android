package tw.bot.kaxanet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Ask extends RefActivity {
    /** Called when the activity is first created. */
	String email;
	String passwd;
	String userid;
	int login_check;
	EditText title;
	EditText msg;
	//Spinner education;
	//Spinner course;
	Spinner theme;
	ImageView imageView,camera;
	boolean photo_setup=false;
	String filePath;
	String action_type;
	String seq;
	TextView tv;
	Button ask_btn;
	public static final int NONE = 0;  
    public static final int PHOTOCAPTURE = 1;
    public static final int PHOTOPICK = 2;
	private static final int MAX_IMAGE_DIMENSION = 800;  
    File filecapture=null;
    Bitmap photo;
    
    private String succes_upload;
    private String error_upload;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask);
        action_type = getIntent().getExtras().getString("action_type");
        seq = getIntent().getExtras().getString("seq");
        ParseXml.themeXmlList();
        
        succes_upload = this.getString(R.string.succes_upload);
        error_upload = this.getString(R.string.error_upload);
        
        findview();
    }
    
    Handler myHandler = new Handler() {  
        public void handleMessage(Message msg) {
        	super.handleMessage(msg);
        	switch(msg.what) {
        	case 333:
        		Ask.this.cancelDialog.dismiss();
        		if(upload_fine)
        			Toast.makeText(getBaseContext(), succes_upload, Toast.LENGTH_LONG).show();
        		else
        			Toast.makeText(getBaseContext(), error_upload, Toast.LENGTH_LONG).show();
        		break;
        	}
        }   
	};
    private void notsetting() {
		// TODO Auto-generated method stub
    	new AlertDialog.Builder(this)
        .setMessage(this.getString(R.string.not_setting))
        .setPositiveButton(this.getString(R.string.go_setting),
            new DialogInterface.OnClickListener(){
                public void onClick(
                    DialogInterface dialoginterface, int i){
                	Intent intent = new Intent();
    		    	intent.setClass(Ask.this, AccountSetting.class);
    		    	startActivity(intent);
                    }
                })
        .setNegativeButton(this.getString(R.string.exit),
            new DialogInterface.OnClickListener(){
                public void onClick(
                    DialogInterface dialoginterface, int i){
                    }
                })
        .show();
	}
    @Override
    public void onPause() {
    	super.onPause();
    }
    @Override
    public void onDestroy(){
    	super.onDestroy();
    }
    @Override
    public void onResume(){
    	super.onResume();
    	SharedPreferences settings = getSharedPreferences("setting", 0);
    	email=settings.getString("email","");
    	passwd = settings.getString("passwd","");
    	userid = settings.getString("userid", "");
    	if(email==""||passwd=="")	notsetting();
    }
	private void findview() {
		tv = (TextView) findViewById(R.id.asktitle);
    	Button r_btn = (Button) findViewById(R.id.title_right_btn);
    	ask_btn = (Button) findViewById(R.id.askBtn);
    	imageView = (ImageView) findViewById(R.id.imageID);
    	camera = (ImageView) findViewById(R.id.camera);
    	imageView.setDrawingCacheEnabled(true);
    	r_btn.setOnClickListener(btnclk);
    	ask_btn.setOnClickListener(btnclk);
    	imageView.setOnClickListener(btnclk);
    	camera.setOnClickListener(btnclk);
//    	education = (Spinner)findViewById(R.id.sp_education);
//	    course = (Spinner)findViewById(R.id.sp_course);
//    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
//                android.R.layout.simple_spinner_item, ParseXml.educationList);
//    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        education.setAdapter(adapter);
//        adapter = new ArrayAdapter<String>(this, 
//                android.R.layout.simple_spinner_item, ParseXml.courseList);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        course.setAdapter(adapter);
        theme =  (Spinner)findViewById(R.id.sp_theme);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
              android.R.layout.simple_spinner_item, ParseXml.themeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        theme.setAdapter(adapter);        
        title = (EditText) findViewById(R.id.title);
        msg = (EditText) findViewById(R.id.msg);
        if(action_type.equals("a")){
        	tv.setText(this.getString(R.string.answer));
        	ask_btn.setText(this.getString(R.string.answer));
//        	education.setVisibility(View.GONE);
//        	course.setVisibility(View.GONE);
        	theme.setVisibility(View.GONE);
        	title.setVisibility(View.GONE);
        }
	}
	private OnClickListener btnclk = new OnClickListener() {
    	public void onClick(View v){
    		switch(v.getId()) {
    			case R.id.title_right_btn:
    				finish();
    				break;
    			case R.id.askBtn:
    				Ask.this.createCancelProgressDialog();
    				new Thread(){ 
    					@Override
    					public void run(){
    						sending_ask();
    						Message m = new Message();
    						m.what = 333;
    						myHandler.sendMessage(m);
    					}
    				}.start();
    				break;
    			case R.id.imageID:
    				registerForContextMenu(v);
    		    	openContextMenu(v);
    		    	unregisterForContextMenu(v);
    				break;
    			case R.id.camera:
    				registerForContextMenu(v);
    		    	openContextMenu(v);
    		    	unregisterForContextMenu(v);
    				break;
    			default:
    				break;
    		}
    	}
    };
    public void onCreateContextMenu(ContextMenu menu, View v,  ContextMenuInfo menuInfo) {  
        if (v.getId()==R.id.imageID||v.getId()==R.id.camera) {
        	menu.add(0, 0, 0, this.getString(R.string.photo_album));
            menu.add(0, 1, 0, this.getString(R.string.take_picture));
            menu.add(0, 2, 0, this.getString(R.string.cancel));
        }
    }
    @Override  
    public boolean onContextItemSelected(MenuItem item) { 
    	Intent intent;
		switch(item.getItemId()) {
		case 0:
			intent = new Intent(Intent.ACTION_PICK, null);  
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");  
            startActivityForResult(intent, PHOTOPICK);
			break;
		case 1:
			String path = Environment.getExternalStorageDirectory().toString();
            new File(path + "/kaxa/").mkdir();
            filePath = path + "/kaxa/kaxacapture.jpg";
            filecapture = new File(filePath);
            try {
            	filecapture.createNewFile();
            } catch (IOException e) {
            	e.printStackTrace();
            }
            Uri outputFileUri = Uri.fromFile(filecapture);
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, PHOTOCAPTURE);  
			break;
		default:
			break;
		}
    	return super.onContextItemSelected(item);  
    }
    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }
    public static int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }
    public static Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri,int orientation) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
            float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
            float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        return srcBitmap;
    }
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    	photo.recycle();
    	System.gc();
        if (resultCode == NONE)  
            return;  
        if (requestCode == PHOTOCAPTURE) { 
        	Uri selectedImage = Uri.fromFile(new File(filePath));
        	int rotateImage = getCameraPhotoOrientation(Ask.this, selectedImage, filePath);
        	try {
				photo = getCorrectlyOrientedImage(Ask.this, selectedImage,rotateImage);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            imageView.setImageBitmap(photo); 
            photo_setup=true;
        }  
          
        if (data == null)  
            return;  
          
        if (requestCode == PHOTOPICK) {
        	Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();

            int rotateImage = getCameraPhotoOrientation(Ask.this, selectedImage, filePath);
        	try {
				photo = getCorrectlyOrientedImage(Ask.this, selectedImage,rotateImage);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            imageView.setImageBitmap(photo); 
            photo_setup=true;
        }  
        super.onActivityResult(requestCode, resultCode, data);  
    }  
	protected void sending_ask() {
		if(!this.network_ok) return;
		// TODO Auto-generated method stub
		List< NameValuePair> params = new ArrayList< NameValuePair>();
		if(action_type.equals("q")){
			if(photo_setup)
				params.add(new BasicNameValuePair("file",filePath));
			params.add(new BasicNameValuePair("AccID",userid));
			params.add(new BasicNameValuePair("title",title.getText().toString()));
			params.add(new BasicNameValuePair("content",msg.getText().toString()));
			Theme themeSelected = ParseXml.themeXmlList.get(Long.valueOf(theme.getSelectedItemId()).intValue());
			params.add(new BasicNameValuePair("tid",themeSelected.getThemeID()));
			params.add(new BasicNameValuePair("sj",themeSelected.getSubjectID()));
			params.add(new BasicNameValuePair("dg",themeSelected.getDegreeID()));
			
//			params.add(new BasicNameValuePair("sj",Long.toString(course.getSelectedItemId())));
//			params.add(new BasicNameValuePair("dg",Long.toString(education.getSelectedItemId()+1)));
			doMultiPost(WebUtil.HOST+WebUtil.phone_upload,params);
		}else{
			if(photo_setup)
				params.add(new BasicNameValuePair("file",filePath));
			params.add(new BasicNameValuePair("AccID",userid));
			params.add(new BasicNameValuePair("content",msg.getText().toString()));
			params.add(new BasicNameValuePair("disid",seq));
			doMultiPost(WebUtil.HOST+WebUtil.phone_reply,params);
		}
		finish();
	}
	boolean upload_fine=false;
    private void doMultiPost(String url, List< NameValuePair> params){
    	HttpClient client=new DefaultHttpClient();
    	HttpPost post=new HttpPost(url);
    	 try{
    		 //setup multipart entity
    		 MultipartEntity entity=new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
    		 for(int i=0;i< params.size();i++){
    			 //identify param type by Key
    			 if(params.get(i).getName().equals("file")){
    				 ByteArrayOutputStream bos = new ByteArrayOutputStream();
    				 photo.compress(CompressFormat.JPEG, 75, bos);
    				 byte[] data = bos.toByteArray();
    				 ByteArrayBody fileBody = new ByteArrayBody(data, "temp.jpg");

    				 entity.addPart("image",fileBody);
    			 }else{
    				 //不這樣處理會變亂碼
    				 entity.addPart(params.get(i).getName(),new StringBody(params.get(i).getValue(),"text/plain", Charset.forName( "UTF-8" )));
    			 }
    		 }
    		 post.setEntity(entity);
    		 //create response handler
    		 ResponseHandler< String> handler=new BasicResponseHandler();
    		 //execute and get response
    		 String response=new String(client.execute(post,handler).getBytes(),HTTP.UTF_8);

    		 DocumentBuilderFactory factory;
    		 DocumentBuilder builder;
    		 factory = DocumentBuilderFactory.newInstance();
    		 InputStream is = new ByteArrayInputStream(response.getBytes("UTF-8"));
    		 builder = factory.newDocumentBuilder();
    		 Document document = builder.parse(is);
    		 document.getDocumentElement().normalize();  
    		 NodeList elements = document.getElementsByTagName("Status");
    		 int iElementLength = elements.getLength();
    		 if (iElementLength != 1 ) upload_fine=false;
    		 Element element = (Element) elements.item(0);
    		 element.normalize();
    		 String check = element.getFirstChild().getNodeValue().toString();
    		 if (check.equals("0")) upload_fine=true;
    		 else upload_fine=false;
    	 }catch(Exception e){
    		 e.printStackTrace();
    	 }    
    }
}