package com.myproject.service;

import com.myproject.model.Paper;
import com.myproject.repository.PaperRepository;
import com.myproject.specification.PaperSpecification;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PaperService {

    @Autowired
    private PaperRepository paperRepository;

    public Paper savePaper(Paper paper) {
        return paperRepository.save(paper);
    }

    public List<Paper> getAllPapers() {
        return paperRepository.findAll();
    }

    public Optional<Paper> getPaperById(Long id) {
        return paperRepository.findById(id);
    }

    public void deletePaper(Long id) {
        paperRepository.deleteById(id);
    }

    public Paper updatePaper(Long id, Paper updatedPaper) {
        return paperRepository.findById(id).map(paper -> {
            paper.setTitle(updatedPaper.getTitle());
            paper.setAuthor(updatedPaper.getAuthor());
            paper.setDate(updatedPaper.getDate());
            paper.setJournal(updatedPaper.getJournal());
            paper.setCategory(updatedPaper.getCategory());
            paper.setFileUrl(updatedPaper.getFileUrl());
            paper.setKeywords(updatedPaper.getKeywords());
            return paperRepository.save(paper);
        }).orElseGet(() -> {
            updatedPaper.setId(id);
            return paperRepository.save(updatedPaper);
        });
    }

    public List<Paper> searchPapers(Specification<Paper> spec) {
        return paperRepository.findAll(spec);
    }

    public ByteArrayInputStream exportPapersToExcel(List<Paper> papers) throws IOException {
        String[] columns = {"Title", "Author", "Keywords", "Date", "Journal", "Category"};

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
                row.createCell(1).setCellValue(paper.getAuthor());
                row.createCell(2).setCellValue(paper.getKeywords());
                row.createCell(3).setCellValue(paper.getDate().toString());
                row.createCell(4).setCellValue(paper.getJournal());
                row.createCell(5).setCellValue(paper.getCategory().getName());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
