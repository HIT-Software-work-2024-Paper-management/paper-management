import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileSystemUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/papers")
public class PaperController {

    private final Path rootLocation = Paths.get("paper-files");

    @Autowired
    private PaperService paperService;

    @PostMapping
    public ResponseEntity<Paper> savePaper(@RequestParam("title") String title,
                                           @RequestParam("author") String author,
                                           @RequestParam("date") Date date,
                                           @RequestParam("journal") String journal,
                                           @RequestParam("file") MultipartFile file) throws IOException {
        Paper paper = new Paper();
        paper.setTitle(title);
        paper.setAuthor(author);
        paper.setDate(date);
        paper.setJournal(journal);

        if (!Files.exists(rootLocation)) {
            Files.createDirectories(rootLocation);
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
