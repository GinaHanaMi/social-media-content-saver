package com.example.socialmediacontentsaver.mainView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.socialmediacontentsaver.mainView.feedActivityClasses.FragmentOne;
import com.example.socialmediacontentsaver.mainView.foldersActivityClasses.FragmentTwo;
import com.example.socialmediacontentsaver.mainView.settingsActivityClasses.fragmentThree;

public class VPAdapter extends FragmentStateAdapter {
    public VPAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FragmentOne();
            case 1:
                return new FragmentTwo();
            case 2:
                return new fragmentThree();
            default:
                return new FragmentOne();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
