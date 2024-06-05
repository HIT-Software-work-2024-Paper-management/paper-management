package com.myproject.service;

import com.myproject.model.Paper;
import com.myproject.repository.PaperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            paper.setFileUrl(updatedPaper.getFileUrl());
            return paperRepository.save(paper);
        }).orElseGet(() -> {
            updatedPaper.setId(id);
            return paperRepository.save(updatedPaper);
        });
    }
}
