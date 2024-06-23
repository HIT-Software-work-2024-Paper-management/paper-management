package com.myproject.service;

import com.myproject.model.Author;
import com.myproject.model.Journal;
import com.myproject.model.Paper;
import com.myproject.model.PaperAuthor;
import com.myproject.repository.AuthorRepository;
import com.myproject.repository.JournalRepository;
import com.myproject.repository.PaperAuthorRepository;
import com.myproject.repository.PaperRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaperService {
    private static final Logger logger = LoggerFactory.getLogger(PaperService.class);

    @Autowired
    private PaperRepository paperRepository;

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private PaperAuthorRepository paperAuthorRepository;

    public Paper savePaper(Paper paper, List<PaperAuthor> paperAuthors) {
        Journal journal = journalRepository.findByName(paper.getJournal().getName());
        if (journal != null) {
            paper.setJournal(journal);
        } else {
            journal = paper.getJournal();
            journalRepository.save(journal);
        }

        Paper savedPaper = paperRepository.save(paper);

        for (PaperAuthor paperAuthor : paperAuthors) {
            paperAuthor.setPaper(savedPaper);
            paperAuthorRepository.save(paperAuthor);
        }


        return savedPaper;
    }

    public List<Paper> getAllPapers() {
        return paperRepository.findAll();
    }

    public Optional<Paper> getPaperById(Long id) {
        return paperRepository.findById(id);
    }


    public void deletePaper(Long id) {
        Optional<Paper> paperOptional = paperRepository.findById(id);
        if (paperOptional.isPresent()) {
            Paper paper = paperOptional.get();
            logger.info("Deleting related PaperAuthor records for paper ID: {}", paper.getId());

            // 删除相关的 PaperAuthor 记录
            List<PaperAuthor> paperAuthors = paper.getPaperAuthors();
            if (paperAuthors != null && !paperAuthors.isEmpty()) {
                for (PaperAuthor paperAuthor : paperAuthors) {
                    try {
                        paperAuthorRepository.delete(paperAuthor);
                        logger.info("Deleted PaperAuthor ID: {}", paperAuthor.getId());
                    } catch (Exception e) {
                        logger.error("Error deleting PaperAuthor ID: {}", paperAuthor.getId(), e);
                        throw e;
                    }
                }

                logger.info("Associated PaperAuthors:");
                for (PaperAuthor paperAuthor : paperAuthors) {
                    logger.info("PaperAuthor ID: {}, Author ID: {}", paperAuthor.getId(), paperAuthor.getAuthor().getId());
                }
            } else {
                logger.info("No associated PaperAuthors found.");
            }

            // 删除文件
            String fileUrl = paper.getFileUrl();
            File file = new File(fileUrl);
            if (file.exists()) {
                logger.info("Deleting file: {}", fileUrl);
                boolean deleted = file.delete();
                if (deleted) {
                    logger.info("File deleted successfully");
                } else {
                    logger.error("Failed to delete file");
                }
            } else {
                logger.warn("File not found: {}", fileUrl);
            }

            // 删除数据库记录
            try {
                paperRepository.delete(paper);
            } catch (Exception e) {
                logger.error("Error deleting paper with ID: {}", id, e);
                throw e;
            }
        } else {
            logger.warn("Paper with ID {} not found", id);
        }
    }


    public Paper updatePaper(Long id, Paper updatedPaper) {
        return paperRepository.findById(id).map(paper -> {
            paper.setTitle(updatedPaper.getTitle());
            paper.setAuthors(updatedPaper.getAuthors());
            paper.setDate(updatedPaper.getDate());
            paper.setJournal(updateJournal(updatedPaper.getJournal()));
            paper.setCategory(updatedPaper.getCategory());
            paper.setFileUrl(updatedPaper.getFileUrl());
            paper.setType(updatedPaper.getType());
            return paperRepository.save(paper);
        }).orElseGet(() -> {
            updatedPaper.setId(id);
            updatedPaper.setJournal(updateJournal(updatedPaper.getJournal()));
            return paperRepository.save(updatedPaper);
        });
    }

    private Journal updateJournal(Journal journal) {
        Journal existingJournal = journalRepository.findByName(journal.getName());
        if (existingJournal != null) {
            return existingJournal;
        } else {
            return journalRepository.save(journal);
        }
    }

    public List<Paper> searchPapers(Specification<Paper> spec) {
        List<Paper> results = paperRepository.findAll(spec);

        System.out.println("paperserver Search results: " + results.size());
        results.forEach(paper -> System.out.println(paper.getTitle()));
        return results;
    }

    public ByteArrayInputStream exportPapersToExcel(List<Paper> papers) throws IOException {
        String[] columns = {"Title", "Authors", "Keywords", "Date", "Journal", "Category", "Type"};

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Papers");

            // Header
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < columns.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columns[col]);
            }

            // Data
            int rowIdx = 1;
            for (Paper paper : papers) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(paper.getTitle());
                row.createCell(1).setCellValue(paper.getAuthors().stream()
                        .map(Author::getName)
                        .reduce((a, b) -> a + "; " + b).orElse(""));
                row.createCell(2).setCellValue(paper.getDate().toString());
                row.createCell(3).setCellValue(paper.getJournal().getName());
                row.createCell(4).setCellValue(paper.getCategory().getName());
                row.createCell(5).setCellValue(paper.getType());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public Set<String> getCoAuthors(String authorName) {
        List<Paper> papers = paperRepository.findByAuthorsContaining(authorName);
        Set<String> coAuthors = new HashSet<>();

        for (Paper paper : papers) {
            for (Author author : paper.getAuthors()) {
                if (!author.getName().equals(authorName)) {
                    coAuthors.add(author.getName());
                }
            }
        }

        return coAuthors;
    }

    public Author saveAuthor(Author author) {
        Optional<Author> existingAuthor = authorRepository.findById(author.getId());
        if (existingAuthor.isPresent()) {
            return existingAuthor.get();
        } else {
            return authorRepository.save(author);
        }
    }

    public Optional<Author> getAuthorById(Long id) {
        return authorRepository.findById(id);
    }





    //以下用于论文关系网络
    public List<Paper> findPapersByAuthorName(String authorName) {
        return paperRepository.findByAuthorsName(authorName);
    }

    public List<PaperAuthor> findAuthorsByPaperId(Long paperId) {
        return paperAuthorRepository.findAuthorsByPaperId(paperId);
    }
    //以下用于计算工作量分数
    public Paper findPaperByTitle(String title) {
        return paperRepository.findByTitle(title);
    }
    public PaperAuthor findPaperAuthorByPaperAndAuthorName(Paper paper, String authorName) {
        return paper.getPaperAuthors().stream()
                .filter(pa -> pa.getAuthor().getName().equals(authorName))
                .findFirst()
                .orElse(null);
    }

    public double calculateWorkloadScore(Paper paper, String authorName) {
        PaperAuthor paperAuthor = findPaperAuthorByPaperAndAuthorName(paper, authorName);
        if (paperAuthor == null) {
            throw new IllegalArgumentException("Author not found in the paper");
        }
        return calculateWorkloadScore(paper, paperAuthor);
    }

    private double calculateWorkloadScore(Paper paper, PaperAuthor paperAuthor) {
        double impactFactor = paper.getImpactFactor();
        double rankFactor = getRankMultiplier(paperAuthor.getRank());
        double journalWeight = paper.getJournal().getWeight();
        return impactFactor * journalWeight * rankFactor;
    }

    private double getRankMultiplier(int authorRank) {
        if (authorRank == 1) {
            return 1.5;
        } else if (authorRank == 2) {
            return 1.2;
        } else {
            return 1.0;
        }
    }
}
