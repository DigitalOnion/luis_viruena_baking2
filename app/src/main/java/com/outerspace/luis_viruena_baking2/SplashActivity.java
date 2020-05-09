package com.outerspace.luis_viruena_baking2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

/**
 * Splash activity. It just presents an empty activity with a styled
 * background. it launches the main activity immediately, therefore
 * the MainActivity's initialization is done with the splash on the
 * screen rather than a white empty screen. It also does not add
 * artificial arbitrary wait.
 *
 * Credit:  Big Nerd Ranch: Splash Screens the Right Way
 *          https://www.bignerdranch.com/blog/splash-screens-the-right-way/
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
