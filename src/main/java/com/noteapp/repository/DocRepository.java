package com.noteapp.repository;

import com.noteapp.entity.Doc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocRepository extends JpaRepository<Doc, Long> {
    List<Doc> findByParentIdOrderBySortOrderAscIdAsc(Long parentId);
    List<Doc> findByTitleContainingIgnoreCaseOrderByUpdatedAtDesc(String keyword);
}
