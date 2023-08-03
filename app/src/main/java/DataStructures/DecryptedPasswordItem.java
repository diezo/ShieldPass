package DataStructures;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;

public class DecryptedPasswordItem
{
    public String platformIdentifier;
    public String username;
    public String password;

    public DecryptedPasswordItem(@NonNull AppCompatActivity activity, String key, String encryptedPlatformIdentifier, String encryptedUsername, String encryptedPassword)
    {
        try
        {
            this.platformIdentifier = decrypt(key, encryptedPlatformIdentifier);
            this.username = decrypt(key, encryptedUsername);
            this.password = decrypt(key, encryptedPassword);
        }
        catch (GeneralSecurityException ignored) {activity.finishAffinity();}
    }

    private String decrypt(String key, String data) throws GeneralSecurityException
    {
        return AESCrypt.decrypt(key, data);
    }
}
