package com.example.onur.notesharingapp10;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        //username of the logged user is received
        Intent intent = getIntent();
        final String username = intent.getStringExtra("id");

        //If message button is clicked message board activity will open
        Button messageButton = (Button) findViewById(R.id.message_button);

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent messageIntent = new Intent(getApplicationContext(), MessageBoard.class);
                messageIntent.putExtra("id", username);
                startActivity(messageIntent);
            }
        });

        //If take photo button is clicked TakePhotoActivity will open
        Button takePhotoButton = (Button) findViewById(R.id.take_photo_button);

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoIntent = new Intent(getApplicationContext(), TakePhotoActivity.class);
                photoIntent.putExtra("id", username);
                startActivity(photoIntent);
            }
        });

        //If share file button is clicked file upload activity will open
        Button fileUploadButton = (Button) findViewById(R.id.share_file_button);

        fileUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent uploadIntent = new Intent(getApplicationContext(), FileUpload.class);
                uploadIntent.putExtra("id", username);
                startActivity(uploadIntent);
            }
        });

        //If share photo button is clicked share photo activity will open
        Button photoUploadButton = (Button) findViewById(R.id.share_photo_button);

        photoUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent uploadPhotoIntent = new Intent(getApplicationContext(), SharePhoto.class);
                uploadPhotoIntent.putExtra("id", username);
                startActivity(uploadPhotoIntent);
            }
        });

        //If Show repositories button is clicked file download activity will open
        Button fileDownloadButton = (Button) findViewById(R.id.show_files_button);

        fileDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent downloadIntent = new Intent(getApplicationContext(), FileDownload.class);
                startActivity(downloadIntent);
            }
        });


        //If logout button is clicked directed to main activity
        Button btnLogout = (Button) findViewById(R.id.logout_button);

        btnLogout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent login = new Intent(getApplicationContext(), MainActivity.class);
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_page, menu);
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
