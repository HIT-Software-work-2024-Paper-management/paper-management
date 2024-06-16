package com.myproject.controller;

import com.myproject.dto.ReferenceResponse;
import com.myproject.model.Paper;
import com.myproject.model.Reference;
import com.myproject.service.ReferenceService;
import com.myproject.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/references")
public class ReferenceController {

    @Autowired
    private ReferenceService ReferenceService;

    @Autowired
    private PaperService paperService; // 确保注入 PaperService

    @PostMapping("/add")
    public ResponseEntity<Reference> addReference(@RequestParam Long paperId, @RequestParam Long referenceId) {
        Reference Reference = ReferenceService.addReference(paperId, referenceId);
        return ResponseEntity.ok(Reference);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteReference(@PathVariable Long id) {
        ReferenceService.deleteReference(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{paperId}")
    public ResponseEntity<List<ReferenceResponse>> getReferencesByPaperId(@PathVariable Long paperId) {
        List<Reference> references = ReferenceService.getReferencesByPaperId(paperId);
        List<ReferenceResponse> response = references.stream()
                .map(ref -> new ReferenceResponse(ref.getId(), ref.getReferenceId(), getReferenceName(ref.getReferenceId())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private String getReferenceName(Long referenceId) {
        // 获取 referenceName 的逻辑，这里假设从 paperService 中获取
        return paperService.getPaperById(referenceId).map(Paper::getTitle).orElse("Unknown");
    }
}
