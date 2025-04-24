package com.example.socialmediacontentsaver.homeView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class VPAdapter extends FragmentStateAdapter {
    public VPAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new fragmentOne();
            case 1:
                return new fragmentTwo();
            case 2:
                return new fragmentThree();
            default:
                return new fragmentOne();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
