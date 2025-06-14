package engineeer.ahmed.zaeem.doitandroidversion;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class HomeScreenActivity extends AppCompatActivity {
    TextView greetingTextView;
    RecyclerView recyclerView;
    FloatingActionButton fab;
    Button logoutButton;
    EditText searchEditText;
    Spinner statusFilterSpinner;
    TaskAdapter adapter;
    SharedPreferences sharedPreferences;
    TaskDatabaseHelper dbHelper;
    String username;
    String selectedStatus = "All";
    String searchTerm = "";
    ImageButton settingsButton;
    TextView emptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // UI references
        greetingTextView = findViewById(R.id.greetingTextView);
        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);
        logoutButton = findViewById(R.id.logoutButton);
        searchEditText = findViewById(R.id.searchEditText);
        statusFilterSpinner = findViewById(R.id.statusFilterSpinner);
        settingsButton = findViewById(R.id.settingsButton);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        dbHelper = new TaskDatabaseHelper(this);
        username = sharedPreferences.getString("username", "");
        greetingTextView.setText("Welcome " + username + "!");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(this, dbHelper.getTasks(username, selectedStatus, searchTerm), dbHelper, username);
        recyclerView.setAdapter(adapter);

        // Search bar
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchTerm = s.toString();
                refreshTasks();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Status filter spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.status_filter_array, R.layout.spinner_item_status);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item_status);
        statusFilterSpinner.setAdapter(spinnerAdapter);
        statusFilterSpinner.setSelection(0);
        statusFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStatus = parent.getItemAtPosition(position).toString();
                refreshTasks();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // FAB
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, AddTaskActivity.class);
            // Use ActivityResultLauncher instead of deprecated startActivityForResult
            addTaskLauncher.launch(intent);
        });

        // Logout
        logoutButton.setOnClickListener(v -> {
            sharedPreferences.edit().clear().apply();
            Intent intent = new Intent(HomeScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Settings
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        applyFontSize();
    }

    // Add ActivityResultLauncher for AddTaskActivity
    private final androidx.activity.result.ActivityResultLauncher<Intent> addTaskLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().hasExtra("task_result")) {
                    String res = result.getData().getStringExtra("task_result");
                    if (res != null) {
                        String msg = "Task saved successfully";
                        if ("updated".equals(res)) msg = "Task updated successfully";
                        com.google.android.material.snackbar.Snackbar snackbar = com.google.android.material.snackbar.Snackbar.make(findViewById(android.R.id.content), msg, com.google.android.material.snackbar.Snackbar.LENGTH_LONG);
                        snackbar.setBackgroundTint(android.graphics.Color.parseColor("#222222"));
                        snackbar.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                        snackbar.show();
                    }
                }
            });

    // Expose the ActivityResultLauncher for editing tasks
    public final androidx.activity.result.ActivityResultLauncher<Intent> editTaskLauncher = addTaskLauncher;

    @Override
    protected void onResume() {
        super.onResume();
        applyFontSize();
        refreshTasks();
    }

    private void refreshTasks() {
        List<Task> tasks = dbHelper.getTasks(username, selectedStatus, searchTerm);
        adapter.setTasks(tasks);
        updateEmptyState(tasks);
    }

    // Change updateEmptyState to public so TaskAdapter can call it
    @SuppressLint("SetTextI18n")
    public void updateEmptyState(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            emptyStateTextView.setVisibility(View.VISIBLE);
            if (!searchTerm.isEmpty()) {
                emptyStateTextView.setText("No tasks match your search.");
            } else if (!"All".equals(selectedStatus)) {
                if ("Completed".equals(selectedStatus)) {
                    emptyStateTextView.setText("No completed tasks found.");
                } else if ("In Progress".equals(selectedStatus)) {
                    emptyStateTextView.setText("No in-progress tasks found.");
                } else if ("Pending".equals(selectedStatus)) {
                    emptyStateTextView.setText("No pending tasks found.");
                } else {
                    emptyStateTextView.setText("No tasks found for this filter.");
                }
            } else {
                emptyStateTextView.setText("No tasks yet. Add your first task!");
            }
        } else {
            emptyStateTextView.setVisibility(View.GONE);
        }
    }

    private void applyFontSize() {
        String fontSize = sharedPreferences.getString("font_size", "Medium");
        float sizeSp = 16f;
        if ("Small".equals(fontSize)) sizeSp = 14f;
        else if ("Large".equals(fontSize)) sizeSp = 20f;
        greetingTextView.setTextSize(sizeSp + 6);
        searchEditText.setTextSize(sizeSp);
        logoutButton.setTextSize(sizeSp);
        if (adapter != null) {
            adapter.setFontSize(sizeSp);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
