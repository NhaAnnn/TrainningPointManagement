package com.example.qldrl.Account;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
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
    private ImageView imgBackListAcc;

    public static Dialog currentDialog;
    FragmentActivity fragmentActivity = (FragmentActivity) this;
    private boolean isSearchViewFocused = false, isBackPressed = false;
    @Override
    public void onBackPressed() {
        // Kiểm tra xem SearchView có focus không
        if (isSearchViewFocused) {
            // Nếu có, ẩn bàn phím ảo
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchAcc.getWindowToken(), 0);

            // Và bỏ focus khỏi SearchView
            searchAcc.clearFocus();
            isSearchViewFocused = false;
        } else {
            // Nếu không, xử lý như bình thường

            // Nếu đã nhấn nút back 2 lần, thì mới gọi super.onBackPressed()
            if (isBackPressed) {
                super.onBackPressed();
            } else {
                isBackPressed = true;
                finish();
            }

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_acc);

        layoutAcc = findViewById(R.id.layoutAcc);
        layoutManyAcc = findViewById(R.id.layoutManyAcc);
        recycAcc = findViewById(R.id.recycAcc);
        searchAcc = findViewById(R.id.searchAcc);
        imgBackListAcc = findViewById(R.id.imgBackListAcc);
        getAllAccounts();

        imgBackListAcc.setOnClickListener(v -> onBackPressed());

        searchAcc();


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

    private void searchAcc() {
        searchAcc.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapterAccount.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterAccount.getFilter().filter(newText);
                return false;
            }


        });

        searchAcc.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
            isSearchViewFocused = hasFocus;
            isBackPressed = false;
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
      //  Toast.makeText(listAcc.this, "hekko size"+ newAccounts.size(), Toast.LENGTH_LONG).show();
        adapterAccount.notifyDataSetChanged();

    }
}