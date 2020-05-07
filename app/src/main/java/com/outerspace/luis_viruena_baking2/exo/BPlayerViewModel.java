package com.outerspace.luis_viruena_baking2.exo;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BPlayerViewModel extends ViewModel {
    private MutableLiveData<String> mutableVideoUrl = new MutableLiveData<>("");
    public MutableLiveData<String> getMutableVideoUrl() { return mutableVideoUrl; }

    private MutableLiveData<Long> mutablePlaybackPosition = new MutableLiveData<>(0L);
    public MutableLiveData<Long> getMutablePlaybackPosition() { return mutablePlaybackPosition; }
}
