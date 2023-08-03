package shared;

import androidx.appcompat.app.AppCompatActivity;

public class HideActionBar
{
    public HideActionBar(AppCompatActivity activity)
    {
        if (activity.getSupportActionBar() != null) activity.getSupportActionBar().hide();
    }
}
