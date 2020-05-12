package com.outerspace.luis_viruena_baking2;

import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.outerspace.luis_viruena_baking2.model.ModelBehavior;
import com.outerspace.luis_viruena_baking2.model.RecipeModelFactory;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class RecipeListLoadingTest {
    private static final boolean INITIAL_TOUCH_MODE = true;
    private static final boolean DO_NOT_LAUNCH_ACTIVITY = false;

    /* NOTE: This test suite, requires mockDebug build variant to run successfully   */

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class, INITIAL_TOUCH_MODE, DO_NOT_LAUNCH_ACTIVITY);

    @Test
    public void emptyRecipeListTest() {
        Intent startIntent = new Intent();
        startIntent.putExtra(MainActivity.EXTRA_BEHAVIOR, ModelBehavior.MOCK_EMPTY_LIST);
        activityTestRule.launchActivity(startIntent);

        onView(withText(R.string.empty_response_title)).check(matches(isDisplayed()));
    }

    @Test
    public void oneRecordRecipeListTest() {
        Intent startIntent = new Intent();
        startIntent.putExtra(MainActivity.EXTRA_BEHAVIOR, ModelBehavior.MOCK_ONE_RECORD_LIST);
        activityTestRule.launchActivity(startIntent);

        onView(withId(R.id.recipeRecycler)).check(new RecyclerViewItemCountAssertion(1));
    }

    @Test
    public void manyRecordRecipeListTest() {
        Intent startIntent = new Intent();
        startIntent.putExtra(MainActivity.EXTRA_BEHAVIOR, ModelBehavior.MOCK_LONG_LIST);
        activityTestRule.launchActivity(startIntent);

        onView(withId(R.id.recipeRecycler)).check(new RecyclerViewItemCountAssertion(RecipeModelFactory.LONG_RECIPE_LENGTH));
    }

    @Test
    public void networkErrorRecipeListTest() {
        Intent startIntent = new Intent();
        startIntent.putExtra(MainActivity.EXTRA_BEHAVIOR, ModelBehavior.MOCK_NETWORK_ERROR);
        activityTestRule.launchActivity(startIntent);

        onView(withText(R.string.network_error_title)).check(matches(isDisplayed()));
    }

    // CREDIT: https://stackoverflow.com/questions/36399787/how-to-count-recyclerview-items-with-espresso
    private class RecyclerViewItemCountAssertion implements ViewAssertion {
        private final int expectedCount;

        public RecyclerViewItemCountAssertion(int expectedCount) {
            this.expectedCount = expectedCount;
        }

        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            assertThat(adapter.getItemCount(), is(expectedCount));
        }
    }
}
