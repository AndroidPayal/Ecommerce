package payal.cluebix.www.ecommerce;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import net.yazeed44.imagepicker.model.ImageEntry;
import net.yazeed44.imagepicker.util.Picker;

import java.util.ArrayList;
import java.util.HashMap;

import payal.cluebix.www.ecommerce.Datas.Base_url;
import payal.cluebix.www.ecommerce.Handlers.SessionManager;

public class CenterActivity extends AppCompatActivity {

    private Fragment fragment;
    private FragmentManager fragmentManager;
    private DrawerLayout drawerLayout;
    Toolbar toolbar, search_tool2;
    FloatingActionButton floatb;

    private ArrayList<ImageEntry> mSelectedImages = new ArrayList<>();
    SessionManager session;
    String Uid;
    String Uname, Umail, Udate1, Udate2, Umob;
    BottomNavigationView bottomNavigation;
    Bundle extras;
    FragmentTransaction transaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);


        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        Uname = user.get(SessionManager.KEY_NAME);
        Uid = user.get(SessionManager.KEY_ID);
        Umail = user.get(SessionManager.KEY_email);
        Udate1 = user.get(SessionManager.KEY_createDate);
        Udate2 = user.get(SessionManager.KEY_LastModified);
        Umob = user.get(SessionManager.KEY_mobile);
        Log.d("sessionscreen", "name_userId=" + Uid + "\n_user_name=" + Uname + "\nemail=" + Umail
                + "\ndate1=" + Udate1 + "\ndate2=" + Udate2 + "\nmobile=" + Umob);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        floatb = (FloatingActionButton) findViewById(R.id.float_newProduct);
        search_tool2 = (Toolbar) findViewById(R.id.toolbar_search2);
        setSupportActionBar(toolbar);


        initNavigationDrawer();

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        // bottomNavigation.inflateMenu(R.menu.bottom_navigation);
        fragmentManager = getSupportFragmentManager();
        Log.d("ononon","inside oncreate");

    //    Toast.makeText(this, "dashboard act called", Toast.LENGTH_SHORT).show();





/*
        if (extras.getBoolean("cartTransition")) {
            Log.d("center_screen", "inflating cart");
            //  if (extras.getBoolean("cartTransition")) {

            Fragment newFragment = new CartFragment();
            transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.main_container, newFragment);
            transaction.addToBackStack(null);

            search_tool2.setVisibility(View.GONE);
            transaction.commit();


            bottomNavigation.setSelectedItemId(R.id.bottom_nav_cart);

        }*/





        /*

        extras = getIntent().getStringExtra("cartTransition");
Log.d("center_screen",extras);

        if(extras.equalsIgnoreCase("cart"))
        {
           *//* Log.d("center_screen","inside extras");
            if(extras.getBoolean("cartTransition"))
            {*//*

                Fragment newFragment = new CartFragment();
                 transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_container, newFragment);
                transaction.addToBackStack(null);
                search_tool2.setVisibility(View.GONE);
                transaction.commit();
                bottomNavigation.setSelectedItemId(R.id.bottom_nav_cart);

            //}
        }
        else{
            Log.d("center_screen","extra null");
            fragment = new DashboardFragment();
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_container, fragment).commit();
        }
 */
        fragment = new DashboardFragment();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, fragment).commit();


        floatb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CenterActivity.this, Add_New_product.class);
                startActivity(i);
              /*  new Picker.Builder(getApplicationContext(),
                        new Picker.PickListener() {
                            @Override
                            public void onPickedSuccessfully(ArrayList<ImageEntry> images) {
                                mSelectedImages = images;
                               // setupImageSamples();
                                Log.d("CenterActivity_screen", "Picked images  " + images.toString());
                            }

                            @Override
                            public void onCancel() {
                                Log.i("CenterActivity_screen", "User canceled picker activity");
                                Toast.makeText(getApplicationContext(), "User canceld picker activtiy", Toast.LENGTH_SHORT).show();

                            }
                        }, R.style.MIP_theme)
                        .setPickMode(Picker.PickMode.MULTIPLE_IMAGES)
                        .setLimit(Base_url.numberOfImagesToSelect)
                        .build()
                        .startActivity();*/
            }
        });

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.bottom_nav_home:
                        search_tool2.setVisibility(View.VISIBLE);
                        fragment = new DashboardFragment();

                        break;
                    case R.id.bottom_nav_cart:
                        search_tool2.setVisibility(View.GONE);
                        fragment = new CartFragment();
                        break;
                }
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, fragment).commit();
                return true;
            }
        });

    }


    private void initNavigationDrawer() {

        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                int id = item.getItemId();

                switch (id) {
                    case R.id.home:
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.Profile:
                        Intent i = new Intent(CenterActivity.this, Update_profile.class);
                        startActivity(i);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.addNew:
                        i = new Intent(CenterActivity.this, My_products.class);
                        startActivity(i);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.cart:
                        Fragment newFragment = new CartFragment();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                        transaction.replace(R.id.main_container, newFragment);
                        transaction.addToBackStack(null);

                        transaction.commit();
                        search_tool2.setVisibility(View.GONE);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.logOut:
                        Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_SHORT).show();
                        i = new Intent(CenterActivity.this, Login.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);
                        finish();
                        session.logoutUser();
                        break;
                    case R.id.quote:
                        i = new Intent(CenterActivity.this, Quotation_list.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        drawerLayout.closeDrawers();
                        break;
                }

                return true;
            }
        });


        View header = navigationView.getHeaderView(0);
        TextView tv_email = (TextView) header.findViewById(R.id.navigation_email);
        tv_email.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Lato-Semibold.ttf"));
        tv_email.setText("WELCOME " + Uname);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        finishAffinity();
    }

/*

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //extras="";
        Log.d("center_screen", "act postresumed");
        extras = getIntent().getExtras();
        Log.d("center_screen", "extras");

        if (extras.getBoolean("cartTransition")) {
            Log.d("center_screen", "inflating cart");
          //  if (extras.getBoolean("cartTransition")) {

                Fragment newFragment = new CartFragment();
                transaction = getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.main_container, newFragment);
                transaction.addToBackStack(null);

                search_tool2.setVisibility(View.GONE);
                transaction.commit();


                bottomNavigation.setSelectedItemId(R.id.bottom_nav_cart);

                //}
            } else {
                Log.d("center_screen", "inflating dash");
                fragment = new DashboardFragment();
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, fragment).commit();
            }

        }


*/


    }


