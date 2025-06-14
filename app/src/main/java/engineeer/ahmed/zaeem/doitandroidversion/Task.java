package engineeer.ahmed.zaeem.doitandroidversion;

public class Task {
    private final int id;
    private final String username;
    private String title;
    private String description;
    private String status;
    private String category;
    private final String publishedDate;
    private String startDate;
    private String endDate;

    public Task(int id, String username, String title, String description, String status, String publishedDate, String startDate, String endDate, String category) {
        this.id = id;
        this.username = username;
        this.title = title;
        this.description = description;
        this.status = status;
        this.publishedDate = publishedDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category = category;
    }
    public Task(int id, String username, String title, String description, String status, String publishedDate, String startDate, String endDate) {
        this(id, username, title, description, status, publishedDate, startDate, endDate, "General");
    }
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getCategory() { return category; }
    public String getPublishedDate() { return publishedDate; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void setCategory(String category) { this.category = category; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}

