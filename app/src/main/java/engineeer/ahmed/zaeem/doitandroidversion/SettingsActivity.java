package engineeer.ahmed.zaeem.doitandroidversion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {
    Spinner fontSizeSpinner;
    TextView usernameTextView, phoneTextView;
    Button logoutButton;
    SharedPreferences sharedPreferences;
    UserDatabaseHelper dbHelper;
    String username;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        fontSizeSpinner = findViewById(R.id.fontSizeSpinner);
        usernameTextView = findViewById(R.id.usernameTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        logoutButton = findViewById(R.id.logoutButton);
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        dbHelper = new UserDatabaseHelper(this);
        // Get user details from SharedPreferences
        username = sharedPreferences.getString("username", "");
        phone = sharedPreferences.getString("phone", "");
        String savedFontSize = sharedPreferences.getString("font_size", "Medium");
        usernameTextView.setText(username);
        phoneTextView.setText(phone);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.font_size_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontSizeSpinner.setAdapter(adapter);
        int pos = adapter.getPosition(savedFontSize);
        fontSizeSpinner.setSelection(pos);
        fontSizeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                dbHelper.setFontSizeForUser(username, selected);
                sharedPreferences.edit().putString("font_size", selected).apply();
                applyFontSize(selected);
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            sharedPreferences.edit().clear().apply();
                            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        buttonBack.setOnClickListener(v -> finish());
        // Apply font size on load
        applyFontSize(savedFontSize);
    }

    private void applyFontSize(String fontSize) {
        float sizeSp = 16f;
        if ("Small".equals(fontSize)) sizeSp = 14f;
        else if ("Large".equals(fontSize)) sizeSp = 20f;
        usernameTextView.setTextSize(sizeSp);
        phoneTextView.setTextSize(sizeSp);
        logoutButton.setTextSize(sizeSp);
        fontSizeSpinner.setPadding(fontSizeSpinner.getPaddingLeft(), fontSizeSpinner.getPaddingTop(), fontSizeSpinner.getPaddingRight(), (int) sizeSp); // Optional: adjust spinner padding
        // Add more views as needed
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
