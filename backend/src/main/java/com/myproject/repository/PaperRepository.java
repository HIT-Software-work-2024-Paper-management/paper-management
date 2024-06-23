package com.myproject.repository;

import com.myproject.model.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

import java.util.List;

@Repository
public interface PaperRepository extends JpaRepository<Paper, Long>, JpaSpecificationExecutor<Paper> {
    List<Paper> findByAuthorsContaining(String authorName);

    @Query("SELECT p FROM Paper p JOIN p.paperAuthors pa JOIN pa.author a WHERE a.name LIKE %:authorName%")
    List<Paper> findByAuthorsName(@Param("authorName") String authorName);

    Paper findByTitle(String title);
}
