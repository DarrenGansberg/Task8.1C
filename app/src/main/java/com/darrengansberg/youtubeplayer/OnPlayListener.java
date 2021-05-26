package com.darrengansberg.youtubeplayer;
/*=========================OnPlayListener.java ===============================
Written by: Darren Gansberg
Copyright: 2021, All rights Reserved.
Description: The AppHomeFragment class is the fragment responsible for creating and
displaying an EditText box and play button which allow the user to enter the url
of a YouTube video and watch/play the video.
 */
//Defines an interface for an OnPlayListener that handles raising of an
//onPlay event.

public interface OnPlayListener {
    void onPlay(String videoId);
}