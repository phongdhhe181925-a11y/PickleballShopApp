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
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private DrawerAdapter drawerAdapter;
    private final List<DrawerAdapter.Group> drawerGroups = new ArrayList<>();

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

        androidx.recyclerview.widget.RecyclerView drawerRecycler = findViewById(R.id.drawerRecyclerView);
        drawerAdapter = new DrawerAdapter(this, new DrawerAdapter.Callback() {
            @Override
            public void onOpenAll(String category) {
                Intent i = new Intent(MainActivity.this, ProductListActivity.class);
                i.putExtra("category", category);
                startActivity(i);
                drawerLayout.closeDrawer(GravityCompat.START);
            }

            @Override
            public void onOpenBrand(String category, int brandId, String brandName) {
                Intent i = new Intent(MainActivity.this, ProductListActivity.class);
                i.putExtra("category", category);
                i.putExtra("brandId", brandId);
                i.putExtra("brandName", brandName);
                startActivity(i);
                drawerLayout.closeDrawer(GravityCompat.START);
            }

            @Override
            public void onOpenBallsAll() {
                Intent i = new Intent(MainActivity.this, ProductListActivity.class);
                i.putExtra("category", "balls");
                startActivity(i);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        drawerRecycler.setAdapter(drawerAdapter);
        drawerRecycler.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        setupDrawerSkeleton();
        fetchBrandsForCategory("racket", 1);
        fetchBrandsForCategory("shoes", 3);

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

    private void setupDrawerSkeleton() {
        drawerGroups.clear();

        DrawerAdapter.Group rackets = new DrawerAdapter.Group("Vợt Pickleball", "racket", false);
        rackets.children.add(new DrawerAdapter.Child("Xem tất cả Vợt", "racket", true, 0));
        drawerGroups.add(rackets);

        DrawerAdapter.Group shoes = new DrawerAdapter.Group("Giày Pickleball", "shoes", false);
        shoes.children.add(new DrawerAdapter.Child("Xem tất cả Giày", "shoes", true, 0));
        drawerGroups.add(shoes);

        DrawerAdapter.Group balls = new DrawerAdapter.Group("Bóng Pickleball", "balls", false);
        drawerGroups.add(balls);

        drawerAdapter.setData(drawerGroups);
    }

    private void fetchBrandsForCategory(String categoryKey, int categoryId) {
        ApiClient.getApiService().getBrandsByCategory(categoryId)
                .enqueue(new Callback<BrandResponse>() {
                    @Override
                    public void onResponse(Call<BrandResponse> call, Response<BrandResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            java.util.List<BrandResponse.BrandSimple> brandList = response.body().getData();
                            if (brandList == null || brandList.isEmpty()) {
                                return;
                            }

                            runOnUiThread(() -> {
                                for (DrawerAdapter.Group group : drawerGroups) {
                                    if (group.category.equals(categoryKey)) {
                                        java.util.List<DrawerAdapter.Child> preserved = new java.util.ArrayList<>();
                                        for (DrawerAdapter.Child child : group.children) {
                                            if (child.isAll) {
                                                preserved.add(child);
                                            }
                                        }
                                        group.children.clear();
                                        group.children.addAll(preserved);
                                        for (BrandResponse.BrandSimple brand : brandList) {
                                            group.children.add(new DrawerAdapter.Child(brand.name, categoryKey, false, brand.id));
                                        }
                                        break;
                                    }
                                }
                                drawerAdapter.setData(drawerGroups);
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<BrandResponse> call, Throwable t) {
                        // Optional: handle error (log/toast)
                    }
                });
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
            Intent i = new Intent(this, ProductListActivity.class);
            i.putExtra("category", "racket");
            startActivity(i);
        } else if (id == R.id.nav_shoes) {
            Intent i = new Intent(this, ProductListActivity.class);
            i.putExtra("category", "shoes");
            startActivity(i);
        } else if (id == R.id.nav_accessories) {
            Intent i = new Intent(this, ProductListActivity.class);
            i.putExtra("category", "balls");
            startActivity(i);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
