package com.darrengansberg.youtubeplayer;

/*=========================AppHomeFragment.java ===============================
Written by: Darren Gansberg
Copyright: 2021, All rights Reserved.
Description: The AppHomeFragment class is the fragment responsible for creating and
displaying an EditText box and play button which allow the user to enter the url
of a YouTube video and watch/play the video.
 */

import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

/*This class is a fragment for the initial part of the video app UI.
It is used for presentation of the play button and entry of the URL.
 */
public class AppHomeFragment extends Fragment {

    private OnPlayListener playListener;

    public AppHomeFragment() { /* Required empty public constructor*/ }

    public static AppHomeFragment newInstance(){ return new AppHomeFragment(); }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button playButton = view.findViewById(R.id.play_button);
        playButton.setOnClickListener(this::onPlayClicked);
    }

    public void onPlayClicked(View v)
    {
        String videoId = getVideoId();
        if ((videoId == null) || (videoId.equals("")))
        {
            Toast.makeText(v.getContext(), "Unrecognised URL", Toast.LENGTH_SHORT).show();
            return;
        }
        Hide();
        playListener.onPlay(videoId);
    }

    public void Hide()
    {
        hideURLEditText();
        hidePlayButton();
    }

    public void Show()
    {
        showURLEditText();
        showPlayButton();
    }

    protected void hidePlayButton()
    {
        Button playButton = getView().findViewById(R.id.play_button);
        playButton.setVisibility(View.GONE);
    }

    protected void hideURLEditText()
    {
        EditText videoURLInput = getView().findViewById(R.id.video_url_input);
        videoURLInput.setVisibility(View.GONE);
    }

    protected void showPlayButton()
    {
        Button playButton = getView().findViewById(R.id.play_button);
        playButton.setVisibility(View.VISIBLE);
    }

    protected void showURLEditText()
    {
        EditText videoURLInput = getView().findViewById(R.id.video_url_input);
        videoURLInput.setVisibility(View.VISIBLE);
    }

    public void AddOnPlayListener(@NonNull OnPlayListener playListener)
    {
        this.playListener = playListener;
    }

    public String getVideoId()
    {
        String videoId = null;
        try
        {
            EditText urlInput = getView().findViewById(R.id.video_url_input);
            String inputValue = urlInput.getText().toString();
            Uri uri = Uri.parse(inputValue.trim());
            try
            {
                //Try to identify id of video based upon youtube url.
                //Youtube URL'S currently have at least two different forms

                //Form 1: https://www.youtube.com/watch?v=xapksKzqIw4
                //(the video id is the value of the v query parameters)

                //Form 2: https://youtu.be/xapksKzqIw4
                //(the video id is the first component of the path.

                videoId = uri.getQueryParameter("v"); //attempt to identify video id from assuming form 1.
                if ((videoId == null) || (videoId == "")) //if we couldn't retrieve video id, then try assuming form 2.
                {
                    List<String> pathSegments = uri.getPathSegments();
                    if ((pathSegments != null) && (pathSegments.size() > 0))
                    {
                        videoId = pathSegments.get(0);
                    }
                }
                //if we haven't found the video id at this point, give up and just display error
                //message of unrecognised url.
                if ((videoId == null) || (videoId.equals("")))
                {
                    Toast.makeText(getView().getContext(), "Unrecognised Video URL", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {

                Toast.makeText(getView().getContext(), "Unrecognised Video URL", Toast.LENGTH_SHORT).show();

            }
        }
        catch (Exception ex)
        {
            Toast.makeText(getView().getContext(), "Unrecognised Video URL", Toast.LENGTH_SHORT).show();
        }

        return videoId;
    }

}