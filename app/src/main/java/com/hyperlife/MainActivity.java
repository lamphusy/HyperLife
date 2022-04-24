package com.hyperlife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.hyperlife.fragment.UserProfileFragment;
import com.hyperlife.fragment.WorkoutFragment;


public class MainActivity extends AppCompatActivity {
    public BottomNavigationView btmNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        getSupportActionBar().hide();


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,HomeFragment.class,null);
        fragmentTransaction.commit();


        btmNav = findViewById(R.id.bottom_nav);
        btmNav.setBackground(null);
        btmNav.setItemIconTintList(null);
        btmNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    handleChangeFragment(item.getItemId());
                    return true;
                }
            });

    }

//    public void handleBtmNavClick(int id) {
//            btmNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//                @Override
//                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                    handleChangeFragment(id);
//                    return true;
//                }
//            });
//    }

    public void handleChangeFragment(int id) {
        if(id == R.id.nav_home){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,HomeFragment.class,null);
            fragmentTransaction.commit();
        }
        if(id == R.id.nav_workout){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, WorkoutFragment.class,null);
            fragmentTransaction.commit();
        }
        if(id == R.id.nav_user){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, UserProfileFragment.class,null);
            fragmentTransaction.commit();
        }
        if(id == R.id.nav_meal){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,MealFragment.class,null);
            fragmentTransaction.commit();
        }
    }


}