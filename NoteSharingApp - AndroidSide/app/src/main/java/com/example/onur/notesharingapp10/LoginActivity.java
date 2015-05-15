package com.example.onur.notesharingapp10;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends Activity {

    EditText uname, password; //Edit text field for username and password
    JSONParser jParser = new JSONParser();// Creating JSON Parser object
    JSONObject json;
    //url for the servlet
    private static String url_login = "http://notesharing-env.elasticbeanstalk.com/LoginServlet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        uname = (EditText) findViewById(R.id.txtUser);
        password = (EditText) findViewById(R.id.txtPass);

        //if register button is clicked, sign up activity is started
        Button registerButton2 = (Button) findViewById(R.id.button2);

        registerButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent2 = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(registerIntent2);
            }
        });

        Button loginButton2 = (Button) findViewById(R.id.button1);

        //if login button is clicked login task is started
        loginButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Login().execute();
            }
        });
    }

    private class Login extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            // Getting username and password from user input
            String username = uname.getText().toString();
            String pass = password.getText().toString();

            //username and password values are sent to server
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("u",username));
            params.add(new BasicNameValuePair("p",pass));
            json = jParser.makeHttpRequest(url_login, "GET", params);

            try {
                //the server response is received
                String s = json.getString("info");
                Log.d("Msg", json.getString("info"));
                //if login is successful main page activity is started
                if (s.equals("success")) {
                    Intent login = new Intent(getApplicationContext(), MainPage.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    login.putExtra("id", username); //username is sent to the activity as we will need it later
                    startActivity(login);
                    finish();
                }
                //if login is not successful a message is shown
                else{
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Wrong username or password!", Toast.LENGTH_LONG).show();
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
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
