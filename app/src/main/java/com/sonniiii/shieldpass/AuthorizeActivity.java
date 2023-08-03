package com.sonniiii.shieldpass;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import Storage.DeveloperInfo;
import shared.HideActionBar;
import shared.MasterPassword;

public class AuthorizeActivity extends AppCompatActivity
{
    LinearLayout developerProfileButton;
    EditText masterPasswordInput;
    TextView checkButton;
    ProgressBar progressBar;
    boolean busy;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorize);
        new HideActionBar(this);

        initComponents();
        bindListeners();
        setDefaults();
    }

    private void initComponents()
    {
        masterPasswordInput = findViewById(R.id.Authorize_MasterPasswordInput);
        checkButton = findViewById(R.id.Authorize_CheckButton);
        progressBar = findViewById(R.id.Authorize_ProgressBar);
        developerProfileButton = findViewById(R.id.Authorize_DeveloperProfileButton);
    }

    private void bindListeners()
    {
        checkButton.setOnClickListener(v ->
        {
            String masterPassword = masterPasswordInput.getText().toString();

            if (!masterPassword.equals(""))
            {
                if (!busy) processMasterPassword(masterPassword);
            }
        });

        developerProfileButton.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(DeveloperInfo.ProfileUrl))));
    }

    private void processMasterPassword(String masterPassword)
    {
        busy = true;
        masterPasswordInput.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() ->
        {
            String masterPasswordHash = computeHash(masterPassword);
            if (MasterPassword.LoadHash(this).equals(masterPasswordHash))
            {
                runOnUiThread(() ->
                {
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.putExtra("rawMasterPassword", masterPassword);

                    if (getIntent().hasExtra("shouldForward")) intent.putExtra("action", getIntent().getStringExtra("shouldForward"));

                    startActivity(intent);
                    finish();
                });
            }
            else
            {
                runOnUiThread(() ->
                {
                    Toast.makeText(this, "Incorrect master password!", Toast.LENGTH_SHORT).show();

                    progressBar.setVisibility(View.INVISIBLE);
                    masterPasswordInput.setEnabled(true);
                });

                busy = false;
            }
        }
        , 500);
    }

    private void setDefaults() { busy = false; }

    private String computeHash(String masterPassword)
    {
        String hashOutput = "";

        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.reset();
            messageDigest.update(masterPassword.getBytes());

            byte[] digest = messageDigest.digest();
            StringBuilder stringBuilder = new StringBuilder(digest.length * 2);

            for (byte b : digest)
            {
                int v = b & 0xff;
                if (v < 16) stringBuilder.append("0");
                stringBuilder.append(Integer.toHexString(v));
            }

            hashOutput = stringBuilder.toString();
        }
        catch (NoSuchAlgorithmException exception) {finishAffinity();}

        return hashOutput;
    }
}