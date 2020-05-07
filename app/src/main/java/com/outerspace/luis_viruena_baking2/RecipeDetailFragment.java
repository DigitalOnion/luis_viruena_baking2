package com.outerspace.luis_viruena_baking2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.outerspace.luis_viruena_baking2.databinding.FragmentRecipeDetailBinding;
import com.outerspace.luis_viruena_baking2.exo.BPlayerViewModel;
import com.outerspace.luis_viruena_baking2.helper.OnSwipeGestureListener;
import com.outerspace.luis_viruena_baking2.helper.StepToBind;

public class RecipeDetailFragment extends Fragment implements OnSwipeGestureListener {
    private FragmentRecipeDetailBinding binding;
    private BPlayerViewModel bpViewModel;
    private MainViewModel mainViewModel;
    private GestureDetectorCompat gestureDetector;

    public RecipeDetailFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        bpViewModel = new ViewModelProvider(getActivity()).get(BPlayerViewModel.class);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_detail, container, false);
        binding.setLifecycleOwner(this);
        binding.setBPlayerViewModel(bpViewModel);
        getLifecycle().addObserver(binding.bPlayer);

        bpViewModel.getMutableVideoUrl().observe(this, binding.bPlayer::playVideo);
        bpViewModel.getMutablePlaybackPosition().observe(this, binding.bPlayer::seekTo);

        mainViewModel.getMutableStep().observe(this, (step) -> {
            StepToBind preStep = binding.getStep();
            if(preStep != null && step != null && preStep.id != step.id) {
                bpViewModel.getMutablePlaybackPosition().setValue(0L);
            }
            binding.setStep(step);
        });

        // Gesture detector for the swipe up/down on the recipeDetailFragment
        gestureDetector = new GestureDetectorCompat(getActivity(), this);

        binding.getRoot().setOnTouchListener((view, event) -> {
            view.performClick();
            return gestureDetector.onTouchEvent(event);
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        bpViewModel.getMutableVideoUrl().setValue(bpViewModel.getMutableVideoUrl().getValue());
        bpViewModel.getMutablePlaybackPosition().setValue(bpViewModel.getMutablePlaybackPosition().getValue());
    }

        @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // rate = |velocityY/VelocityX|; it tells the direction of the swipe.
        // rate > 1 ==> greater than 45 degrees => swipe up or down
        // the test is in case velocityX equal or close to zero
        float rate = Math.abs(velocityX) < 0.01f ? 100f : Math.abs(velocityY / velocityX);
        if (rate > 1.0f) {
            if(Math.abs(velocityY) > (float) getResources().getInteger(R.integer.min_swipe_velocity)) {
                // above the speed limit to consider it a swipe
                if (velocityY < 0.0f) {
                    mainViewModel.getMutableDetailOffset().setValue(+1);
                } else {
                    mainViewModel.getMutableDetailOffset().setValue(-1);
                }
            }
            return true;
        }
        return false;
    }

    @BindingAdapter("ingredients")
    public static void setIngredients(WebView web, String ingredients) {
        web.loadData(ingredients, "text/html", "utf-8");
    }
}
