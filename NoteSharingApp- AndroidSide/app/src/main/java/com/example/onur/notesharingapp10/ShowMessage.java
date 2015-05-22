package com.example.onur.notesharingapp10;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ShowMessage extends Activity {

    String message_detail;
    String username;
    String message_user;
    String message;
    JSONObject json;
    JSONParser jParser = new JSONParser();
    String id;
    private static String url_delete_message = "http://notesharing-env.elasticbeanstalk.com/DeleteMessageServlet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_message);

        TextView messageUser = (TextView) findViewById(R.id.message_user);
        TextView messageText = (TextView) findViewById(R.id.message_detail);
        TextView messageNumber = (TextView) findViewById(R.id.message_number);
        Button deleteMessage = (Button) findViewById(R.id.message_delete_button);

        //message item and username are received
        Intent intent = getIntent();
        message_detail = intent.getStringExtra("message");
        username = intent.getStringExtra("id");

        int start = message_detail.indexOf("(");
        int end = message_detail.indexOf(")");

        //sender of the message is retrieved
        message_user = message_detail.substring(start+1, end);

        //id number of the message is retrived
        id = message_detail.substring(0, message_detail.indexOf("(")-1);

        //message is retrieved
        message = message_detail.substring(end+4);

        messageNumber.setText("Message Number: " + id);

        messageUser.setText("Writer: " + message_user);

        messageText.setText("Message: " + message);

        //if delete message is clicked and the logged user is admin or the owner of the message delete message task will execute
        deleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.equals("admin")){
                    new DeleteMessage().execute();
                }
                else if(message_user.equals(username)){
                    new DeleteMessage().execute();
                }
                else{
                    Toast.makeText(getApplicationContext(), "You can only delete your own messages", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private class DeleteMessage extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {

            //message info is sent to server
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user",message_user));
            params.add(new BasicNameValuePair("message",message));
            params.add(new BasicNameValuePair("id", id));
            json = jParser.makeHttpRequest(url_delete_message, "GET", params);

            try {
                //server response is received
                String s = json.getString("info");
                Log.d("Msg", json.getString("info"));
                //if message is successfully deleted message board activity will start
                if(s.equals("success")){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Message successfully deleted", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MessageBoard.class);
                            startActivity(intent);
                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Failed to delete message", Toast.LENGTH_LONG).show();
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
        getMenuInflater().inflate(R.menu.menu_show_message, menu);
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
