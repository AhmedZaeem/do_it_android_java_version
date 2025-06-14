package engineeer.ahmed.zaeem.doitandroidversion;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
            Intent intent;
            if (isLoggedIn) {
                intent = new Intent(this, HomeScreenActivity.class);
            } else {
                intent = new Intent(this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, 3000);
    }
}