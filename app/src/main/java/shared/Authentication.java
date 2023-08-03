package shared;

import android.content.Context;
import android.content.SharedPreferences;

public class Authentication
{
    public static boolean is_logged_in(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MasterPassword", Context.MODE_PRIVATE);
        return !sharedPreferences.getString("hash", "").equals("");
    }
}
