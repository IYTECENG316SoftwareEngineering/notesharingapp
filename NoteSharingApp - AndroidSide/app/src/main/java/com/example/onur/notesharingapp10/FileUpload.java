package com.example.onur.notesharingapp10;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


public class FileUpload extends Activity {

    private static int RESULT_LOAD_IMG = 1;
    private static String url_upload_files = "http://notesharing-env.elasticbeanstalk.com/FileUploadServlet";
    private String filePath;
    JSONParser jParser = new JSONParser();
    JSONObject json;
    static InputStream is = null;
    String fileName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);


        //file is chosen with file dialog helper class
        FileDialog fileDialog;

        File mPath = new File(Environment.getExternalStorageDirectory() + "//");
        fileDialog = new FileDialog(this, mPath);
        //fileDialog.setFileEndsWith(".txt");
        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
            public void fileSelected(File file) {
                Log.d(getClass().getName(), "selected file " + file.toString());
                filePath = file.getPath();
                fileName = file.getName();
                TextView tx = (TextView) findViewById(R.id.textView);
                tx.setText(fileName);

            }
        });
        fileDialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
          public void directorySelected(File directory) {
              Log.d(getClass().getName(), "selected dir " + directory.toString());
         }
        });
        fileDialog.setSelectDirectoryOption(false);
        fileDialog.showDialog();

        //file upload task will begin
        Button uploadFile = (Button) findViewById(R.id.buttonUploadFile);

        uploadFile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                new UploadFile().execute();
            }
        });


    }

    private class UploadFile extends AsyncTask<String, String, String>
    {
        @Override
        protected String doInBackground(String... args) {
            Intent intent = getIntent();
            String user = intent.getStringExtra("id");

            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

            HttpPost httppost = new HttpPost(url_upload_files);

            MultipartEntity mpEntity = new MultipartEntity();
            FileBody localFileBody = new FileBody(new File(filePath), "image/jpg");
            mpEntity.addPart("userfile", localFileBody);
            try {
                mpEntity.addPart("user", new StringBody(user));
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
                            Intent newIntent = new Intent(getApplicationContext(), FileDownload.class);
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
}
