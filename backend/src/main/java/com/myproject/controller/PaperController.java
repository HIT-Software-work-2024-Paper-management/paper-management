package com.myproject.controller;

import com.myproject.model.Paper;
import com.myproject.model.Author;
import com.myproject.model.Category;
import com.myproject.model.Journal;
import com.myproject.model.PaperAuthor;
import com.myproject.service.PaperService;
import com.myproject.service.CategoryService;
import com.myproject.service.JournalService;
import com.myproject.specification.PaperSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.jpa.domain.Specification;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/papers")
public class PaperController {

    private final Path rootLocation = Paths.get("paper-files");

    @Autowired
    private PaperService paperService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private JournalService journalService;

    @PostConstruct
    public void init() {
        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Paper> savePaper(
            @RequestParam("title") String title,
            @RequestParam Map<String, String> authorsMap,
            @RequestParam("date") Date date,
            @RequestParam("journal") Long journalId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("type") String type,
            @RequestParam("impactFactor") double impactFactor) throws IOException {


        System.out.println("Title: " + title);
        System.out.println("Date: " + date);
        System.out.println("JournalId: " + journalId);
        System.out.println("CategoryId: " + categoryId);
        System.out.println("Type: " + type);
        System.out.println("ImpactFactor: " + impactFactor);
        System.out.println("File: " + file.getOriginalFilename());

        Paper paper = new Paper();
        paper.setTitle(title);

        // 初始化paperAuthors列表
        paper.setPaperAuthors(new ArrayList<>());


        // 处理作者列表
        List<PaperAuthor> paperAuthors = new ArrayList<>();
        int i = 0;
        while (authorsMap.containsKey("authors[" + i + "].id")) {
            Long authorId = Long.parseLong(authorsMap.get("authors[" + i + "].id"));
            Optional<Author> authorOpt = paperService.getAuthorById(authorId);

            if (!authorOpt.isPresent()) {
                return ResponseEntity.badRequest().body(null); // 如果作者不存在，返回错误
            }

            Author author = authorOpt.get();
            PaperAuthor paperAuthor = new PaperAuthor();
            paperAuthor.setAuthor(author);
            paperAuthor.setRank(Integer.parseInt(authorsMap.get("authors[" + i + "].rank")));
            paperAuthor.setPaper(paper);
            paperAuthors.add(paperAuthor);
            i++;
        }

        // 设置其他字段
        paper.setDate(date);
        paper.setImpactFactor(impactFactor);
        paper.setType(type);

        Optional<Category> categoryOpt = categoryService.getCategoryById(categoryId);
        if (categoryOpt.isPresent()) {
            paper.setCategory(categoryOpt.get());
        } else {
            return ResponseEntity.badRequest().build();
        }

        Optional<Journal> journalOpt = journalService.getJournalById(journalId);
        if (journalOpt.isPresent()) {
            paper.setJournal(journalOpt.get());
        } else {
            return ResponseEntity.badRequest().build();
        }


        String filename = file.getOriginalFilename();
        if (filename != null) {
            Path destinationFile = rootLocation.resolve(Paths.get(filename)).normalize().toAbsolutePath();
            if (!destinationFile.startsWith(rootLocation.toAbsolutePath())) {
                // 防止文件路径遍历攻击
                return ResponseEntity.badRequest().build();
            }
            Files.copy(file.getInputStream(), destinationFile);
            paper.setFileUrl(destinationFile.toString());
        }

//        paper.setPaperAuthors(paperAuthors); // 设置paperAuthors到paper
        Paper savedPaper = paperService.savePaper(paper, paperAuthors);
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
        Optional<Paper> existingPaperOpt = paperService.getPaperById(id);
        if (existingPaperOpt.isPresent()) {
            Paper existingPaper = existingPaperOpt.get();
            existingPaper.setTitle(updatedPaper.getTitle());
            existingPaper.setAuthors(updatedPaper.getAuthors());
            existingPaper.setDate(updatedPaper.getDate());
            existingPaper.setCategory(updatedPaper.getCategory());
            existingPaper.setFileUrl(updatedPaper.getFileUrl());
            existingPaper.setType(updatedPaper.getType());
            existingPaper.setImpactFactor(updatedPaper.getImpactFactor());

            Optional<Journal> journalOpt = journalService.getJournalById(updatedPaper.getJournal().getId());
            if (journalOpt.isPresent()) {
                existingPaper.setJournal(journalOpt.get());
            } else {
                return ResponseEntity.badRequest().build();
            }

            Paper savedPaper = paperService.updatePaper(id, existingPaper);
            return ResponseEntity.ok(savedPaper);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public List<Paper> searchPapers(@RequestParam(value = "title", required = false) String title,
                                    @RequestParam(value = "authors", required = false) String authors,
                                    @RequestParam(value = "keywords", required = false) String keywords,
                                    @RequestParam(value = "date", required = false) Date date,
                                    @RequestParam(value = "journal", required = false) String journal,
                                    @RequestParam(value = "categoryId", required = false) Long categoryId) {

        // 打印各个传入的参数
        System.out.println("title=" + title);
        System.out.println("authors=" + authors);
        System.out.println("keywords=" + keywords);
        System.out.println("journal=" + journal);
        System.out.println("categoryId=" + categoryId);

        Specification<Paper> spec = Specification.where(PaperSpecification.hasTitle(title))
                .and(PaperSpecification.hasAuthors(authors))
                .and(PaperSpecification.hasKeywords(keywords))
                .and(PaperSpecification.hasDate(date))
                .and(PaperSpecification.hasJournal(journal))
                .and(PaperSpecification.hasCategory(categoryId));

        List<Paper> results = paperService.searchPapers(spec);
        System.out.println("Query Results: " + results.size() + " papers found.");
        return results;
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportPapers(@RequestParam(value = "title", required = false) String title,
                                                 @RequestParam(value = "authors", required = false) String authors,
                                                 @RequestParam(value = "keywords", required = false) String keywords,
                                                 @RequestParam(value = "date", required = false) Date date,
                                                 @RequestParam(value = "journal", required = false) String journal,
                                                 @RequestParam(value = "categoryId", required = false) Long categoryId,
                                                 @RequestParam(value = "type", required = false) String type) throws IOException {
        Specification<Paper> spec = Specification.where(PaperSpecification.hasTitle(title))
                .and(PaperSpecification.hasAuthors(authors))
                .and(PaperSpecification.hasKeywords(keywords))
                .and(PaperSpecification.hasDate(date))
                .and(PaperSpecification.hasJournal(journal))
                .and(PaperSpecification.hasCategory(categoryId))
                .and(PaperSpecification.hasType(type));
        List<Paper> papers = paperService.searchPapers(spec);
        ByteArrayInputStream in = paperService.exportPapersToExcel(papers);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=papers.xlsx");

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws IOException {
        Optional<Paper> paperOpt = paperService.getPaperById(id);
        if (paperOpt.isPresent()) {
            Paper paper = paperOpt.get();
            Path filePath = Paths.get(paper.getFileUrl());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
