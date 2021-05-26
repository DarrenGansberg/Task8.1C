package com.darrengansberg.youtubeplayer;
/*=========================MainActivity.java ===============================
Written by: Darren Gansberg
Copyright: 2021, All rights Reserved.
Description: The MainActivity class is the only activity used by the YouTube Video app,
and utilises fragments to display a basic edittext box and play button which allow the user
to enter the url of a YouTube video and play the video.
 */

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.support.annotation.NonNull;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

public class MainActivity extends AppCompatActivity {

    private YouTubePlayerFragment mFragment; //used to display the video.
    private YouTubePlayer mPlayer;
    private YouTubePlayer.Provider mProvider;

    //the id of the Video that would be returned by the YouTube Data API
    private String mVideoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerPlayListener();
    }

    private void registerPlayListener()
    {
        //Register for activation of the play button.
        FragmentManager fm = getFragmentManager();
        AppHomeFragment fragment = (AppHomeFragment)getFragmentManager().findFragmentById(R.id.activity_main);
        fragment.AddOnPlayListener(new PlayListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //clean up resources.
        if (mPlayer != null)
        {
            mPlayer.release();
            mPlayer = null;
            mProvider = null;
        }
    }

    //Handler for configuration changes, simply causes video to go into fullscreen mode.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mFragment != null) //is null when the video is playing, so we'll switch to Full screen mode
        {
            // Checks the orientation of the screen
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mPlayer.setFullscreen(true);
            }
        }
    }


    //Setups up the YouTubeFragment that is used to display the video to the viewer.
    protected void setupVideoView()
    {
        mFragment = new YouTubePlayerFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.activity_main, mFragment)
                .addToBackStack(null)
                .commit();
        String key = getString(R.string.youtube_api_key);
        mFragment.initialize(key, new PlayerInitialisationListener());

    }

    //Loads and plays the videos
    protected void playVideo(@NonNull String videoId)
    {
        mPlayer.loadVideo(videoId);
        mPlayer.play();
    }

    //Listener to called upon, and handling, post initialisation of YouTubeFragment
    private class PlayerInitialisationListener implements YouTubePlayer.OnInitializedListener{

        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

            //ensure references are maintained following initialisation otherwise they appear
            //to cease to exist and the app crashes
            mProvider = provider;
            mPlayer = youTubePlayer;
            playVideo(mVideoId);
            //register a listener so that when the video is displayed and the user hits the back
            //button the video displaying fragment can be popped (reversing changes)
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.addOnBackStackChangedListener(new VideoPlayerViewBackListener());
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            Toast.makeText(getApplicationContext(), getString(R.string.video_play_error), Toast.LENGTH_SHORT).show();
        }
    }

    //A listener to handle back button when video is being displayed.
    private class VideoPlayerViewBackListener implements FragmentManager.OnBackStackChangedListener{

        @Override
        public void onBackStackChanged() {

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.removeOnBackStackChangedListener(this);
            fragmentManager.popBackStack();
            mPlayer.release();
            mProvider = null;
            mPlayer = null;
            mFragment = null;
            AppHomeFragment fragment = (AppHomeFragment)fragmentManager.findFragmentById(R.id.activity_main);
            fragment.Show();
        }
    }

    //A listener for handling OnPlay event raised by the fragment that displays the play button.
    private class PlayListener implements OnPlayListener
    {
        @Override
        public void onPlay(@NonNull  String videoId) {
            mVideoId = videoId;
            setupVideoView();
        }
    }
}