package com.myproject.service;

import com.myproject.model.Reference;
import com.myproject.repository.ReferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReferenceService {

    @Autowired
    private ReferenceRepository ReferenceRepository;

    public Reference addReference(Long paperId, Long referenceId) {
        Reference Reference = new Reference();
        Reference.setPaperId(paperId);
        Reference.setReferenceId(referenceId);
        return ReferenceRepository.save(Reference);
    }

    public void deleteReference(Long id) {
        ReferenceRepository.deleteById(id);
    }

    public List<Reference> getReferencesByPaperId(Long paperId) {
        return ReferenceRepository.findByPaperId(paperId);
    }
}
