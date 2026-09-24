package com.noteapp.service;

import com.noteapp.dto.DocSummary;
import com.noteapp.entity.Doc;
import com.noteapp.repository.DocRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DocService {

    private final DocRepository repo;

    public DocService(DocRepository repo) {
        this.repo = repo;
    }

    public List<DocSummary> getSummaries(String keyword) {
        List<Doc> docs;
        if (keyword != null && !keyword.trim().isEmpty()) {
            docs = repo.findByTitleContainingIgnoreCaseOrderByUpdatedAtDesc(keyword);
        } else {
            docs = repo.findAll(Sort.by("sortOrder").ascending().and(Sort.by("id").ascending()));
        }
        return docs.stream().map(DocSummary::from).collect(Collectors.toList());
    }

    public Optional<Doc> getById(Long id) {
        return repo.findById(id);
    }

    public Doc create(Doc doc) {
        doc.setId(null);
        if (doc.getIsFolder() == null) doc.setIsFolder(false);
        if (doc.getParentId() != null) {
            repo.findById(doc.getParentId())
                .orElseThrow(() -> new RuntimeException("Parent not found: " + doc.getParentId()));
        }
        if (doc.getSortOrder() == null || doc.getSortOrder() == 0) {
            doc.setSortOrder(nextSortOrder(doc.getParentId()));
        }
        if (Boolean.TRUE.equals(doc.getIsFolder())) {
            doc.setContent(null);
        } else if (doc.getContent() == null) {
            doc.setContent("");
        }
        return repo.save(doc);
    }

    public Doc update(Long id, Doc details) {
        Doc doc = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Doc not found: " + id));
        if (details.getTitle() != null && !details.getTitle().trim().isEmpty()) {
            doc.setTitle(details.getTitle());
        }
        if (!Boolean.TRUE.equals(doc.getIsFolder()) && details.getContent() != null) {
            doc.setContent(details.getContent());
        }
        return repo.save(doc);
    }

    public Doc move(Long id, Long newParentId, Integer sortOrder) {
        Doc doc = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Doc not found: " + id));
        if (newParentId != null) {
            if (newParentId.equals(id)) {
                throw new IllegalArgumentException("Cannot move a node into itself");
            }
            if (isDescendant(newParentId, id)) {
                throw new IllegalArgumentException("Cannot move a node into its own descendant");
            }
            Doc parent = repo.findById(newParentId)
                .orElseThrow(() -> new RuntimeException("Parent not found: " + newParentId));
            if (!Boolean.TRUE.equals(parent.getIsFolder())) {
                throw new IllegalArgumentException("Target parent is not a folder");
            }
        }
        doc.setParentId(newParentId);
        if (sortOrder != null) doc.setSortOrder(sortOrder);
        return repo.save(doc);
    }

    public void delete(Long id) {
        Doc doc = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Doc not found: " + id));
        List<Long> ids = new ArrayList<>();
        collectIds(doc, ids);
        repo.deleteAll(repo.findAllById(ids));
    }

    private void collectIds(Doc doc, List<Long> ids) {
        ids.add(doc.getId());
        if (Boolean.TRUE.equals(doc.getIsFolder())) {
            for (Doc child : repo.findByParentIdOrderBySortOrderAscIdAsc(doc.getId())) {
                collectIds(child, ids);
            }
        }
    }

    private boolean isDescendant(Long candidateId, Long ancestorId) {
        Long current = candidateId;
        int guard = 0;
        while (current != null && guard++ < 1000) {
            if (current.equals(ancestorId)) return true;
            Optional<Doc> d = repo.findById(current);
            if (!d.isPresent()) return false;
            current = d.get().getParentId();
        }
        return false;
    }

    private int nextSortOrder(Long parentId) {
        List<Doc> siblings = parentId == null
            ? repo.findAll()
            : repo.findByParentIdOrderBySortOrderAscIdAsc(parentId);
        int max = 0;
        for (Doc d : siblings) {
            if (parentId == null && d.getParentId() != null) continue;
            if (d.getSortOrder() != null && d.getSortOrder() > max) max = d.getSortOrder();
        }
        return max + 1;
    }
}
