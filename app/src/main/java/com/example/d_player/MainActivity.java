package com.example.d_player;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.example.d_player.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.VideoView;

import org.w3c.dom.Text;

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

    // onClick method for 
    public void playMusic(View view) {

       Button playPause = findViewById(R.id.play);

       if (!mediaPlayer.isPlaying()) {
           mediaPlayer.start();
           playPause.setText(R.string.pause);
           timerHandler.postDelayed(updateTimer, 0);
       } else {
           mediaPlayer.pause();
           playPause.setText(R.string.play);
           timerHandler.removeCallbacks(updateTimer);
       }

    };

    private Runnable updateTimer = new Runnable() {

        @Override
        public void run() {

            int currentDuration = mediaPlayer.getCurrentPosition();

            TextView currentDurationText = (TextView) findViewById(R.id.currentDuration);
            currentDurationText.setText(milliSecondsToTimer(currentDuration));

            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        TextView currentDurationText = ( TextView ) findViewById(R.id.currentDuration);
        mediaPlayer = MediaPlayer.create(this, R.raw.fast_foot);
        SeekBar seekbar = ( SeekBar ) findViewById(R.id.seekBar);
        seekbar.setMax(mediaPlayer.getDuration());

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStartTrackingTouch(SeekBar seekbar){}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar){}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                Log.i("Seek bar Progress", Integer.toString(progress));

            }



        });

        totalDuration = milliSecondsToTimer(mediaPlayer.getDuration());

        TextView duration = ( TextView ) findViewById(R.id.duration);
        Log.i("Duration", totalDuration);
        duration.setText(totalDuration);
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
