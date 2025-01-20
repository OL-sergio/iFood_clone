package udemy.java.desenvolvimento.android.completo.ifood_clone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import udemy.java.desenvolvimento.android.completo.ifood_clone.R;
import udemy.java.desenvolvimento.android.completo.ifood_clone.databinding.ActivityClientBinding;
import udemy.java.desenvolvimento.android.completo.ifood_clone.helper.UserFirebase;


public class ClientActivity extends AppCompatActivity {

    private ActivityClientBinding binding;

    private UserFirebase userFirebase;

    private  SearchView searchView;
    private boolean isSearchViewExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityClientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();

        components();

        animateSearchViewExpand(isSearchViewExpanded);

        // Set up the search view listener
       /* searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                animateSearchView(hasFocus);
                animateSearchViewExpand(hasFocus);
            }
        });*/

        // Handle search query submission
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle the search query submission
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle text change in the search view
                return true;
            }
        });

    }

    private void animateSearchView(boolean expand) {
        Animation animation;
        if (expand) {
            animation = new AlphaAnimation(0f, 1f);
            searchView.setIconified(false); // Expand the search view
        } else {
            animation = new AlphaAnimation(1f, 0f);
            searchView.setIconified(true); // Collapse the search view

        }

        animation.setDuration(300);
        animation.setFillAfter(true);
        searchView.startAnimation(animation);

    }

    private void animateSearchViewExpand(boolean expand) {
        float scaleX = expand ? 1.0f : 0.0f;
        float scaleY = expand ? 1.0f : 0.0f;

        searchView.animate()
                .scaleX(scaleX)
                .scaleY(scaleY)
                .setDuration(300)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        searchView.setIconified(!expand); // Collapse or expand the search view
                    }
                })
                .start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_client, menu);

        MenuItem menuItem = menu.findItem(R.id.menuSearch);
        MenuItem settingsItem = menu.findItem(R.id.menuSettingsUser);
        MenuItem logoutItem = menu.findItem(R.id.menuLogout);

        if (isSearchViewExpanded == true) {
            searchView.setVisibility(View.VISIBLE);
            menuItem.setIcon(R.drawable.ic_close_24);
            settingsItem.setVisible(false);
            logoutItem.setVisible(false);


        } else if (isSearchViewExpanded == false) {
            searchView.setVisibility(View.GONE);
            menuItem.setIcon(R.drawable.ic_search_24);

        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menuSearch) {
            startSearchView();
            return true;

        }

        if (id == R.id.menuSettingsUser) {
            settingUser();
            return true;
        }

        if (id == R.id.menuLogout) {
            logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startSearchView() {

        if (isSearchViewExpanded) {
            animateSearchView(false);
            isSearchViewExpanded = false;
            animateSearchViewExpand(isSearchViewExpanded);
            invalidateOptionsMenu();

        } else if (!isSearchViewExpanded) {
            animateSearchView(true);
            isSearchViewExpanded = true;
            animateSearchViewExpand(isSearchViewExpanded);
            invalidateOptionsMenu();

        }
    }

    private void settingUser() {
        startActivity(new Intent(this, SettingUserActivity.class));
    }

    private void logoutUser() {

        userFirebase.logoutUser();
        startActivity(new Intent(this, AuthenticationActivity.class));
        finish();

    }

    private void  setupToolbar(){
        Toolbar toolbar = binding.toolbar.toolbarClient;
        toolbar.setTitle("Ifood - Client");
        setSupportActionBar(toolbar);
    }

    private void components() {

        userFirebase = new UserFirebase();
        searchView = binding.toolbar.searchView;// Collapse the search view

    }
}