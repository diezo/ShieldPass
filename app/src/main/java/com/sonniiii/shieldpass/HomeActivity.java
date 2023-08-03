package com.sonniiii.shieldpass;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ShortcutInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

import ArrayAdapters.PasswordItemsAdapter;
import DataStructures.DecryptedPasswordItem;
import DataStructures.PasswordItem;
import shared.HideActionBar;
import shared.Passwords;

public class HomeActivity extends AppCompatActivity
{
    final int GENERATED_PASSWORD_LENGTH = 20;
    String RAW_MASTER_PASSWORD;
    ImageView addPasswordButton;
    ListView listView;
    ImageView noItemsRepresentation;
    PasswordItemsAdapter adapter;
    ArrayList<DecryptedPasswordItem> passwordItems;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        new HideActionBar(this);

        // Intent Actions
        if (getIntent().hasExtra("action"))
        {
            if (Objects.equals(getIntent().getStringExtra("action"), "add_password")) addPasswordDialog(null);
        }

        registerShortcuts();
        load_raw_master_password();
        initComponents();
        bindListeners();
        bindAdapter();
        loadItems();
        registerDeletionBroadcastListener();
    }

    private void registerShortcuts()
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage("com.sonniiii.shieldpass");
        intent.setClass(this, AuthorizeActivity.class);
        intent.putExtra("shouldForward", "add_password");

        ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(this, "add_password")
                .setShortLabel(getString(R.string.shortcut_label_add_password))
                .setLongLabel(getString(R.string.shortcut_label_add_password))
                .setIcon(IconCompat.createWithResource(this, R.drawable.add_password))
                .setIntent(intent)
                .build();

        ShortcutManagerCompat.pushDynamicShortcut(this, shortcutInfo);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.hasExtra("position")) deletePasswordItem(intent.getIntExtra("position", -1));
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void registerDeletionBroadcastListener()
    {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("PasswordItemsAdapter_DeleteItem");

        registerReceiver(receiver, intentFilter, RECEIVER_EXPORTED);
    }

    private void bindListeners()
    {
        addPasswordButton.setOnClickListener(this::addPasswordDialog);
    }

    private void addPasswordDialog(View v)
    {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.create_item_dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        if (dialog.getWindow() != null)
        {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        }

        TextView dialogCreateItemButton = dialog.findViewById(R.id.CreateItemDialog_CreateButton);
        TextView dialogGeneratePasswordButton = dialog.findViewById(R.id.CreateItemDialog_GeneratePasswordButton);
        TextView dialogPlatformIdentifierInput = dialog.findViewById(R.id.CreateItemDialog_PlatformIdentifierInput);
        TextView dialogUsernameInput = dialog.findViewById(R.id.CreateItemDialog_UsernameInput);
        TextView dialogPasswordInput = dialog.findViewById(R.id.CreateItemDialog_PasswordInput);

        dialogGeneratePasswordButton.setOnClickListener(vv ->
        {
            String allowedChars = "acdefghijklmnopqrstuvwxyz0123456789/\\,.<>;:'\"}{[]_+=-!@#$%^&*()`~|?";
            StringBuilder generatedPassword = new StringBuilder();

            for (int i = 0; i < GENERATED_PASSWORD_LENGTH; i++)
            {
                Random random = new Random();
                generatedPassword.append(allowedChars.charAt(random.nextInt(allowedChars.length() - 1)));
            }

            dialogPasswordInput.setText(generatedPassword);
        });

        dialogCreateItemButton.setOnClickListener(vv ->
        {
            String platformIdentifierValue = dialogPlatformIdentifierInput.getText().toString().trim();
            String usernameValue = dialogUsernameInput.getText().toString().trim();
            String passwordValue = dialogPasswordInput.getText().toString();

            if (!platformIdentifierValue.isEmpty() && !usernameValue.isEmpty() && !passwordValue.isEmpty())
            {
                createPasswordItem(new PasswordItem(this, RAW_MASTER_PASSWORD, platformIdentifierValue, usernameValue, passwordValue));
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void loadItems()
    {
        ArrayList<DecryptedPasswordItem> loadedItems = Passwords.Load(this, this, RAW_MASTER_PASSWORD);
        Collections.reverse(loadedItems);

        if (loadedItems.size() > 0)
        {
            loadedItems.forEach(this::addAdapterItem);

            noItemsRepresentation.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
        else
        {
            listView.setVisibility(View.GONE);
            noItemsRepresentation.setVisibility(View.VISIBLE);
        }
    }

    private void createPasswordItem(PasswordItem passwordItem)
    {
        Passwords.Save(this, passwordItem);

        clearAdapter();
        loadItems();
    }

    private void clearAdapter()
    {
        passwordItems.clear();
        adapter.notifyDataSetChanged();
    }

    private void deletePasswordItem(int position)
    {
        Passwords.Delete(this, passwordItems.size() - position - 1);
        clearAdapter();
        loadItems();

        Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
    }

    private void addAdapterItem(DecryptedPasswordItem passwordItem)
    {
        passwordItems.add(passwordItem);
        adapter.notifyDataSetChanged();
    }

    private void bindAdapter()
    {
        passwordItems = new ArrayList<>();
        adapter = new PasswordItemsAdapter(this, passwordItems);
        listView.setAdapter(adapter);
    }

    private void initComponents()
    {
        listView = findViewById(R.id.Home_PasswordItemsListView);
        addPasswordButton = findViewById(R.id.Home_AddPasswordButton);
        noItemsRepresentation = findViewById(R.id.Home_NoItemsRepresentation);
    }

    private void load_raw_master_password()
    {
        if (getIntent().hasExtra("rawMasterPassword"))
        {
            RAW_MASTER_PASSWORD = getIntent().getStringExtra("rawMasterPassword");
            if (RAW_MASTER_PASSWORD != null && RAW_MASTER_PASSWORD.isEmpty()) finishAffinity();
        }
        else finishAffinity();
    }
}