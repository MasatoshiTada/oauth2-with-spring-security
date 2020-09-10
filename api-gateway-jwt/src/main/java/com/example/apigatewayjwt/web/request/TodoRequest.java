package com.example.apigatewayjwt.web.request;

import java.time.LocalDate;

public class TodoRequest {

    private String description;

    private LocalDate deadline;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
}
