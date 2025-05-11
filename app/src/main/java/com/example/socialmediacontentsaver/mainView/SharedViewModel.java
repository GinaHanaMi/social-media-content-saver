package com.example.socialmediacontentsaver.mainView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private final MutableLiveData<Boolean> refreshFeed = new MutableLiveData<>();

    // For search feature
    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }

    // For triggering feed refresh
    public void triggerRefreshFeed() {
        refreshFeed.setValue(true);
    }

    public LiveData<Boolean> getRefreshFeed() {
        return refreshFeed;
    }
}
