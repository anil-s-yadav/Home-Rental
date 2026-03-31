package com.legendarysoftwares.homerental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.legendarysoftwares.homerental.fragments.Add;
import com.legendarysoftwares.homerental.fragments.Home;
import com.legendarysoftwares.homerental.fragments.Search;
import com.legendarysoftwares.homerental.fragments.Profile;
import com.legendarysoftwares.homerental.fragments.Saved;


public class MainActivity extends AppCompatActivity {
    BottomNavigationView navBar;

    public String searchQuery = "";

    public void openSearchWithQuery(String query) {
        this.searchQuery = query;
        if (navBar != null) {
            navBar.setSelectedItemId(R.id.search_tab);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            loadfrag(new Home(), 0);
        }
        
        LoginBottomSheetHelper loginBottomSheetHelper = new LoginBottomSheetHelper(this);

        navBar = findViewById(R.id.navBar);
        navBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = (item.getItemId());
                if (id == R.id.home_tab){
                    loadfrag(new Home(),1);
                }
                else if (id==R.id.search_tab) {
                    Search searchFrag = new Search();
                    if (!searchQuery.isEmpty()) {
                        Bundle args = new Bundle();
                        args.putString("query", searchQuery);
                        searchFrag.setArguments(args);
                        searchQuery = ""; // reset
                    }
                    loadfrag(searchFrag,1);
                }
                else if (id==R.id.add_tab) {
                    if (!loginBottomSheetHelper.isLoggedIn()) {
                        loginBottomSheetHelper.showLoginBottomSheet();
                    } else {
                        loadfrag(new Add(),1);
                    }
                }
                else if (id==R.id.save_tab) {
                    loadfrag(new Saved(),1);
                }
                else if (id==R.id.profile_tab) {
                    loadfrag(new Profile(),1);
                }
                return true;
            }
        });
    }


    public void loadfrag(Fragment fragment, int flag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (flag==0)
            ft.add(R.id.container,fragment);
        else
            ft.replace(R.id.container,fragment);
        ft.commit();
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            showExitDialog();
        }
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want to exit ?");
        builder.setTitle("Alert !");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", (dialog, which) -> finish());
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}