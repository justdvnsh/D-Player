package com.example.d_player;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.d_player.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer = null; // Creating a mediaPlayer Variable, which we will use in the onCreate method.
    String totalDuration; // Creatng a total Duration method.
    Handler timerHandler = new Handler(); // making a new HandlerObject to handle time.

    Context context; // Context variable to get the current working context

    public static final int RUNTIME_PERMISSION_CODE = 7; // Permissiom code for read_write permission

    String[] ListElements = new String[] { }; // Dynamic array of String

    ListView listView; // a list view var

    List<String> ListElementsArrayList ; // List of String to store the songs

    List<String> SongsData ; // List of strings to store the full data path of the song

    ArrayAdapter<String> adapter ; // adapter to display the list elements in the list view

    ContentResolver contentResolver; // content resolver to query the storage system of the mobile

    Cursor cursor; // cursor to actually query

    Uri uri; // path resolver

    public static String path;

    // Makeing the file variable and Array containing the files.
    private File root;
    private ArrayList<File> fileList = new ArrayList<File>();

    // Creating a function to make milliseconds into minutes and seconds.
    public String milliSecondsToTimer(int duration) {

        long minutes = ( duration / 1000 ) / 60;
        long seconds = ( duration / 1000 ) % 60;

        if ( minutes < 10 & seconds < 10) {

            return "0" + minutes + ":" + "0" + seconds;

        } else if ( minutes < 10 & seconds >= 10) {

            return "0" + minutes + ":" + seconds;

        } else if ( minutes >= 10 & seconds < 10 ) {

            return minutes + ":" + "0" + seconds;

        } else {

            return minutes + ":" + seconds;

        }

    };

//    // onClick method for the play&Pause button
//    public void playMusic(View view) {
//       Button playPause = findViewById(R.id.play);
//
//
//
//    };

    // a runnable type Object , which is actually the meat of the updating timer.
    private Runnable updateTimer = new Runnable() {

        // the main run command.
        @Override
        public void run() {

            // get current position.
            int currentDuration = mediaPlayer.getCurrentPosition();

            // find the running timer id.
            TextView currentDurationText = (TextView) findViewById(R.id.currentDuration);
            // set its text.
            currentDurationText.setText(milliSecondsToTimer(currentDuration));

            // then post it every .5 seconds to update the time.
            timerHandler.postDelayed(this, 500);
        }
    };

    public void GetAllMediaMp3Files(){

        // content resolver
        contentResolver = context.getContentResolver();

        // the internal storage of the path of the phone.
        // Since I have all the music files in my internal storage.
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        // query all the files at the given uri ( path )
        cursor = contentResolver.query(
                uri, // Uri
                null,
                null,
                null,
                null
        );

        if (cursor == null) {

            Toast.makeText(MainActivity.this,"Something Went Wrong.", Toast.LENGTH_LONG);

        } else if (!cursor.moveToFirst()) {

            Toast.makeText(MainActivity.this,"No Music Found on SD Card.", Toast.LENGTH_LONG);

        }
        else {

            // if cursor is populated, find the Title of the song and data.
            int Data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int Title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);

            //Getting Song ID From Cursor.
            //int id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);

            do {

                // You can also get the Song ID using cursor.getLong(id).
                //long SongID = cursor.getLong(id);

                String SongTitle = cursor.getString(Title);
                String SongData = cursor.getString(Data);

                // Adding Media File Names to ListElementsArrayList.
                ListElementsArrayList.add(SongTitle);
                SongsData.add(SongData);

            } while (cursor.moveToNext());
        }
    }

    // Creating Runtime permission function.
    // Asks for permission on teh run time.
    public void AndroidRuntimePermission(){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){

                    AlertDialog.Builder alert_builder = new AlertDialog.Builder(MainActivity.this);
                    alert_builder.setMessage("External Storage Permission is Required.");
                    alert_builder.setTitle("Please Grant Permission.");
                    alert_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    RUNTIME_PERMISSION_CODE

                            );
                        }
                    });

                    alert_builder.setNeutralButton("Cancel",null);

                    AlertDialog dialog = alert_builder.create();

                    dialog.show();

                }
                else {

                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            RUNTIME_PERMISSION_CODE
                    );
                }
            }else {

            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){

        switch(requestCode){

            case RUNTIME_PERMISSION_CODE:{

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }
                else {

                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // making the list into arrayList to display into the listview.
        ListElementsArrayList = new ArrayList<>(Arrays.asList(ListElements));

        SongsData = new ArrayList<>(Arrays.asList(ListElements));

        // getting the listview
        listView = (ListView) findViewById(R.id.listview1);

        // getting all the media files from the phone
        context = getApplicationContext();

        // setting up the listview.
        adapter = new ArrayAdapter<String>
                (MainActivity.this, android.R.layout.simple_list_item_1, ListElementsArrayList);

        // Requesting run time permission for Read External Storage.
        AndroidRuntimePermission();

        // actually getting the song files.
        GetAllMediaMp3Files();

        listView.setAdapter(adapter);

        Log.i("MP3 FILES FOUND ARE", ListElementsArrayList.toString());
        Log.i("Length", Integer.toString(ListElementsArrayList.size()));
        Log.i("MP3 Data FOUND ARE", SongsData.toString());
        Log.i("Length", Integer.toString(SongsData.size()));

        // ListView on item selected listener.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
                // TODO Make the next and previous buttons work.
                // TODO Make the Repeat and Shuffle buttons work.

                String song = parent.getAdapter().getItem(position).toString();

                for ( String s: SongsData ) {

                    if ( s.contains(song) ) {

                        if(mediaPlayer!=null)
                        {
                            mediaPlayer.release();
                            mediaPlayer=null;
                        }

                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(MainActivity.this , Uri.parse(s));
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            timerHandler.postDelayed(updateTimer, 0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        final SeekBar seekbar = ( SeekBar ) findViewById(R.id.seekBar);
                        // set the max of seekbar to the max duration of the song - as in upto where we can seek the
                        // seekbar.
                        seekbar.setMax(mediaPlayer.getDuration());
                        seekbar.setProgress(0);
                        final Button playPause = ( Button ) findViewById(R.id.play);
                        playPause.setText("||");
                        playPause.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // check if the music is playing
                                if (!mediaPlayer.isPlaying()) {
                                    mediaPlayer.start();
                                    playPause.setText(R.string.pause);
                                    // send updates every second to update the text of the timer running .
                                    timerHandler.postDelayed(updateTimer, 0);
                                } else {
                                    mediaPlayer.pause();
                                    playPause.setText(R.string.play);
                                    // if music is paused , remove all callbacks and do not send any update.
                                    timerHandler.removeCallbacks(updateTimer);
                                }
                            }
                        });


//        //making a new timer object to update the seekbar every second and run a piece of code.
//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//
//                // to change the position of the seekbar every second.
//               seekbar.setProgress(mediaPlayer.getCurrentPosition());
//
//            }
//        }, 0, 1000);// 0 -> starts immediately ; 1000 -> updates every second.

                        // setting up an onChangeListener.
                        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                            @Override
                            public void onStartTrackingTouch(SeekBar seekbar){}

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar){}

                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                                // the seek bar seeks to the current progress .
                                mediaPlayer.seekTo(progress);

                            }



                        });

                        totalDuration = milliSecondsToTimer(mediaPlayer.getDuration());

                        TextView duration = ( TextView ) findViewById(R.id.duration);
                        Log.i("Duration", totalDuration);
                        duration.setText(totalDuration);

                        // creating the forward and backward functionality.
                        Button forward = ( Button ) findViewById(R.id.forward);
                        Button backward = ( Button ) findViewById(R.id.backward);
                        Button next = ( Button ) findViewById(R.id.next);
                        Button previous = ( Button ) findViewById(R.id.previous);

                        forward.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                seekbar.setProgress(mediaPlayer.getCurrentPosition() + 5000);
                            }
                        });

                        backward.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                seekbar.setProgress(mediaPlayer.getCurrentPosition() - 5000);
                            }
                        });

                    }

                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
