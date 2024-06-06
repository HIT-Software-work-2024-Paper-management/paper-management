package com.myproject.repository;

import com.myproject.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
@Repository
public interface PaperRepository extends JpaRepository<Paper, Long> {
    List<Paper> findByAuthorsContaining(String authorName);
}