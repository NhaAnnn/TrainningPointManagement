package com.example.qldrl.Account.FagmentCreate;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FagAdapter extends FragmentStateAdapter {


    public FagAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new uploadListBoard();
            case 1:
                return new uploadListTeacher();
            case 2:
                return new uploadListStudent();
            default:
                return new uploadListBoard();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
