package engineeer.ahmed.zaeem.doitandroidversion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    EditText usernameEditText, phoneEditText, passwordEditText;
    Button registerButton;
    UserDatabaseHelper dbHelper, userDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usernameEditText = findViewById(R.id.editTextUsername);
        phoneEditText = findViewById(R.id.editTextPhone);
        passwordEditText = findViewById(R.id.editTextPassword);
        registerButton = findViewById(R.id.buttonRegister);
        dbHelper = new UserDatabaseHelper(this);
        userDbHelper = new UserDatabaseHelper(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (username.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (dbHelper.isUsernameTaken(username)) {
                    Toast.makeText(RegisterActivity.this, "Username already taken", Toast.LENGTH_SHORT).show();
                    return;
                }
                dbHelper.insertUser(username, phone, password);
                Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        applyFontSize();
    }

    private void applyFontSize() {
        String fontSize = userDbHelper.getFontSizeForUser(usernameEditText.getText().toString());
        float sizeSp = 16f;
        if ("Small".equals(fontSize)) sizeSp = 14f;
        else if ("Large".equals(fontSize)) sizeSp = 20f;
        usernameEditText.setTextSize(sizeSp);
        phoneEditText.setTextSize(sizeSp);
        passwordEditText.setTextSize(sizeSp);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
