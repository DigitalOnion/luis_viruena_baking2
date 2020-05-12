package com.outerspace.luis_viruena_baking2;

import androidx.core.util.Consumer;

import com.outerspace.luis_viruena_baking2.api.Recipe;
import com.outerspace.luis_viruena_baking2.model.IRecipeModel;
import com.outerspace.luis_viruena_baking2.model.ModelBehavior;
import com.outerspace.luis_viruena_baking2.model.RecipeModelFactory;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class RecipeModelTest {

    /* NOTE: This test suite, requires prodDebug build variant to run successfully   */

       /**
        * fetchRecipeListTest
        *
        * I created this method to make a Unit Test of the Network Asynchronous Call.
        * as an alternative to Idling Resources with Espresso.
        *
        * it achieves synchronization by means of Thread.join
        *
        * The network call is started in a new thread which later goes to sleep indefinitely,
        * we can call it startingThread. The network call will spawn its own work thread
        * for which we do not have control.
        *
        * Since the result is served on a Consumer, we can extend the Consumer class
        * to hold a thread. Before we start the startingThread, we pass it to the consumers
        *
        * When the results are finally returned from the network, the starting thread is
        * interrupted at the consumers. remember that up to that point the starting thread
        * has been looping and sleeping.
        */

    @Test
    public void fetchRecipeListTest() {
        final ConsumerOnThread<List<Recipe>> resultConsumer = new ConsumerOnThread<>();
        final ConsumerOnThread<Integer> errorConsumer = new ConsumerOnThread<>();

        try {
            Thread startingThread = new Thread(() -> {
                // The model has an asynchronous process when we call fetchRecipeList
                IRecipeModel model = new RecipeModelFactory.Builder()
                        .setBehavior(ModelBehavior.NETWORK_REQUEST)
                        .build();
                model.fetchRecipeList(resultConsumer, errorConsumer);

                try {
                    do {
                        Thread.sleep(1000); // sleep forever, one second at a time.
                    } while(true);
                } catch (InterruptedException e) { /* interrupted by consumers */ }
                // thread ends here
            });
            resultConsumer.setThread(startingThread);
            errorConsumer.setThread(startingThread);
            startingThread.start();
            startingThread.join();       // this will block the Main Test Thread until interrupted by consumers
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
            return;
        }

        List<Recipe> recipes = resultConsumer.getAccepted();
        Assert.assertNotNull(recipes);
    }

    private static class ConsumerOnThread<T> implements Consumer<T> {
        private T t;
        private Thread thread;

        void setThread(Thread thread) {
            this.thread = thread;
        }

        T getAccepted() {
            return t;
        }

        @Override
        public void accept(T t) {
            this.t = t;
            thread.interrupt();
        }
    };
}