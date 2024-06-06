import com.myproject.model.Paper;
import com.myproject.model.Category;
import com.myproject.service.PaperService;
import com.myproject.service.CategoryService;
import com.myproject.specification.PaperSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/papers")
public class PaperController {

    private final Path rootLocation = Paths.get("paper-files");

    @Autowired
    private PaperService paperService;

    @Autowired
    private CategoryService categoryService;

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
    public ResponseEntity<Paper> savePaper(@RequestParam("title") String title,
                                           @RequestParam("author") String author,
                                           @RequestParam("date") Date date,
                                           @RequestParam("journal") String journal,
                                           @RequestParam("file") MultipartFile file,
                                           @RequestParam("categoryId") Long categoryId,
                                           @RequestParam("type") String type) throws IOException {
        Paper paper = new Paper();
        paper.setTitle(title);
        paper.setAuthor(author);
        paper.setDate(date);
        paper.setJournal(journal);
        paper.setType(type);

        Optional<Category> categoryOpt = categoryService.getCategoryById(categoryId);
        if (categoryOpt.isPresent()) {
            paper.setCategory(categoryOpt.get());
        } else {
            return ResponseEntity.badRequest().build();
        }

        String filename = file.getOriginalFilename();
        if (filename != null) {
            Path destinationFile = rootLocation.resolve(Paths.get(filename)).normalize().toAbsolutePath();
            Files.copy(file.getInputStream(), destinationFile);

            paper.setFileUrl(destinationFile.toString());
        }

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

    @GetMapping("/search")
    public List<Paper> searchPapers(@RequestParam(value = "title", required = false) String title,
                                    @RequestParam(value = "author", required = false) String author,
                                    @RequestParam(value = "keywords", required = false) String keywords,
                                    @RequestParam(value = "date", required = false) Date date,
                                    @RequestParam(value = "journal", required = false) String journal,
                                    @RequestParam(value = "categoryId", required = false) Long categoryId,
                                    @RequestParam(value = "type", required = false) String type) {
        Specification<Paper> spec = Specification.where(PaperSpecification.hasTitle(title))
                .and(PaperSpecification.hasAuthor(author))
                .and(PaperSpecification.hasKeywords(keywords))
                .and(PaperSpecification.hasDate(date))
                .and(PaperSpecification.hasJournal(journal))
                .and(PaperSpecification.hasCategory(categoryId))
                .and(PaperSpecification.hasType(type));
        return paperService.searchPapers(spec);
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportPapers(@RequestParam(value = "title", required = false) String title,
                                                 @RequestParam(value = "author", required = false) String author,
                                                 @RequestParam(value = "keywords", required = false) String keywords,
                                                 @RequestParam(value = "date", required = false) Date date,
                                                 @RequestParam(value = "journal", required = false) String journal,
                                                 @RequestParam(value = "categoryId", required = false) Long categoryId,
                                                 @RequestParam(value = "type", required = false) String type) throws IOException {
        Specification<Paper> spec = Specification.where(PaperSpecification.hasTitle(title))
                .and(PaperSpecification.hasAuthor(author))
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
