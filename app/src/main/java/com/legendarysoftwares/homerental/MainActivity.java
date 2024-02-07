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
    //BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadfrag(new Home(),0);
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
                    loadfrag(new Search(),1);
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
                    /*Intent intent=new Intent(MainActivity.this,UserProfileActivity.class);
                    startActivity(intent);*/
                }
                return true;
            }
        });

    }

    /*private boolean isLoggedIn() {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            return auth.getCurrentUser() != null;
    }*/


    public void loadfrag(Fragment fragment, int flag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (flag==0)
            ft.add(R.id.container,fragment);
        else
            ft.replace(R.id.container,fragment);
        ft.commit();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    /*private void createShowLoginBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.ask_for_login_bs,null,false);
        Button loginBtn = view.findViewById(R.id.bottomSheet_login_button);
        Button registerBtn = view.findViewById(R.id.bottomSheet_register_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(view);

    }*/


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want to exit ?");
        builder.setTitle("Alert !");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            finish();});
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();});

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}