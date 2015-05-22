package com.example.onur.notesharingapp10;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class ShowFile extends Activity {

    private static String url_download_files = "http://notesharing-env.elasticbeanstalk.com/FileDownloadServlet";
    private static String url_delete_file = "http://notesharing-env.elasticbeanstalk.com/DeleteFileServlet";
    int pos;
    JSONObject json;
    JSONParser jParser = new JSONParser();
    String id;
    String file_detail;
    String username;
    String file_user;
    String file_name;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_files);

        TextView fileUser = (TextView) findViewById(R.id.file_user);
        TextView fileText = (TextView) findViewById(R.id.file_detail);
        TextView fileNumber = (TextView) findViewById(R.id.file_number);

        Intent intent = getIntent();
        file_detail = intent.getStringExtra("file");
        username = intent.getStringExtra("id");
        pos = intent.getIntExtra("position", 0);
        pos++;

        int start = file_detail.indexOf("(");
        int end = file_detail.indexOf(")");

        //sender of the file is retrieved
        file_user = file_detail.substring(start+1, end);

        //id number of the file is retrived
        id = file_detail.substring(0, file_detail.indexOf("(")-1);

        //file name is retrieved
        file_name = file_detail.substring(end+4);

        fileNumber.setText("File Number: " + id);

        fileUser.setText("Writer: " + file_user);

        fileText.setText("Message: " + file_name);

        // instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(ShowFile.this);
        mProgressDialog.setMessage("Download On Progress");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        Button downloadButton = (Button) findViewById(R.id.file_download_button);

        final DownloadFile downloadTask = new DownloadFile(ShowFile.this);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadTask.execute("the url to the file you want to download");
            }
        });

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });

        Button deleteButton = (Button) findViewById(R.id.file_delete_button);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.equals("admin")){
                    new DeleteFile().execute();
                }
                else if(file_user.equals(username)){
                    new DeleteFile().execute();
                }
                else{
                    Toast.makeText(getApplicationContext(), "You can only delete your own files", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private class DownloadFile extends AsyncTask<String, String, String>
    {
        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadFile(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... args) {

            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            HttpGet httpget = new HttpGet(url_download_files);
            httpget.setHeader("id", "" + pos);
            HttpResponse response = null;
            try {
                response = httpclient.execute(httpget);
            } catch (IOException e) {
                e.printStackTrace();
            }
            HttpEntity entity=null;
            if(response!=null) {
                entity = response.getEntity();
            }
            if (entity != null) {
                long len = entity.getContentLength();
                try {
                    long lenghtOfFile = entity.getContentLength();
                    InputStream is = entity.getContent();

                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File (sdCard.getAbsolutePath() + "/notesharing");
                    dir.mkdirs();
                    File file = new File(dir, file_name);
                    FileOutputStream f = new FileOutputStream(file);

                    byte[] buffer = new byte[1024];
                    int len1 = 0;
                    long count = 0;
                    while ((len1 = is.read(buffer)) > 0) {
                        count += len1;
                        onProgressUpdate("" + (int) ((count * 100) / lenghtOfFile));
                        f.write(buffer, 0, len1);
                    }
                    is.close();
                    f.close();


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }


        protected void onProgressUpdate(String... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context,"File downloaded to directory sdcard/notesharing", Toast.LENGTH_LONG).show();
        }
    }

    private class DeleteFile extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {

            //message info is sent to server
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user",file_user));
            params.add(new BasicNameValuePair("message",file_name));
            params.add(new BasicNameValuePair("id", id));
            json = jParser.makeHttpRequest(url_delete_file, "GET", params);

            try {
                //server response is received
                String s = json.getString("info");
                Log.d("Msg", json.getString("info"));
                //if file is successfully deleted file list activity will start
                if(s.equals("success")){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "File successfully deleted", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), FileDownload.class);
                            startActivity(intent);
                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Failed to delete file", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_files, menu);
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
