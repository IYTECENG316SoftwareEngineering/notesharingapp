package com.example.onur.notesharingapp10;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class TakePhotoActivity extends Activity {

    private static String url_upload_files = "http://notesharing-env.elasticbeanstalk.com/FileUploadServlet";
    ImageView mImageView;
    private String filePath ="";
    public static final int MEDIA_TYPE_IMAGE = 1;
    JSONParser jParser = new JSONParser();
    JSONObject json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        //check for camera support
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // Open default camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, MEDIA_TYPE_IMAGE);

        } else {
            Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MEDIA_TYPE_IMAGE && resultCode == RESULT_OK && data!=null) {
            mImageView = (ImageView) findViewById(R.id.imgView);

            //show the image in imageview
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            mImageView.setImageBitmap(photo);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            //create file for the image
            final File file = new File(Environment.getExternalStorageDirectory()+File.separator +
                    String.valueOf(System.currentTimeMillis()) + "image.jpg");
            try {
                file.createNewFile();
                FileOutputStream fo = new FileOutputStream(file);

                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //photo upload task will start
            Button uploadFile = (Button) findViewById(R.id.buttonUploadFile);

            uploadFile.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    filePath = file.getPath();
                    new UploadFile().execute();
                }
            });
        }
    }


    private class UploadFile extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            Intent intent = getIntent();
            final String user = intent.getStringExtra("id");
            String folder = intent.getStringExtra("folder");

            //send data to server as multipart entity
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

            HttpPost httppost = new HttpPost(url_upload_files);

            MultipartEntity mpEntity = new MultipartEntity();
            FileBody localFileBody = new FileBody(new File(filePath), "image/jpg");
            mpEntity.addPart("userfile", localFileBody);
            try {
                mpEntity.addPart("user", new StringBody(user));
                mpEntity.addPart("folder", new StringBody(folder));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            httppost.setEntity(mpEntity);
            HttpResponse response = null;
            try {
                response = httpclient.execute(httppost);
            } catch (IOException e) {
                e.printStackTrace();
            }
            HttpEntity resEntity = response.getEntity();


            if (resEntity != null) {
                try {
                    resEntity.consumeContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            httpclient.getConnectionManager().shutdown();

            json = jParser.makeHttpRequest2(url_upload_files);

            try {
                if(json.getString("info").equals("success")) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Upload Successful", Toast.LENGTH_LONG).show();
                            Intent newIntent = new Intent(getApplicationContext(), FolderList.class);
                            newIntent.putExtra("id", user);
                            newIntent.putExtra("type", "download");
                            startActivity(newIntent);
                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_take_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
