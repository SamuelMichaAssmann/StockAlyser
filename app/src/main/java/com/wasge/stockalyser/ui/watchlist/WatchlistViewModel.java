package com.wasge.stockalyser.ui.watchlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WatchlistViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public WatchlistViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is watchlist fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}