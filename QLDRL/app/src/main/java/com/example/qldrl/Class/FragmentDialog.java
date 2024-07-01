package com.example.qldrl.Class;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentDialog extends FragmentStateAdapter {

    public FragmentDialog(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new DialogCreateFileClass();
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
