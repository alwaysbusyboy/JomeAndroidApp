package com.nfreespace.app.android.JoMe.Adapter;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nfreespace.app.android.JoMe.constant;
import com.nfreespace.app.android.JoMe.Object.JMCacheObject;
 
class ImageDownloaderTask1 extends AsyncTask<String, Void, Bitmap> {
    @SuppressWarnings("rawtypes")
	private final WeakReference imageViewReference;
    private final WeakReference progressViewReference;
     
    Context cont;   
    String  bitFileName=new String();  
    int     position;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public ImageDownloaderTask1(ImageView imageView,ProgressBar progress,Context context,String fileName,int pos) {
        imageViewReference = new WeakReference(imageView);
        progressViewReference = new WeakReference(progress);
        this.bitFileName=fileName;  
        this.position = pos;
        cont=context;       
    }
 
    @Override
    // Actual download method, run in the task thread
    protected Bitmap doInBackground(String... params) {
        // params comes from the execute() call: params[0] is the url.
        return downloadBitmap(params[0]);
    }
 
    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        } 
        if (imageViewReference != null) {
            ImageView imageView =(ImageView)imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {
                	
                	Object tag = imageView.getTag();
                	int tagInt = ((Integer) tag).intValue();
                	if(tagInt == position)
                		imageView.setImageBitmap(bitmap);	
                	
                	saveBitmap(bitmap);
                }
            } 
            
            ProgressBar progress = (ProgressBar)progressViewReference.get();
            if(progress != null)
            	progress.setVisibility(View.INVISIBLE);
            
         }        
    }
    static Bitmap downloadBitmap(String url) {
    	        
         final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
         final HttpGet getRequest = new HttpGet(url);
         try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            
            if (statusCode == 301 || statusCode == 302)
            {
                Header redirect = response.getFirstHeader("Location");
                if (client instanceof AndroidHttpClient)
                    ((AndroidHttpClient)client).close();
                return downloadBitmap(redirect.getValue());
            }
            
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode
                        + " while retrieving bitmap from " + url);
                return null;
            }
 
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                   
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or
            // IllegalStateException
            getRequest.abort();
            Log.w("ImageDownloader", "Error while retrieving bitmap from " + url);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return null;
    }
    
    public void saveBitmap(Bitmap bitmap)
    {
    	if(bitFileName != null)
    	{
    		JMCacheObject object = new JMCacheObject(bitFileName, bitmap);
    		constant.gCacheList.add(object);
    	}
    }
   
  
}