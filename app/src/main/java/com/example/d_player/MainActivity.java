package com.example.d_player;

import android.media.MediaPlayer;
import android.os.Bundle;

import com.example.d_player.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ToggleButton;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
//    Button playPause = findViewById(R.id.play);

    public void playMusic(View view) {

       Button playPause = findViewById(R.id.play);

       if (!mediaPlayer.isPlaying()) {
           mediaPlayer.start();
           playPause.setText(R.string.pause);
       } else {
           mediaPlayer.pause();
           playPause.setText(R.string.play);
       }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mediaPlayer = MediaPlayer.create(this, R.raw.fast_foot);
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
