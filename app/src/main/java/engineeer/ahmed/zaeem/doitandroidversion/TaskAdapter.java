package engineeer.ahmed.zaeem.doitandroidversion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;
import java.util.Objects;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks;
    private final Context context;
    private final TaskDatabaseHelper dbHelper;
    private final String username;
    private float fontSizeSp = 16f;
    private Task recentlyDeletedTask;
    private int recentlyDeletedTaskPosition;

    public TaskAdapter(Context context, List<Task> tasks, TaskDatabaseHelper dbHelper, String username) {
        this.context = context;
        this.tasks = tasks;
        this.dbHelper = dbHelper;
        this.username = username;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
        // Notify the activity to update the empty state view immediately
        if (context instanceof HomeScreenActivity) {
            ((HomeScreenActivity) context).updateEmptyState(tasks);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFontSize(float sizeSp) {
        this.fontSizeSp = sizeSp;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title, category, status;
        View options;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.taskTitle);
            category = itemView.findViewById(R.id.taskCategory);
            status = itemView.findViewById(R.id.taskStatus);
            options = itemView.findViewById(R.id.taskOptions);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.title.setText(task.getTitle());
        holder.status.setText(task.getStatus());
        holder.title.setTextSize(fontSizeSp);
        holder.category.setTextSize(fontSizeSp);
        holder.status.setTextSize(fontSizeSp);
        holder.category.setText(task.getCategory());
        holder.itemView.setBackgroundResource(R.drawable.card_glow_bg);
        // Set description
        TextView descView = holder.itemView.findViewById(R.id.taskDescription);
        if (descView != null) {
            descView.setText(task.getDescription());
        }
        // Set status bar color based on status
        View statusBar = holder.itemView.findViewById(R.id.statusBar);
        switch (task.getStatus()) {
            case "Completed":
                statusBar.setBackgroundColor(Color.parseColor("#4CAF50")); // Green
                break;
            case "In Progress":
                statusBar.setBackgroundColor(Color.parseColor("#2196F3")); // Blue
                break;
            case "Pending":
                statusBar.setBackgroundColor(Color.parseColor("#B0B0B0")); // Gray
                break;
            default:
                statusBar.setBackgroundColor(Color.TRANSPARENT);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddTaskActivity.class);
            intent.putExtra("task_id", task.getId());
            if (context instanceof HomeScreenActivity) {
                ((HomeScreenActivity) context).editTaskLauncher.launch(intent);
            } else {
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onLongClick(View v) {
                // Prevent multiple dialogs by checking if a dialog is already showing
                if (v.getTag() != null && v.getTag() instanceof androidx.appcompat.app.AlertDialog) {
                    androidx.appcompat.app.AlertDialog existingDialog = (androidx.appcompat.app.AlertDialog) v.getTag();
                    if (existingDialog.isShowing()) return true;
                }
                View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_delete_task, null);
                TextView title = dialogView.findViewById(R.id.dialogDeleteTitle);
                TextView message = dialogView.findViewById(R.id.dialogDeleteMessage);
                title.setText("Delete Task");
                message.setText("Are you sure you want to delete this task?");
                Button deleteBtn = dialogView.findViewById(R.id.buttonConfirmDelete);
                // Remove all backgrounds and set only the custom drawable
                deleteBtn.setBackgroundResource(R.drawable.bg_button_delete);
                deleteBtn.setTextColor(Color.WHITE);
                deleteBtn.setAllCaps(false);
                // Remove default outline/background for Material/Android buttons
                deleteBtn.setStateListAnimator(null);
                androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.CustomDialog)
                        .setView(dialogView)
                        .setCancelable(true)
                        .create();
                v.setTag(dialog);
                dialog.setOnDismissListener(d -> v.setTag(null));
                dialogView.findViewById(R.id.buttonCancelDelete).setOnClickListener(view -> dialog.dismiss());
                deleteBtn.setOnClickListener(view -> {
                    recentlyDeletedTask = task;
                    recentlyDeletedTaskPosition = holder.getAdapterPosition();
                    dbHelper.deleteTask(task.getId());
                    setTasks(dbHelper.getTasks(username, "All", ""));
                    dialog.dismiss();
                    Snackbar snackbar = Snackbar.make(holder.itemView, "Task removed successfully", Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.parseColor("#222222"));
                    snackbar.setTextColor(Color.parseColor("#FFFFFF"));
                    snackbar.setAction("UNDO", v1 -> {
                        if (recentlyDeletedTask != null) {
                            dbHelper.insertTask(recentlyDeletedTask);
                            setTasks(dbHelper.getTasks(username, "All", ""));
                            recentlyDeletedTask = null;
                        }
                    });
                    snackbar.setActionTextColor(Color.parseColor("#F44336"));
                    snackbar.show();
                });
                dialog.show();
                return true;
            }
        });
        // Add 3-dots menu logic
        holder.options.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                android.widget.PopupMenu popup = new android.widget.PopupMenu(context, v);
                popup.getMenu().add("Edit");
                popup.getMenu().add("Delete");
                popup.setOnMenuItemClickListener(item -> {
                    if (Objects.equals(item.getTitle(), "Edit")) {
                        Intent intent = new Intent(context, AddTaskActivity.class);
                        intent.putExtra("task_id", task.getId());
                        if (context instanceof HomeScreenActivity) {
                            ((HomeScreenActivity) context).editTaskLauncher.launch(intent);
                        } else {
                            context.startActivity(intent);
                        }
                        return true;
                    } else if (Objects.equals(item.getTitle(), "Delete")) {
                        // Show custom delete dialog directly (do NOT call performLongClick)
                        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_delete_task, null);
                        TextView title = dialogView.findViewById(R.id.dialogDeleteTitle);
                        TextView message = dialogView.findViewById(R.id.dialogDeleteMessage);
                        title.setText("Delete Task");
                        message.setText("Are you sure you want to delete this task?");
                        Button deleteBtn = dialogView.findViewById(R.id.buttonConfirmDelete);
                        deleteBtn.setBackgroundResource(R.drawable.bg_button_delete);
                        deleteBtn.setTextColor(Color.WHITE);
                        deleteBtn.setAllCaps(false);
                        deleteBtn.setStateListAnimator(null);
                        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.CustomDialog)
                                .setView(dialogView)
                                .setCancelable(true)
                                .create();
                        dialogView.findViewById(R.id.buttonCancelDelete).setOnClickListener(view -> dialog.dismiss());
                        deleteBtn.setOnClickListener(view -> {
                            recentlyDeletedTask = task;
                            recentlyDeletedTaskPosition = holder.getAdapterPosition();
                            dbHelper.deleteTask(task.getId());
                            setTasks(dbHelper.getTasks(username, "All", ""));
                            dialog.dismiss();
                            Snackbar snackbar = Snackbar.make(holder.itemView, "Task removed successfully", Snackbar.LENGTH_LONG);
                            snackbar.setBackgroundTint(Color.parseColor("#222222"));
                            snackbar.setTextColor(Color.parseColor("#FFFFFF"));
                            snackbar.setAction("UNDO", v1 -> {
                                if (recentlyDeletedTask != null) {
                                    dbHelper.insertTask(recentlyDeletedTask);
                                    setTasks(dbHelper.getTasks(username, "All", ""));
                                    recentlyDeletedTask = null;
                                }
                            });
                            snackbar.setActionTextColor(Color.parseColor("#F44336"));
                            snackbar.show();
                        });
                        dialog.show();
                        return true;
                    }
                    return false;
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
