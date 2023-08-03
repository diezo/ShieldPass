package ArrayAdapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.sonniiii.shieldpass.R;

import java.util.ArrayList;

import DataStructures.DecryptedPasswordItem;

public class PasswordItemsAdapter extends BaseAdapter
{
    private final String USERNAME = "username";
    private final String PASSWORD = "password";

    Context context;
    ArrayList<DecryptedPasswordItem> passwordItems;
    LayoutInflater layoutInflater;

    public PasswordItemsAdapter(@NonNull Context context, ArrayList<DecryptedPasswordItem> passwordItems)
    {
        this.context = context;
        this.passwordItems = passwordItems;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) convertView = layoutInflater.inflate(R.layout.password_item, parent, false);

        LinearLayout linearLayout = convertView.findViewById(R.id.PasswordItem_Container);
        linearLayout.setOnLongClickListener(v -> { DeleteItem(position); return false; });

        TextView titleTextView = convertView.findViewById(R.id.PasswordItem_ItemTitleText);
        titleTextView.setText(passwordItems.get(position).platformIdentifier);

        TextView subtitleTextView = convertView.findViewById(R.id.PasswordItem_ItemSubtitleText);
        subtitleTextView.setText(passwordItems.get(position).username);

        ImageView copyUsernameButton = convertView.findViewById(R.id.PasswordItem_CopyUsernameButton);
        ImageView copyPasswordButton = convertView.findViewById(R.id.PasswordItem_CopyPasswordButton);

        linearLayout.setOnClickListener(v ->
        {
            if (position + 1 <= passwordItems.size()) copyToClipboard(PASSWORD, passwordItems.get(position).password);
        });

        copyUsernameButton.setOnClickListener(v ->
        {
            if (position + 1 <= passwordItems.size()) copyToClipboard(USERNAME, passwordItems.get(position).username);
        });

        copyPasswordButton.setOnClickListener(v ->
        {
            if (position + 1 <= passwordItems.size()) copyToClipboard(PASSWORD, passwordItems.get(position).password);
        });

        return convertView;
    }

    private void DeleteItem(int position)
    {
        Intent intent = new Intent("PasswordItemsAdapter_DeleteItem");
        intent.putExtra("position", position);

        context.sendBroadcast(intent);
    }

    private void copyToClipboard(String label, String text)
    {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText(label, text));

        Toast.makeText(context, String.format("Copied %s", label), Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getCount() { return passwordItems.size(); }

    @Override
    public DecryptedPasswordItem getItem(int position) { return passwordItems.get(position); }

    @Override
    public long getItemId(int position) { return position; }
}
