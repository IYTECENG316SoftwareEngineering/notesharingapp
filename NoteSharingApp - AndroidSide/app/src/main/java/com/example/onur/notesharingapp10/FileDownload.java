package com.example.onur.notesharingapp10;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.ResponseServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class FileDownload extends Activity {

    private static String url_get_files = "http://notesharing-env.elasticbeanstalk.com/GetFilesServlet";
    private ArrayAdapter<String> fileArrayAdapter;
    private ArrayList<String> fileItems;
    JSONParser jParser = new JSONParser();
    int pos;
    private static final int SHOW_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_download);

        ListView fileView = (ListView) findViewById(R.id.files);
        fileItems = new ArrayList<String>();


        fileArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, fileItems);
        fileView.setAdapter(fileArrayAdapter);

        new GetFile().execute();

        Button fileSelectButton = (Button) findViewById(R.id.file_select_button);

        fileSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent uploadIntent = new Intent(getApplicationContext(), FileUpload.class);
                startActivity(uploadIntent);
            }
        });

        Button refreshButton = (Button) findViewById(R.id.refresh_file_list);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileItems.clear();
                new GetFile().execute();
            }
        });

        fileView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;//item position is saved
                Intent showFileIntent = new Intent(FileDownload.this, ShowFile.class);
                showFileIntent.putExtra("file", fileItems.get(position));
                showFileIntent.putExtra("position", pos);
                startActivityForResult(showFileIntent, SHOW_FILE);
            }
        });
    }

    private class GetFile extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {

            JSONArray json = jParser.getJSONFromUrl(url_get_files);
            String jsonString = json.toString(); // HttpClient?
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                int j = jsonArray.length() - 1;
                while (j >= 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        fileItems.add(i, jsonArray.getString(j));
                        j--;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        fileArrayAdapter.notifyDataSetChanged();

                    }
                });
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
        getMenuInflater().inflate(R.menu.menu_file_download, menu);
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
