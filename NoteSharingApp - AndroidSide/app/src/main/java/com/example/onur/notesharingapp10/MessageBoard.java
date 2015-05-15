package com.example.onur.notesharingapp10;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MessageBoard extends Activity {

    private ArrayAdapter<String> messageArrayAdapter;
    private ArrayList<String> messageItems;
    private static String url_get_message = "http://notesharing-env.elasticbeanstalk.com/GetMessageServlet";
    private static String url_add_message = "http://notesharing-env.elasticbeanstalk.com/AddMessageServlet";
    JSONParser jParser = new JSONParser();
    JSONObject json;
    String username;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board);

        //username is received
        Intent intent = getIntent();
        username = intent.getStringExtra("id");

        //views are set
        ListView messageView = (ListView) findViewById(R.id.messages);
        final Button addTodoButton = (Button) findViewById(R.id.send_message_button);
        final Button refreshButton = (Button) findViewById(R.id.refresh);
        final EditText editMessage = (EditText) findViewById(R.id.editText);

        //arraylist for messages is set with adapter in listview
        messageItems = new ArrayList<String>();
        messageArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_textview, messageItems);
        messageView.setAdapter(messageArrayAdapter);

        //get message list task is executed
        new GetMessage().execute();

        //a message is added
        addTodoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddMessage().execute();
                editMessage.setText("");
            }
        });

        //refresh the list
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageItems.clear();
                new GetMessage().execute();
            }
        });

        //If a message is clicked Show message activity will open to show details of the message
        messageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent showMessageIntent = new Intent(getApplicationContext(), ShowMessage.class);
                showMessageIntent.putExtra("id", username);
                showMessageIntent.putExtra("message", messageItems.get(position));
                startActivity(showMessageIntent);
            }
        });
    }

    private class GetMessage extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {

            //messages are received from server and set to the listview
            JSONArray json = jParser.getJSONFromUrl(url_get_message);
            String jsonString = json.toString();
            try {
                jsonArray = new JSONArray(jsonString);
                int j=jsonArray.length()-1;
                while(j>=0){
                    for (int i = 0; i < jsonArray.length(); i++) {
                        messageItems.add(i, jsonArray.getString(j));
                        j--;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    messageArrayAdapter.notifyDataSetChanged();
                    }
                });
            } catch (JSONException e) {
                    // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private class AddMessage extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {


            EditText todoEditText = (EditText) findViewById(R.id.editText);

            String message = todoEditText.getText().toString();

            //when a new message is added username and message are sent to server
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("u",username));
            params.add(new BasicNameValuePair("m",message));
            json = jParser.makeHttpRequest(url_add_message, "GET", params);

            try {
                String s = json.getString("info");
                Log.d("Msg", json.getString("info"));
                //if message is successfully added message list will be updated
                if (s.equals("success")) {
                    messageItems.clear();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            messageArrayAdapter.notifyDataSetChanged();

                        }
                    });
                    new GetMessage().execute();
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
        getMenuInflater().inflate(R.menu.menu_message_board, menu);
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
