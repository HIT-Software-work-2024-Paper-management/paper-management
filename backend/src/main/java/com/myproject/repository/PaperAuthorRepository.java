package com.myproject.repository;

import com.myproject.model.PaperAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaperAuthorRepository extends JpaRepository<PaperAuthor, Long> {
}
