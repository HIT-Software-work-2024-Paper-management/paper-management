package com.myproject.service;

import com.myproject.model.Paper;
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
import java.util.*;
import java.util.stream.Collectors;

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
        Optional<Paper> paperOptional = paperRepository.findById(id);
        if (paperOptional.isPresent()) {
            Paper paper = paperOptional.get();
            // 删除文件
            String fileUrl = paper.getFileUrl(); // 假设文件路径存储在 filePath 字段中
            File file = new File(fileUrl);
            if (file.exists()) {
                file.delete();
            }
            // 删除数据库记录
            paperRepository.deleteById(id);
        }
    }

    public Paper updatePaper(Long id, Paper updatedPaper) {
        return paperRepository.findById(id).map(paper -> {
            paper.setTitle(updatedPaper.getTitle());
            paper.setAuthors(updatedPaper.getAuthors());
            paper.setDate(updatedPaper.getDate());
            paper.setJournal(updatedPaper.getJournal());
            paper.setCategory(updatedPaper.getCategory());
            paper.setFileUrl(updatedPaper.getFileUrl());
            paper.setType(updatedPaper.getType());
            paper.setKeywords(updatedPaper.getKeywords());
            return paperRepository.save(paper);
        }).orElseGet(() -> {
            updatedPaper.setId(id);
            return paperRepository.save(updatedPaper);
        });
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
                row.createCell(1).setCellValue(paper.getAuthors()); // 修改为直接使用字符串
                row.createCell(2).setCellValue(paper.getKeywords());
                row.createCell(3).setCellValue(paper.getDate().toString());
                row.createCell(4).setCellValue(paper.getJournal());
                row.createCell(5).setCellValue(paper.getCategory().getName());
                row.createCell(6).setCellValue(paper.getType());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public Map<String, Set<String>> getCoAuthors(String authorName) {
        List<Paper> papers = paperRepository.findByAuthorsContaining(authorName);
        Map<String, Set<String>> coAuthorMap = new HashMap<>();

        for (Paper paper : papers) {
            String[] authorsArray = paper.getAuthors().split(";");
            for (String author : authorsArray) {
                if (!author.trim().equals(authorName)) {
                    coAuthorMap.computeIfAbsent(authorName, k -> new HashSet<>()).add(author.trim());
                }
            }
        }

        return coAuthorMap;
    }

}
