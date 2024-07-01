package com.example.qldrl.Account;

import com.example.qldrl.General.Account;

import java.util.List;

public interface CreateManyAccountCallback {
    void onManyAccountCreated(List<Account> account);
}