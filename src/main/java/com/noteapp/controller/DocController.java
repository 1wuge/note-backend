package com.noteapp.controller;

import com.noteapp.dto.DocSummary;
import com.noteapp.dto.MoveRequest;
import com.noteapp.entity.Doc;
import com.noteapp.service.DocService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/docs")
public class DocController {

    private final DocService docService;

    public DocController(DocService docService) {
        this.docService = docService;
    }

    @GetMapping
    public ResponseEntity<List<DocSummary>> getSummaries(
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(docService.getSummaries(keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doc> getById(@PathVariable Long id) {
        return docService.getById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Doc> create(@Valid @RequestBody Doc doc) {
        return ResponseEntity.status(HttpStatus.CREATED).body(docService.create(doc));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doc> update(@PathVariable Long id, @RequestBody Doc doc) {
        return ResponseEntity.ok(docService.update(id, doc));
    }

    @PutMapping("/{id}/move")
    public ResponseEntity<Doc> move(@PathVariable Long id, @RequestBody MoveRequest req) {
        return ResponseEntity.ok(docService.move(id, req.getParentId(), req.getSortOrder()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        docService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
