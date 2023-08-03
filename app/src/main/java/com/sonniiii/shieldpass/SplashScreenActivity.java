package com.sonniiii.shieldpass;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import shared.Authentication;
import shared.HideActionBar;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        new HideActionBar(this);

        new Handler().postDelayed(() ->
        {
            if (Authentication.is_logged_in(this)) startActivity(new Intent(this, AuthorizeActivity.class));
            else startActivity(new Intent(this, LoginActivity.class));

            finish();
        }
        , 300);
    }
}