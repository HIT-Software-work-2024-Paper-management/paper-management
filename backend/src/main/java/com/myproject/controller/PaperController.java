package com.myproject.controller;

import com.myproject.model.Paper;
import com.myproject.model.Author;
import com.myproject.model.Category;
import com.myproject.model.Journal;
import com.myproject.model.PaperAuthor;
import com.myproject.service.PaperService;
import com.myproject.service.CategoryService;
import com.myproject.service.JournalService;
import com.myproject.dto.AuthorResponse;
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
import java.util.stream.Collectors;
import java.nio.file.StandardCopyOption;


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

        paper.setPaperAuthors(paperAuthors); // 设置paperAuthors到paper
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
        try {
            System.out.println("come to here1 ");
            Optional<Paper> paperOpt = paperService.getPaperById(id);
            System.out.println("come to here2 ");
            if (paperOpt.isPresent()) {
                Paper paper = paperOpt.get();
                System.out.println("Deleting paper with ID: " + paper.getId());
                paperService.deletePaper(id);
                System.out.println("Successfully deleted paper with ID: " + paper.getId());
                return ResponseEntity.noContent().build();
            } else {
                System.out.println("Paper with ID " + id + " not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paper> updatePaper(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam Map<String, String> authorsMap,
            @RequestParam("date") Date date,
            @RequestParam("journal") Long journalId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("type") String type,
            @RequestParam("impactFactor") double impactFactor) throws IOException {

        Optional<Paper> existingPaperOpt = paperService.getPaperById(id);

        if (!existingPaperOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Paper existingPaper = existingPaperOpt.get();
        existingPaper.setTitle(title);

        // 更新作者列表
        System.out.println("Received authorsMap: " + authorsMap);
        System.out.println("1111111111111111111111111111111111111111111 " );

        List<PaperAuthor> paperAuthors = new ArrayList<>();
        int i = 0;
        while (authorsMap.containsKey("authorsMap[" + i + "].id")) {
            Long authorId = Long.parseLong(authorsMap.get("authorsMap[" + i + "].id"));
            System.out.println("Processing authorId: " + authorId + " with rank: " + authorsMap.get("authorsMap[" + i + "].rank"));
            Optional<Author> authorOpt = paperService.getAuthorById(authorId);

            if (!authorOpt.isPresent()) {
                return ResponseEntity.badRequest().body(null); // 如果作者不存在，返回错误
            }

            Author author = authorOpt.get();
            PaperAuthor paperAuthor = new PaperAuthor();
            paperAuthor.setAuthor(author);
            paperAuthor.setRank(Integer.parseInt(authorsMap.get("authorsMap[" + i + "].rank")));
            paperAuthor.setPaper(existingPaper);
            paperAuthors.add(paperAuthor);
            System.out.println("Added PaperAuthor: " + paperAuthor);
            i++;
        }
        // 打印解析后的 paperAuthors 列表
        System.out.println("11111111111111111111111111111111111111 " );
        System.out.println("Parsed paperAuthors before setting: " + paperAuthors);

        existingPaper.setPaperAuthors(paperAuthors); // 更新 paperAuthors 到 existingPaper
        System.out.println("Parsed paperAuthors: " + existingPaper.getPaperAuthors());



        // 更新其他字段
        existingPaper.setDate(date);
        existingPaper.setImpactFactor(impactFactor);
        existingPaper.setType(type);

        Optional<Category> categoryOpt = categoryService.getCategoryById(categoryId);
        if (categoryOpt.isPresent()) {
            existingPaper.setCategory(categoryOpt.get());
        } else {
            return ResponseEntity.badRequest().build();
        }

        Optional<Journal> journalOpt = journalService.getJournalById(journalId);
        if (journalOpt.isPresent()) {
            existingPaper.setJournal(journalOpt.get());
        } else {
            return ResponseEntity.badRequest().build();
        }

        String filename = file.getOriginalFilename();
        if (filename != null && !filename.isEmpty()) {
            Path destinationFile = rootLocation.resolve(Paths.get(filename)).normalize().toAbsolutePath();
            if (!destinationFile.startsWith(rootLocation.toAbsolutePath())) {
                // 防止文件路径遍历攻击
                return ResponseEntity.badRequest().build();
            }
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            existingPaper.setFileUrl(destinationFile.toString());
        }



        Paper savedPaper = paperService.updatePaper(id, existingPaper,paperAuthors); // 调整 updatePaper 方法调用

        return ResponseEntity.ok(savedPaper);
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
                                                 @RequestParam(value = "date", required = false) Date date,
                                                 @RequestParam(value = "journal", required = false) String journal,
                                                 @RequestParam(value = "categoryId", required = false) Long categoryId,
                                                 @RequestParam(value = "type", required = false) String type) throws IOException {
        Specification<Paper> spec = Specification.where(PaperSpecification.hasTitle(title))
                .and(PaperSpecification.hasAuthors(authors))
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

    //以下用于论文呢关系网络
    @GetMapping("/author/{authorName}")
    public ResponseEntity<List<Paper>> getPapersByAuthorName(@PathVariable String authorName) {
        List<Paper> papers = paperService.findPapersByAuthorName(authorName);
        return ResponseEntity.ok(papers);
    }

    @GetMapping("/{paperId}/authors")
    public ResponseEntity<List<AuthorResponse>> getAuthorsByPaperId(@PathVariable Long paperId) {
        List<PaperAuthor> paperAuthors = paperService.findAuthorsByPaperId(paperId);
        List<AuthorResponse> response = paperAuthors.stream()
                .map(pa -> new AuthorResponse(pa.getAuthor().getName(), pa.getRank()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

        //以下用于计算工作量分数
        @GetMapping("/workload-score")
        public ResponseEntity<Double> getWorkloadScore(@RequestParam String paperTitle, @RequestParam String authorName) {
            Paper paper = paperService.findPaperByTitle(paperTitle);
            if (paper == null) {
                return ResponseEntity.badRequest().body(null);
            }

            double score;
            try {
                score = paperService.calculateWorkloadScore(paper, authorName);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(null);
            }

            return ResponseEntity.ok(score);
        }
}
