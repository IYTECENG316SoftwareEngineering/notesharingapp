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


public class SignUpActivity extends Activity {

    EditText uname, password, password2, name, email;
    String EMAIL_REGEX;

    {
        EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
    }

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    JSONObject json;
    private static String url_login = "http://notesharing-env.elasticbeanstalk.com/RegisterServlet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //views are set
        uname = (EditText) findViewById(R.id.user);
        password = (EditText) findViewById(R.id.pass);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        password2 = (EditText) findViewById(R.id.pass2);

        //if login button is clicked, login activity will start
        Button loginButton2 = (Button) findViewById(R.id.button2);

        loginButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent2 = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent2);
            }
        });


        //if register button is clicked registar task will be executed
        Button registerButton2 = (Button) findViewById(R.id.button1);

        registerButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Register().execute();
            }
        });
    }

    private class Register extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            // Getting user inputs
            String username = uname.getText().toString();
            String pass = password.getText().toString();
            String pass2 = password2.getText().toString();
            String nm = name.getText().toString();
            String em = email.getText().toString();

            //checking for empty fields
            if(nm.equals("") || username.equals("") || pass.equals("")){
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Don't leave fields empty", Toast.LENGTH_LONG).show();
                    }
                });
            }
            //checking for email extension
            else if(!emailCheck(em)){
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Email must be @domain.edu.tr format!", Toast.LENGTH_LONG).show();
                    }
                });
            }
            //checking for password validation
            else if (!pass.equals(pass2)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Passwords don't match please rewrite!", Toast.LENGTH_LONG).show();

                    }
                });
            } else {
                //user info is sent to server
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("u", username));
                params.add(new BasicNameValuePair("p", pass));
                params.add(new BasicNameValuePair("n", nm));
                params.add(new BasicNameValuePair("e", em));
                json = jParser.makeHttpRequest(url_login, "GET", params);

                try {
                    //server response is received
                    String s = json.getString("info");
                    Log.d("Msg", json.getString("info"));
                    //if register is successful main page is opened
                    if (s.equals("success")) {
                        Intent login = new Intent(getApplicationContext(), MainPage.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(login);
                        finish();
                    } else if (s.equals("fail")) {
                        finish();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }



            return null;
        }

    }

    public boolean emailCheck(String email){
        if(email.endsWith(".edu.tr") || email.matches(EMAIL_REGEX)){
            return true;
        }
        else{
            return false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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
