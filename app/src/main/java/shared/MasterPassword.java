package shared;

import android.content.Context;
import android.content.SharedPreferences;

public class MasterPassword
{
    public static void SaveHash(Context context, String hash)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MasterPassword", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("hash", hash);
        editor.apply();
    }

    public static String LoadHash(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MasterPassword", Context.MODE_PRIVATE);
        return sharedPreferences.getString("hash", "");
    }
}
