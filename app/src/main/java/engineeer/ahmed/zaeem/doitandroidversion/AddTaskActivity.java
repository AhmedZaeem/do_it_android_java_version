package engineeer.ahmed.zaeem.doitandroidversion;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Button;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddTaskActivity extends AppCompatActivity {
    TextInputEditText titleEditText, descriptionEditText, categoryEditText, startDateEditText, endDateEditText;
    Spinner statusSpinner, categorySpinner;
    MaterialButton saveButton, addCategoryButton;
    TextView titleTextView;
    SharedPreferences sharedPreferences;
    TaskDatabaseHelper dbHelper;
    UserDatabaseHelper userDbHelper;
    String username;
    Calendar calendar = Calendar.getInstance();
    boolean isEditMode = false;
    int editingTaskId = -1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        titleEditText = findViewById(R.id.editTextTitle);
        descriptionEditText = findViewById(R.id.editTextDescription);
        startDateEditText = findViewById(R.id.editTextStartDate);
        endDateEditText = findViewById(R.id.editTextEndDate);
        statusSpinner = findViewById(R.id.spinnerStatus);
        saveButton = findViewById(R.id.buttonSave);
        titleTextView = findViewById(R.id.textViewTitle);
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        dbHelper = new TaskDatabaseHelper(this);
        userDbHelper = new UserDatabaseHelper(this);
        username = sharedPreferences.getString("username", "");
        categorySpinner = findViewById(R.id.spinnerCategory);
        addCategoryButton = findViewById(R.id.buttonAddCategory);

        // Category Spinner logic
        List<String> categories = dbHelper.getAllCategories();
        if (categories.isEmpty()) {
            categories.add("General");
            dbHelper.insertCategory("General");
        }
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        addCategoryButton.setOnClickListener(v -> {
            // Custom dialog for adding a category
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
            final android.widget.EditText input = dialogView.findViewById(R.id.editTextNewCategory);
            androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.CustomDialog)
                    .setView(dialogView)
                    .setCancelable(true)
                    .create();
            dialogView.findViewById(R.id.buttonCancelCategory).setOnClickListener(view -> dialog.dismiss());
            dialogView.findViewById(R.id.buttonAddCategoryConfirm).setOnClickListener(view -> {
                String newCategory = input.getText().toString().trim();
                if (newCategory.isEmpty()) {
                    input.setError("Category name cannot be empty");
                    return;
                }
                if (dbHelper.categoryExists(newCategory)) {
                    input.setError("Category already exists");
                    return;
                }
                dbHelper.insertCategory(newCategory);
                categories.clear();
                categories.addAll(dbHelper.getAllCategories());
                categoryAdapter.notifyDataSetChanged();
                int pos = categoryAdapter.getPosition(newCategory);
                if (pos >= 0) categorySpinner.setSelection(pos);
                dialog.dismiss();
                com.google.android.material.snackbar.Snackbar.make(addCategoryButton, "Category added", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show();
            });
            dialog.show();
        });
        // Remove "All" from status options for this screen
        String[] statusArray = getResources().getStringArray(R.array.status_array);
        String[] filteredStatusArray;
        if (statusArray.length > 0 && statusArray[0].equalsIgnoreCase("All")) {
            filteredStatusArray = new String[statusArray.length - 1];
            System.arraycopy(statusArray, 1, filteredStatusArray, 0, statusArray.length - 1);
        } else {
            filteredStatusArray = statusArray;
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filteredStatusArray);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(spinnerAdapter);

        // Date pickers for both start and end date
        View.OnClickListener dateClickListener = v -> {
            final TextInputEditText target = (TextInputEditText) v;
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(AddTaskActivity.this, (view, y, m, d) -> {
                String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d);
                target.setText(date);
            }, year, month, day);
            dialog.show();
        };
        startDateEditText.setOnClickListener(dateClickListener);
        endDateEditText.setOnClickListener(dateClickListener);

        // Check for edit mode
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("task_id")) {
            isEditMode = true;
            editingTaskId = intent.getIntExtra("task_id", -1);
            Task task = dbHelper.getTaskById(editingTaskId);
            if (task != null) {
                titleEditText.setText(task.getTitle());
                descriptionEditText.setText(task.getDescription());
                startDateEditText.setText(task.getStartDate());
                endDateEditText.setText(task.getEndDate());
                int statusPos = spinnerAdapter.getPosition(task.getStatus());
                statusSpinner.setSelection(statusPos);
                // Set the category spinner to the task's category
                int catPos = categoryAdapter.getPosition(task.getCategory());
                if (catPos >= 0) categorySpinner.setSelection(catPos);
                // Make start and end date read-only in edit mode
                startDateEditText.setFocusable(false);
                startDateEditText.setClickable(false);
                endDateEditText.setFocusable(false);
                endDateEditText.setClickable(false);
            }
            titleTextView.setText("Edit Task");
            saveButton.setText("Update");
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Edit Task");
        } else {
            titleTextView.setText("Add Task");
            saveButton.setText("Save");
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Add Task");
        }

        saveButton.setOnClickListener(v -> {
            String title = Objects.requireNonNull(titleEditText.getText()).toString();
            String description = Objects.requireNonNull(descriptionEditText.getText()).toString();
            String status = statusSpinner.getSelectedItem().toString();
            String category = categorySpinner.getSelectedItem() != null ? categorySpinner.getSelectedItem().toString() : "General";
            String startDate = Objects.requireNonNull(startDateEditText.getText()).toString();
            String endDate = Objects.requireNonNull(endDateEditText.getText()).toString();
            if (title.isEmpty() || status.isEmpty()|| startDate.isEmpty() || endDate.isEmpty()) {
                com.google.android.material.snackbar.Snackbar snackbar = com.google.android.material.snackbar.Snackbar.make(saveButton, "All fields are required", com.google.android.material.snackbar.Snackbar.LENGTH_LONG);
                snackbar.setBackgroundTint(android.graphics.Color.parseColor("#222222"));
                snackbar.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                snackbar.show();
                return;
            }
            String publishedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            boolean updated = false;
            if (isEditMode) {
                Task updatedTask = new Task(editingTaskId, username, title, description, status, publishedDate, startDate, endDate, category);
                dbHelper.updateTask(updatedTask);
                updated = true;
            } else {
                Task task = new Task(0, username, title, description, status, publishedDate, startDate, endDate, category);
                dbHelper.insertTask(task);
            }
            Intent resultIntent = new Intent();
            resultIntent.putExtra("task_result", updated ? "updated" : "saved");
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // Make start and end date read-only in edit mode (robust)
        if (isEditMode) {
            startDateEditText.setFocusable(false);
            startDateEditText.setFocusableInTouchMode(false);
            startDateEditText.setClickable(false);
            startDateEditText.setLongClickable(false);
            startDateEditText.setCursorVisible(false);
            startDateEditText.setKeyListener(null);
            startDateEditText.setOnClickListener(null);
            endDateEditText.setFocusable(false);
            endDateEditText.setFocusableInTouchMode(false);
            endDateEditText.setClickable(false);
            endDateEditText.setLongClickable(false);
            endDateEditText.setCursorVisible(false);
            endDateEditText.setKeyListener(null);
            endDateEditText.setOnClickListener(null);
        }
        ImageButton backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());
        applyFontSize();
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
        titleEditText.setTextSize(sizeSp);
        descriptionEditText.setTextSize(sizeSp);
//        categoryEditText.setTextSize(sizeSp);
        startDateEditText.setTextSize(sizeSp);
        endDateEditText.setTextSize(sizeSp);
        saveButton.setTextSize(sizeSp);
        // Add more views as needed
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
