package com.noteapp.dto;

import com.noteapp.entity.Doc;

import java.time.LocalDateTime;

public class DocSummary {
    private Long id;
    private String title;
    private Long parentId;
    private Boolean isFolder;
    private Integer sortOrder;
    private LocalDateTime updatedAt;

    public DocSummary() {}

    public static DocSummary from(Doc doc) {
        DocSummary s = new DocSummary();
        s.id = doc.getId();
        s.title = doc.getTitle();
        s.parentId = doc.getParentId();
        s.isFolder = doc.getIsFolder();
        s.sortOrder = doc.getSortOrder();
        s.updatedAt = doc.getUpdatedAt();
        return s;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Long getParentId() { return parentId; }
    public Boolean getIsFolder() { return isFolder; }
    public Integer getSortOrder() { return sortOrder; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
