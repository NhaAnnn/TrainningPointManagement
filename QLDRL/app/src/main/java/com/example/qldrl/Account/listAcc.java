package com.example.qldrl.Account;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.qldrl.Account.FagmentCreate.FagAdapter;
import com.example.qldrl.Account.FagmentCreate.FragAdapterCreateAcc;
import com.example.qldrl.General.Account;
import com.example.qldrl.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class listAcc extends AppCompatActivity implements CreateManyAccountCallback {
    private RecyclerView recycAcc;
    private LinearLayout layoutManyAcc,layoutAcc;
    private SearchView searchAcc;
    private AdapterAccount adapterAccount;
    List<Account> accountListsss = new ArrayList<>();


    public static Dialog currentDialog;
    FragmentActivity fragmentActivity = (FragmentActivity) this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_acc);

        layoutAcc = findViewById(R.id.layoutAcc);
        layoutManyAcc = findViewById(R.id.layoutManyAcc);
        recycAcc = findViewById(R.id.recycAcc);
        searchAcc = findViewById(R.id.searchAcc);

        getAllAccounts();
//        List<Account> accountList = new ArrayList<>();
//        accountList.add(new Account("1", "do van suong","123","heelos","01/06/2003" ,"heelo"));
//        Toast.makeText(this, accountList.get(0).getTkHoTen(), Toast.LENGTH_LONG).show();
//        adapterAccount = new AdapterAccount(listAcc.this, accountList);
//
//        recycAcc.setAdapter(adapterAccount);
//        recycAcc.setLayoutManager(new GridLayoutManager(listAcc.this, 1));

        layoutManyAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDinalogCreat(Gravity.CENTER);
            }
        });

        layoutAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDinalogCreatAcc(Gravity.CENTER);
            }
        });

    }

    private void openDinalogCreat(int gravity) {
        currentDialog = new Dialog(listAcc.this);
        currentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        currentDialog.setContentView(R.layout.layout_upload_listacc);

        Window window = currentDialog.getWindow();
        if(window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = gravity;
        window.setAttributes(windowAttribute);

        currentDialog.setCancelable(true);

        TabLayout tabLayout = currentDialog.findViewById(R.id.tabLayout);
        ViewPager2 viewPager2 = currentDialog.findViewById(R.id.viewPager);
        FagAdapter fagAdapter;

        fagAdapter = new FagAdapter(this);
        viewPager2.setAdapter(fagAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Ban giám hiệu");
                        break;
                    case 1:
                        tab.setText("Giáo viên");
                        break;
                    case 2:
                        tab.setText("Học sinh");
                        break;
                }
            }
        }).attach();
        currentDialog.show();
    }


    private void openDinalogCreatAcc(int gravity) {
        currentDialog = new Dialog(this);
        currentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        currentDialog.setContentView(R.layout.layout_upload_listacc);

        Window window = currentDialog.getWindow();
        if(window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = gravity;
        window.setAttributes(windowAttribute);

        currentDialog.setCancelable(true);
        TabLayout tabLayout = currentDialog.findViewById(R.id.tabLayout);
        ViewPager2 viewPager2 = currentDialog.findViewById(R.id.viewPager);
        FragAdapterCreateAcc fragAdapterCreateAcc;

        fragAdapterCreateAcc = new FragAdapterCreateAcc(this);
        viewPager2.setAdapter(fragAdapterCreateAcc);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Ban giám hiệu");
                        break;
                    case 1:
                        tab.setText("Giáo viên");
                        break;
                    case 2:
                        tab.setText("Học sinh");
                        break;
                }
            }
        }).attach();
        currentDialog.show();
    }

    private void getAllAccounts() {
        List<Account> accountList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference accountRef = db.collection("taiKhoan");

        accountRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {

                    String tkID = documentSnapshot.getString("TK_id");
                    String tkTenTK = documentSnapshot.getString("TK_TenTaiKhoan");
                    String tkNgaySinh = documentSnapshot.getString("TK_NgaySinh");
                    String tkMatKhau = documentSnapshot.getString("TK_MatKhau");
                    String tkHoTen = documentSnapshot.getString("TK_HoTen");
                    String tkChucVu = documentSnapshot.getString("TK_ChucVu");



                    Account account = new Account(tkID,tkTenTK,tkNgaySinh,tkMatKhau, tkHoTen, tkChucVu);
                    accountList.add(account);
                }
                // Log.d("helllo" ,classRomList.size() + "");


                updateRecyclerView(accountList);
//                adapterAccount = new AdapterAccount(listAcc.this, accountListsss); //truyen vao tuy tung list
//                recycAcc.setAdapter(adapterAccount);
//
//                recycAcc.setLayoutManager(new GridLayoutManager(listAcc.this, 1));
            } else {
                //Toast.makeText(getApplicationContext(), "Error retrieving accounts", Toast.LENGTH_SHORT).show();
            }
        });
        //  Log.d("helllo" ,classRomList.size() + "");


    }
    private void updateRecyclerView(List<Account> accountLists) {
        accountListsss.addAll(accountLists);
        adapterAccount = new AdapterAccount(listAcc.this, accountListsss);
        recycAcc.setAdapter(adapterAccount);
        recycAcc.setLayoutManager(new GridLayoutManager(listAcc.this, 1));
    }


    @Override
    public void onManyAccountCreated(List<Account> newAccounts) {
        // Xử lý tài khoản mới được tạo ở đây
        // Ví dụ: lưu trữ tài khoản, cập nhật giao diện, v.v.
        accountListsss.addAll(newAccounts);
        Toast.makeText(listAcc.this, "hekko size"+ newAccounts.size(), Toast.LENGTH_LONG).show();
        adapterAccount.notifyDataSetChanged();

    }
}