package com.outerspace.luis_viruena_baking2.exo;

import com.google.android.exoplayer2.Player;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.outerspace.luis_viruena_baking2.R;

import java.net.MalformedURLException;
import java.net.URL;

public class BPlayerView extends PlayerView implements LifecycleObserver, Player.EventListener {

    private final static String B_PLAYER_AGENT = "bPlayerView";

    private SimpleExoPlayer simpleExoPlayer;
    private ProgressBar progress;
    private BPlayerViewModel viewModel;
    private String videoUrl;
    private MediaSource currentMediaSource;

    // constructors
    public BPlayerView(Context context) { super(context); addProgressBar(context); }

    public BPlayerView(Context context, AttributeSet attrs) { super(context, attrs); addProgressBar(context); }

    public BPlayerView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); addProgressBar(context); }

    /**
     *
     */
    @BindingAdapter("viewModel")
    public static void setViewModel(BPlayerView bPlayerView, BPlayerViewModel viewModel) {
        bPlayerView.viewModel = viewModel;
    }

    @BindingAdapter("videoUrl")
    public static void setVideoUrl(BPlayerView bPlayerView, String videoUrl) {
        if(videoUrl != null && !videoUrl.isEmpty()) {
            bPlayerView.viewModel.getMutableVideoUrl().setValue(videoUrl);
            bPlayerView.viewModel.getMutablePlaybackPosition().setValue(bPlayerView.viewModel.getMutablePlaybackPosition().getValue());
        } else{
            if(bPlayerView.simpleExoPlayer != null) {
                bPlayerView.simpleExoPlayer.setPlayWhenReady(false);
                bPlayerView.simpleExoPlayer.release();
                bPlayerView.simpleExoPlayer = null;
            }
            bPlayerView.setVisibility(View.GONE);
        }
    }

    private void addProgressBar(Context context) {
        progress = new ProgressBar(this.getContext());
        final LayoutParams params = (LayoutParams) this.getOverlayFrameLayout().getLayoutParams();
        float progressBarSize = context.getResources().getDimension(R.dimen.progress_bar_size);
        params.height = (int) (progressBarSize);
        params.width = (int) (progressBarSize);
        params.gravity = Gravity.CENTER;
        progress.setLayoutParams(params);
        progress.setForegroundGravity(Gravity.CENTER);
        addView(progress);
    }

    private void releasePlayer() {
        simpleExoPlayer.release();
        simpleExoPlayer = null;
    }

    private void initializePlayer() {
        if (simpleExoPlayer == null) {
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(),
                    new DefaultRenderersFactory(getContext()),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());
            setPlayer(simpleExoPlayer);
            simpleExoPlayer.addListener(this);
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(), B_PLAYER_AGENT);
        return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
    }

    private Uri getUri(String url) {
        try {
            new URL(url);
            return Uri.parse(url);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public void playVideo(String videoUrl) {
        if(videoUrl==null || videoUrl.isEmpty())  // null or empty url happens on startup
            return;

        initializePlayer();

        if(this.videoUrl == null || !this.videoUrl.equals(videoUrl)) {   // different video, create new MediaSource
            Uri uri = getUri(videoUrl);
            if (uri != null) {
                currentMediaSource = buildMediaSource(uri);
            } else {
                return;         // invalid url
            }
        }

        simpleExoPlayer.prepare(currentMediaSource, false, false);
        simpleExoPlayer.seekTo(0, 0L);
        simpleExoPlayer.setPlayWhenReady(true);
        this.videoUrl = videoUrl;
    }

    public void seekTo(long playbackPosition) {
        if (simpleExoPlayer != null && currentMediaSource != null && playbackPosition != simpleExoPlayer.getCurrentPosition()) {
            int currentWindow = 0;
            simpleExoPlayer.seekTo(currentWindow, playbackPosition);
            simpleExoPlayer.prepare(currentMediaSource, false, false);
        }
    }

    // LifecycleObserver
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void start() {
        initializePlayer();
    }

    @SuppressLint("ObsoleteSdkInt")
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void resume() {
        if(simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(true);
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private void pause() {
        if(simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
            viewModel.getMutablePlaybackPosition().setValue(simpleExoPlayer.getCurrentPosition());
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void stop() {
        releasePlayer();
    }

    // Event Listener

    @Override
    public void onLoadingChanged(boolean isLoading) {
        progress.setVisibility(isLoading ? VISIBLE : GONE);
    }

    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        if (simpleExoPlayer.getPlaybackState() == ExoPlayer.STATE_READY) {
            progress.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
        }
    }
}