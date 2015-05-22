package com.example.onur.notesharingapp10;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class FolderList extends Activity {

    private ArrayList<String> folders;
    private ArrayAdapter<String> fileAdapter;
    ListView fileView;
    private static final int SHOW_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_list);

        Intent intent = getIntent();
        final String username = intent.getStringExtra("id");
        final String type = intent.getStringExtra("type");

        folders = new ArrayList<String>();
        fileView = (ListView) findViewById(R.id.folders);

        folders.add("Embedded Systems");
        folders.add("Operating Systems");
        folders.add("Software Engineering");
        folders.add("Database Management");
        folders.add("Web Development");
        folders.add("Mobile Application Development");
        folders.add("Computer Architecture");
        folders.add("Differential Equations");
        folders.add("Logic Design");
        folders.add("Numerical Computation");
        folders.add("Computer Networks");
        folders.add("Project Management");

        fileAdapter = new ArrayAdapter<String>(this,
                R.layout.mylist, R.id.Itemname, folders);
        fileView.setAdapter(fileAdapter);

        fileView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(type.equals("download")){
                    Intent showFileIntent = new Intent(FolderList.this, FileDownload.class);
                    showFileIntent.putExtra("id", username);
                    showFileIntent.putExtra("folder", folders.get(position));
                    startActivity(showFileIntent);
                }
                else if(type.equals("upload")){
                    Intent showFileIntent = new Intent(FolderList.this, FileUpload.class);
                    showFileIntent.putExtra("id", username);
                    showFileIntent.putExtra("folder", folders.get(position));
                    startActivity(showFileIntent);
                }
                else if(type.equals("photo")){
                    Intent showFileIntent = new Intent(FolderList.this, TakePhotoActivity.class);
                    showFileIntent.putExtra("id", username);
                    showFileIntent.putExtra("folder", folders.get(position));
                    startActivity(showFileIntent);
                }
                else if(type.equals("pu")){
                    Intent showFileIntent = new Intent(FolderList.this, SharePhoto.class);
                    showFileIntent.putExtra("id", username);
                    showFileIntent.putExtra("folder", folders.get(position));
                    startActivity(showFileIntent);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_folder_list, menu);
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
