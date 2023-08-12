package com.lk.individual_project;

public class Report {
    private String reportId;
    private String imageUrl;
    private String description;

    public Report() {
    }

    public Report(String reportId, String imageUrl, String description) {
        this.reportId = reportId;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
