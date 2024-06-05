package com.myproject.controller;

import com.myproject.model.Paper;
import com.myproject.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/papers")
public class PaperController {

    @Autowired
    private PaperService paperService;

    @PostMapping
    public ResponseEntity<Paper> savePaper(@RequestParam("title") String title,
                                           @RequestParam("author") String author,
                                           @RequestParam("date") Date date,
                                           @RequestParam("journal") String journal,
                                           @RequestParam("file") MultipartFile file) {
        Paper paper = new Paper();
        paper.setTitle(title);
        paper.setAuthor(author);
        paper.setDate(date);
        paper.setJournal(journal);
        paper.setFileUrl(file.getOriginalFilename());
        // Handle file saving logic here
        Paper savedPaper = paperService.savePaper(paper);
        return ResponseEntity.ok(savedPaper);
    }

    @GetMapping
    public List<Paper> getAllPapers() {
        return paperService.getAllPapers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paper> getPaperById(@PathVariable Long id) {
        Optional<Paper> paper = paperService.getPaperById(id);
        return paper.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaper(@PathVariable Long id) {
        paperService.deletePaper(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paper> updatePaper(@PathVariable Long id, @RequestBody Paper updatedPaper) {
        Paper paper = paperService.updatePaper(id, updatedPaper);
        return ResponseEntity.ok(paper);
    }
}
