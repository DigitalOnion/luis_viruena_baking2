package com.outerspace.luis_viruena_baking2;

import android.os.Bundle;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.outerspace.luis_viruena_baking2.databinding.FragmentRecipeDetailBinding;
import com.outerspace.luis_viruena_baking2.exo.BPlayerViewModel;
import com.outerspace.luis_viruena_baking2.helper.StepToBind;

public class RecipeDetailFragment extends Fragment {
    private FragmentRecipeDetailBinding binding;
    private BPlayerViewModel bpViewModel;

    public RecipeDetailFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainViewModel mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
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

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        bpViewModel.getMutableVideoUrl().setValue(bpViewModel.getMutableVideoUrl().getValue());
        bpViewModel.getMutablePlaybackPosition().setValue(bpViewModel.getMutablePlaybackPosition().getValue());
    }

    @BindingAdapter("ingredients")
    public static void setIngredients(WebView web, String ingredients) {
        web.loadData(ingredients, "text/html", "utf-8");
    }
}
