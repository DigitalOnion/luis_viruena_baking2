package com.outerspace.luis_viruena_baking2.exo;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.outerspace.luis_viruena_baking2.Model;

public class BPlayerViewModel extends ViewModel {
    private MutableLiveData<String> mutableVideoUrl = new MutableLiveData<>("");
    public MutableLiveData<String> getMutableVideoUrl() { return mutableVideoUrl; }
    public void setMutableVideoUrl(MutableLiveData<String> mutableVideoUrl) { this.mutableVideoUrl = mutableVideoUrl; }

    private MutableLiveData<Boolean> mutablePlayWhenReady = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> getMutablePlayWhenReady() { return mutablePlayWhenReady; }

    private MutableLiveData<Long> mutablePlaybackPosition = new MutableLiveData<>(0L);
    public MutableLiveData<Long> getMutablePlaybackPosition() { return mutablePlaybackPosition; }

    public void onClickPlayVideo(View view) {
        String nextVideoUrl = Model.getNextVideoUrl();
        mutableVideoUrl.setValue(nextVideoUrl);
    }
}
