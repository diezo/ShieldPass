package DataStructures;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;

public class PasswordItem
{
    public String encryptedPlatformIdentifier;
    public String encryptedUsername;
    public String encryptedPassword;

    public PasswordItem(@NonNull AppCompatActivity activity, String key, String platformIdentifier, String username, String password)
    {
        try
        {
            this.encryptedPlatformIdentifier = encrypt(key, platformIdentifier);
            this.encryptedUsername = encrypt(key, username);
            this.encryptedPassword = encrypt(key, password);
        }
        catch (GeneralSecurityException ignored) {activity.finishAffinity();}
    }

    private String encrypt(String key, String data) throws GeneralSecurityException
    {
        return AESCrypt.encrypt(key, data);
    }
}
