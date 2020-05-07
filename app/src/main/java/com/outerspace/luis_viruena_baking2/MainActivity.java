package com.outerspace.luis_viruena_baking2;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.library.BuildConfig;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.outerspace.luis_viruena_baking2.api.Recipe;
import com.outerspace.luis_viruena_baking2.databinding.ActivityMainBinding;
import com.outerspace.luis_viruena_baking2.model.IRecipeModel;
import com.outerspace.luis_viruena_baking2.model.ModelBehavior;
import com.outerspace.luis_viruena_baking2.model.RecipeModelFactory;

import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.util.List;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    public ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    public static final String EXTRA_BEHAVIOR = "behavior";
    private int[] arrayPages;

    @FunctionalInterface
    interface ArraySupplier<E> {
        E[] get(int length);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setSmallScreen(binding.phoneScreenLayout != null);

        arrayPages = new int[] {0, 1, 2};
        Class[] fragmentClassArray = new Class[] {
                RecipeListFragment.class, RecipeStepFragment.class, RecipeDetailFragment.class
        };

        RecipePagerAdapter adapter = new RecipePagerAdapter(getSupportFragmentManager(), fragmentClassArray);
        binding.pager.setAdapter(adapter);
        binding.pager.setOffscreenPageLimit(2);

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
            @StringRes int title = httpErrorCode == HttpURLConnection.HTTP_NO_CONTENT ?
                    R.string.empty_response_title : R.string.network_error_title;
            @StringRes int message = httpErrorCode == HttpURLConnection.HTTP_NO_CONTENT ?
                    R.string.empty_response_message : R.string.network_error_message;
            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setNegativeButton(R.string.exit_app, (dialog, which) -> super.onBackPressed())
                    .setPositiveButton(R.string.try_again, (dialog, which) -> fetchRecipeListFromModel())
                    .create().show();
        });

        List<Recipe> recipeList = mainViewModel.getMutableRecipeList().getValue();
        if( recipeList == null) {
            mainViewModel.getMutableOnProgress().setValue(true);
            fetchRecipeListFromModel();
        } else {
            mainViewModel.getMutableRecipeList().setValue(recipeList);
        }
    }
    private void fetchRecipeListFromModel() {
        ModelBehavior behavior = (ModelBehavior) getIntent().getSerializableExtra(EXTRA_BEHAVIOR);
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

    @Override
    public void onBackPressed() {
        int page = binding.pager.getCurrentItem();
        if(page > 0) {
            mainViewModel.getMutableViewPagerPage().setValue(page - 1);
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.wanna_exit)
                    .setPositiveButton(R.string.exit_app, (dialog, which) -> super.onBackPressed())
                    .setNegativeButton(R.string.back_to_app, null)
                    .create().show();
        }
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
}