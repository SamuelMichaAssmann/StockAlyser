package com.wasge.stockalyser;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wasge.stockalyser.ui.StockFragment;
import com.wasge.stockalyser.ui.search.SearchFragment;
import com.wasge.stockalyser.util.ApiManager;
import com.wasge.stockalyser.util.FragmentReciever;
import com.wasge.stockalyser.util.FragmentSender;

import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FragmentSender {

    AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onStart() {
        super.onStart();
        StockFragment fragment = new StockFragment();
        SearchFragment fragment1 = new SearchFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.nav_host_fragment, fragment,"fragment_stock").commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment1,"fragment_search").commit();
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.navigation_home);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_watchlist)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        SharedPreferences apikey = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if (apikey != null){
            String s = apikey.getString("apikey", null);

            Log.d("test", "" + s);
        }else
            Log.d("test", "apikey is null");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem itemSearch = menu.findItem(R.id.search_icon);
        final SearchView searchView = (SearchView) itemSearch.getActionView();
        searchView.setQueryHint("Search...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                sendToFragment("fragment_search",  new Object[]{newText});
                return true;
            }
        });


        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        //Navigation on Settings
        if (item.getItemId() == R.id.navigation_settings) {
            navController.navigate(R.id.navigation_settings);
        }
        if (item.getItemId() == R.id.search_icon) {
            navController.navigate(R.id.navigation_search);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendToFragment(String fragment, Object[] input) {
        if (input[0] instanceof Boolean) {

        }else {
            FragmentReciever f = (FragmentReciever) getSupportFragmentManager().findFragmentByTag(fragment);
            f.recieveData(input);
        }
    }
}