package com.esecure.securetask.dto;

import com.esecure.securetask.model.IssueStatus;
import jakarta.validation.constraints.NotNull;

public class IssueStatusUpdate {
    @NotNull
    private IssueStatus status;

    public IssueStatus getStatus(){ return status; }
    public void setStatus(IssueStatus status){ this.status = status; }
}
