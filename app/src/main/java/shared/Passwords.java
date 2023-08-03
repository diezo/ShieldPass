package shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.JsonReader;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.crypto.NoSuchPaddingException;

import DataStructures.DecryptedPasswordItem;
import DataStructures.PasswordItem;

public class Passwords
{
    public static void Save(Context context, PasswordItem passwordItem)
    {
        try
        {
            SharedPreferences sharedPreferences = context.getSharedPreferences("Passwords", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            JSONObject jsonObject = new JSONObject(sharedPreferences.getString("Items", "{\"Data\": []}"));
            JSONArray jsonArray = jsonObject.getJSONArray("Data");

            JSONObject item = new JSONObject();
            item.put("encryptedPlatformIdentifier", passwordItem.encryptedPlatformIdentifier);
            item.put("encryptedUsername", passwordItem.encryptedUsername);
            item.put("encryptedPassword", passwordItem.encryptedPassword);

            jsonArray.put(item);
            jsonObject.put("Data", jsonArray);

            editor.putString("Items", jsonObject.toString());
            editor.apply();
        }
        catch (JSONException ignored) {}
    }

    public static void Delete(Context context, int position)
    {
        try
        {
            SharedPreferences sharedPreferences = context.getSharedPreferences("Passwords", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            JSONObject jsonObject = new JSONObject(sharedPreferences.getString("Items", "{\"Data\": []}"));
            JSONArray jsonArray = jsonObject.getJSONArray("Data");

            jsonArray.remove(position);
            jsonObject.put("Data", jsonArray);

            editor.putString("Items", jsonObject.toString());
            editor.apply();
        }
        catch (JSONException ignored) {}
    }

    public static ArrayList<DecryptedPasswordItem> Load(Context context, AppCompatActivity activity, String key)
    {
        ArrayList<DecryptedPasswordItem> arrayList = new ArrayList<>();

        try
        {
            SharedPreferences sharedPreferences = context.getSharedPreferences("Passwords", Context.MODE_PRIVATE);

            JSONObject jsonObject = new JSONObject(sharedPreferences.getString("Items", "{\"Data\": []}"));
            JSONArray jsonArray = jsonObject.getJSONArray("Data");

            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject object = jsonArray.getJSONObject(i);

                arrayList.add(
                        new DecryptedPasswordItem(
                                activity, key,
                                object.getString("encryptedPlatformIdentifier"),
                                object.getString("encryptedUsername"),
                                object.getString("encryptedPassword")
                        )
                );
            }
        }
        catch (JSONException ignored) {}

        return arrayList;
    }
}
