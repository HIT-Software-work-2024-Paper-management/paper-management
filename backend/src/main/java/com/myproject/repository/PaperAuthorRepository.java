package com.myproject.repository;

import com.myproject.model.PaperAuthor;
import com.myproject.model.Paper;
import com.myproject.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface PaperAuthorRepository extends JpaRepository<PaperAuthor, Long> {
    List<PaperAuthor> findByPaper(Paper paper);

    @Query("SELECT pa FROM PaperAuthor pa WHERE pa.paper.id = :paperId")
    List<PaperAuthor> findAuthorsByPaperId(@Param("paperId") Long paperId);
}
