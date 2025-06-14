package engineeer.ahmed.zaeem.doitandroidversion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText usernameEditText, passwordEditText;
    Button loginButton;
    TextView registerTextView;
    UserDatabaseHelper dbHelper;
    SharedPreferences sharedPreferences;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        registerTextView = findViewById(R.id.textViewRegister);
        dbHelper = new UserDatabaseHelper(this);
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);

        username = sharedPreferences.getString("username", "");
        applyFontSize();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (dbHelper.validateUser(username, password)) {
                    // Fetch user details
                    String phone = dbHelper.getPhoneByUsername(username);
                    String fontSize = dbHelper.getFontSizeForUser(username);
                    // Save all details in SharedPreferences
                    sharedPreferences.edit()
                        .putBoolean("isLoggedIn", true)
                        .putString("username", username)
                        .putString("phone", phone)
                        .putString("font_size", fontSize)
                        .apply();
                    Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyFontSize();
    }

    private void applyFontSize() {
        String fontSize = sharedPreferences.getString("font_size", "Medium");
        float sizeSp = 16f;
        if ("Small".equals(fontSize)) sizeSp = 14f;
        else if ("Large".equals(fontSize)) sizeSp = 20f;
        usernameEditText.setTextSize(sizeSp);
        passwordEditText.setTextSize(sizeSp);
        registerTextView.setTextSize(sizeSp);
        loginButton.setTextSize(sizeSp);
        // Add more views as needed
    }

    @Override
    public boolean onSupportNavigateUp() {
        // No back button for login screen
        return true;
    }
}
