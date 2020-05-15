package com.outerspace.luis_viruena_baking2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.library.BuildConfig;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.outerspace.luis_viruena_baking2.api.Recipe;
import com.outerspace.luis_viruena_baking2.databinding.ActivityMainBinding;
import com.outerspace.luis_viruena_baking2.helper.StepToBind;
import com.outerspace.luis_viruena_baking2.model.IRecipeModel;
import com.outerspace.luis_viruena_baking2.model.ModelBehavior;
import com.outerspace.luis_viruena_baking2.model.RecipeModelFactory;

import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.util.List;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements IMainView, ViewPager.OnPageChangeListener {
    public ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    public static final String EXTRA_BEHAVIOR = "behavior";
    private int[] arrayPages;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        boolean smallScreen = binding.mainScreenLayout.getTag().equals(getString(R.string.phone_screen));
        mainViewModel.setSmallScreen(smallScreen);

        // sets the toolbar
        setSupportActionBar(binding.toolbar);

        // setup ViewPager
        Class[] fragmentClassArray;
        if(mainViewModel.isSmallScreen()) {
            arrayPages = new int[] {0, 1, 2};
            fragmentClassArray = new Class[] {
                    RecipeListFragment.class, RecipeStepFragment.class, RecipeDetailFragment.class
            };
        } else {
            arrayPages = new int[] {0, 1, 1};
            fragmentClassArray = new Class[] {
                    RecipeListFragment.class, RecipeStepAndDetailFragment.class
            };
        }

        RecipePagerAdapter adapter = new RecipePagerAdapter(getSupportFragmentManager(), fragmentClassArray);
        binding.pager.setAdapter(adapter);
        binding.pager.addOnPageChangeListener(this);
        binding.pager.setOffscreenPageLimit(2);

        // ViewModel observers

        // this form (add the observer before setting the mutableViewPagerPage)
        // is needed to prevent a weird bug. The viewPager was moving without
        // calling setValue on rotation
        MutableLiveData<Integer> mutable = new MutableLiveData<>();
        mutable.observe(this, this::viewPagerToPage);
        mainViewModel.setMutableViewPagerPage(mutable);

        mainViewModel.getMutableOnProgress().observe(this, showProgress ->
                binding.progress.setVisibility(showProgress ? View.VISIBLE : View.GONE));

        mainViewModel.getMutableNetworkError().observe(this, httpErrorCode -> {
            mainViewModel.getMutableOnProgress().setValue(false);
            int title, message;
            switch (httpErrorCode) {
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    title = R.string.internal_error_title;
                    message = R.string.internal_error_message;
                    break;
                case HttpURLConnection.HTTP_NO_CONTENT:
                    title = R.string.empty_response_title;
                    message = R.string.empty_response_message;
                    break;
                default:
                    title = R.string.network_error_title;
                    message = R.string.network_error_message;
            }

            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setNegativeButton(R.string.exit_app, (dialog, which) -> super.onBackPressed())
                    .setPositiveButton(R.string.try_again, (dialog, which) -> fetchRecipeListFromModel())
                    .create().show();
        });

        mainViewModel.getMutableShowToast().observe(this, showToast -> {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_recipe_detail, findViewById(R.id.toast_layout));
            Toast toast = new Toast(this);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM,0, 100);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        });

        // Fetch for recipe list from the network.
        // if the list is not already available in the mainViewModel, it will
        // run the network request.
        // but, if it is already available from a previous fetch, it uses it.
        List<Recipe> recipeList = mainViewModel.getMutableRecipeList().getValue();
        if( recipeList == null) {
            mainViewModel.getMutableOnProgress().setValue(true);
            fetchRecipeListFromModel();
        } else {
            mainViewModel.getMutableRecipeList().setValue(recipeList);
        }
    }

    private static final String KEY_VIEW_PAGER_PAGE = "view_pager_page";
    public static final String KEY_RECIPE_SELECTION = "recipe_selection";
    public static final String KEY_STEP_SELECTION = "step_selection";
    public static final String KEY_PLAYBACK_POSITION = "playback_position";
    public static final String KEY_VIDEO_URL = "video_url";

    // onRecipeListReady is called back at the RecipeListFragment when the model returns the RecipeList
    @Override
    public void onRecipeListReady(List<Recipe> recipeList) {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        int recipeSelection = preferences.getInt(KEY_RECIPE_SELECTION, -1);
        int stepSelection = preferences.getInt(KEY_STEP_SELECTION, -1);

        if(recipeSelection >= 0) {
            Recipe recipe = recipeList.get(recipeSelection);
            mainViewModel.getMutableRecipe().setValue(recipe);
            mainViewModel.getMutableRecipeSelection().setValue(recipeSelection);
            if(stepSelection >= 0) {
                if(stepSelection == 0) {
                    mainViewModel.getMutableStep().setValue(
                            new StepToBind(recipe.ingredients, getString(R.string.ingredients))
                    );
                } else {
                    mainViewModel.getMutableStep().setValue(
                            new StepToBind(recipe.steps.get(stepSelection-1))
                    );
                }
                mainViewModel.getMutableStepSelection().setValue(stepSelection);
            } else {
                mainViewModel.getMutableStep().setValue(null);
            }
        } else {
            mainViewModel.getMutableRecipe().setValue(null);
        }

        if(preferences.contains(KEY_VIEW_PAGER_PAGE)) {
            int page = preferences.getInt(KEY_VIEW_PAGER_PAGE, RECIPE_LIST_PAGE);
            mainViewModel.getMutableViewPagerPage().setValue(page);
        }
    }

    @Override
    public void onBackPressed() {
        int page = binding.pager.getCurrentItem();
        if(page > 0) {
            mainViewModel.getMutableViewPagerPage().setValue(page - 1);
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.wanna_exit)
                    .setPositiveButton(R.string.exit_app, (dialog, which) -> {
                        super.onBackPressed();
                        getPreferences(MODE_PRIVATE)
                                .edit()
                                .remove(KEY_RECIPE_SELECTION)
                                .remove(KEY_STEP_SELECTION)
                                .remove(KEY_VIEW_PAGER_PAGE)
                                .remove(KEY_VIDEO_URL)
                                .remove(KEY_PLAYBACK_POSITION)
                                .apply();
                    })
                    .setNegativeButton(R.string.back_to_app, null)
                    .create().show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.main_menu_about) {
            showAboutDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.about)
                .setNeutralButton(R.string.thanks, null)
                .setView(R.layout.about_dialog)
                .create()
                .show();
    }

    private void fetchRecipeListFromModel() {
        ModelBehavior behavior;
        if(getIntent().hasExtra(EXTRA_BEHAVIOR)) {
            behavior = (ModelBehavior) getIntent().getSerializableExtra(EXTRA_BEHAVIOR);
        } else {
            behavior = ModelBehavior.NETWORK_REQUEST;
        }
        IRecipeModel model = new RecipeModelFactory.Builder().setBehavior(behavior).build();
        model.fetchRecipeList(
                mainViewModel.getMutableRecipeList(),
                mainViewModel.getMutableNetworkError());
    }

    private void viewPagerToPage(int page) {
        new Thread(() -> {
            binding.pager.postDelayed(
                    () -> binding.pager.setCurrentItem(arrayPages[page], true),
                    getResources().getInteger(R.integer.third_of_a_second));
        }).start();
    }

    private static class RecipePagerAdapter extends FragmentPagerAdapter {
        Class<? extends Fragment>[] fragmentClassArray;

        RecipePagerAdapter(@NonNull FragmentManager fm, Class<? extends Fragment>[] fragmentClassArray) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragmentClassArray = fragmentClassArray;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            try {
                return fragmentClassArray[position].getConstructor().newInstance();
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                return new Fragment();
            }
        }

        @Override
        public int getCount() {
            return fragmentClassArray.length;
        }
    }

    @Override
    public void onPageSelected(int position) {
        getPreferences(MODE_PRIVATE)
                .edit()
                .putInt(KEY_VIEW_PAGER_PAGE, position)
                .apply();
    }

    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
    @Override public void onPageScrollStateChanged(int state) { }

}
