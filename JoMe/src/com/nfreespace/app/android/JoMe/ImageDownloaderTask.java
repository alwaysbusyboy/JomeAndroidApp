package com.nfreespace.app.android.JoMe;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
 
class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    @SuppressWarnings("rawtypes")
	private final WeakReference imageViewReference;
    
     
    Context cont;   
    String bitFileName=new String();  
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public ImageDownloaderTask(ImageView imageView,Context context,String fileName) {
        imageViewReference = new WeakReference(imageView);
        this.bitFileName=fileName;       
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
                   imageView.setImageBitmap(bitmap);    
                } 
            }   
            
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
    
    public String writeFileToInternalStorage(Context context, Bitmap outputImage) {

    	if(!bitFileName.equals(""))
    	{
    		 FileOutputStream fos = null;
    			try {
    				fos = context.openFileOutput(bitFileName, Context.MODE_PRIVATE);
    			} catch (FileNotFoundException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    	        outputImage.compress(CompressFormat.JPEG, 100, fos);
    	        try {
    				fos.close();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}

    	}
       
        return bitFileName;
    } 
 
}