package com.example.qldrl.Account.FagmentCreate;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragAdapterCreateAcc extends FragmentStateAdapter {


    public FragAdapterCreateAcc(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new board_school();
            case 1:
                return new teacher();
            case 2:
                return new student();
            default:
                return new board_school();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
