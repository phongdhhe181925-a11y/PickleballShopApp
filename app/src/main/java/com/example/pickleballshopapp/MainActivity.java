package com.example.pickleballshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View toolbarLogo = findViewById(R.id.toolbar_logo);
        toolbarLogo.setOnClickListener(v -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (!(currentFragment instanceof HomeFragment)) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
            }
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        // Handle intent on initial creation
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Handle intent when MainActivity is already running
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent == null) return;

        if (intent.getBooleanExtra("OPEN_DRAWER", false)) {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        }

        String navigateTo = intent.getStringExtra("NAVIGATE_TO");
        if (navigateTo != null) {
            switch (navigateTo) {
                case "SEARCH":
                    openFragment(new SearchFragment());
                    break;
                case "CART":
                    openFragment(new CartFragment());
                    break;
            }
        }
        // Clear the extras so they don't trigger again on configuration change
        intent.removeExtra("OPEN_DRAWER");
        intent.removeExtra("NAVIGATE_TO");
    }

    private void openFragment(Fragment fragment) {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment.getClass().isInstance(current)) return; // Don't open if already there

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_right)
                .add(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            openFragment(new SearchFragment());
            return true;
        } else if (id == R.id.action_cart) {
            openFragment(new CartFragment());
            return true;
        } else if (id == R.id.action_account) {
            SessionManager sessionManager = new SessionManager(this);
            Intent intent = new Intent(this, sessionManager.isLoggedIn() ? ProfileActivity.class : LoginActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_paddles) {
            // Handle paddle navigation
        } else if (id == R.id.nav_shoes) {
            // Handle shoes navigation
        } else if (id == R.id.nav_accessories) {
            // Handle accessories navigation
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
