package com.example.d_player;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.example.d_player.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer; // Creating a mediaPlayer Variable, which we will use in the onCreate method.
    String totalDuration; // Creatng a total Duration method.
    Handler timerHandler = new Handler(); // making a new HandlerObject to handle time.

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

    // onClick method for the play&Pause button
    public void playMusic(View view) {

       Button playPause = findViewById(R.id.play);

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

    };

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



    private ArrayList<HashMap<String,String>> getPlayList(String rootPath) {
        ArrayList<HashMap<String,String>> fileList = new ArrayList<>();


        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles(); //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (File file : files) {
                if (file.isDirectory()) {
                    if (getPlayList(file.getAbsolutePath()) != null) {
                        fileList.addAll(getPlayList(file.getAbsolutePath()));
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(".mp3")) {
                    HashMap<String, String> song = new HashMap<>();
                    song.put("file_path", file.getAbsolutePath());
                    song.put("file_name", file.getName());
                    fileList.add(song);
                }
            }
            return fileList;
        } catch (Exception e) {
            return null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // getting all the media files from the phone
        ArrayList<HashMap<String,String>> songList=getPlayList(Environment.getExternalStorageDirectory().getAbsolutePath());
        if(songList!=null){
            for(int i=0;i<songList.size();i++){
                String fileName=songList.get(i).get("file_name");
                String filePath=songList.get(i).get("file_path");
                //here you will get list of file name and file path that present in your device
                Log.i("file details "," name ="+fileName +" path = "+filePath);
               }
            }

        // create a media player object.
        mediaPlayer = MediaPlayer.create(this, R.raw.fast_foot);
        final SeekBar seekbar = ( SeekBar ) findViewById(R.id.seekBar);
        // set the max of seekbar to the max duration of the song - as in upto where we can seek the
        // seekbar.
        seekbar.setMax(mediaPlayer.getDuration());

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
