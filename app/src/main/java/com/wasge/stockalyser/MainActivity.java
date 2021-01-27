package com.wasge.stockalyser;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wasge.stockalyser.ui.SettingsFragment;
import com.wasge.stockalyser.ui.StockFragment;
import com.wasge.stockalyser.ui.search.SearchFragment;
import com.wasge.stockalyser.util.DatabaseManager;

public class MainActivity extends AppCompatActivity {

    AppBarConfiguration mAppBarConfiguration;
    DatabaseManager databaseManager;
    SearchFragment searchFragment;
    StockFragment stockFragment;
    MenuItem bookmark;

    public void subscribeToMain(int id, Fragment f) {
        if (R.id.navigation_stock == id && f instanceof StockFragment) {
            stockFragment = (StockFragment) f;
        } else if (R.id.navigation_search == id && f instanceof SearchFragment) {
            searchFragment = (SearchFragment) f;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.navigation_home);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseManager = new DatabaseManager(this);
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

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem itemSearch = menu.findItem(R.id.search_icon);
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        final SearchView searchView = (SearchView) itemSearch.getActionView();
        searchView.setQueryHint("Search...");
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.navigation_search);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                sendSearchRequest(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                sendSearchRequest(newText);
                return true;
            }
        });

        this.bookmark = menu.findItem(R.id.bookmark);
        bookmark.setVisible(false);
        bookmark.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                toggleOpenStockWatchlist();
                return false;
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


    private void toggleOpenStockWatchlist(){
        stockFragment.toggleCurrentToWatchlist();
    }

    public void sendSearchRequest(String query) {
        if (searchFragment != null)
            searchFragment.searchFor(query);
    }

    public void sendToStockFragment(Object[] data) {
        if (stockFragment != null)
            stockFragment.recieveData(data);
    }

    public void setBookmarkVisibility(boolean visibility){
        bookmark.setVisible(visibility);
    }

    public void setBookmarkStyle(@DrawableRes int id){
        bookmark.setIcon(id);
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}