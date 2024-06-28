package com.myproject.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;
import javax.transaction.Transactional;

@Entity
@Table(name = "papers")
public class Paper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToMany
    @JoinTable(
            name = "paper_author",
            joinColumns = @JoinColumn(name = "paper_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
//    @OneToMany
    private List<Author> authors;

    @Column(name = "date", nullable = false)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "journal_id", nullable = false)
    private Journal journal;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "impact_factor", nullable = false)
    private double impactFactor;

//    @Column(name = "workload_score")
//    private double workloadScore;

//    @OneToMany(mappedBy = "paper")
    @JsonManagedReference
    @OneToMany(mappedBy = "paper", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaperAuthor> paperAuthors; // 添加 paperAuthors 字段

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Journal getJournal() {
        return journal;
    }

    public void setJournal(Journal journal) {
        this.journal = journal;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getImpactFactor() {
        return impactFactor;
    }

    public void setImpactFactor(double impactFactor) {
        this.impactFactor = impactFactor;
    }

//    public double getWorkloadScore() {
//        return workloadScore;
//    }
//
//    public void setWorkloadScore(double workloadScore) {
//        this.workloadScore = workloadScore;
//    }

    public List<PaperAuthor> getPaperAuthors() {
        return paperAuthors;
    }

    public void setPaperAuthors(List<PaperAuthor> newPaperAuthors) {
        // Step 1: Clear the existing relationships
        clearPaperAuthors();

        // Step 2: Add the new relationships
        addPaperAuthors(newPaperAuthors);
    }

    private void clearPaperAuthors() {
        if (this.paperAuthors != null) {
            // Detach each PaperAuthor
            for (PaperAuthor paperAuthor : this.paperAuthors) {
                paperAuthor.setPaper(null);
            }
            this.paperAuthors.clear();
        }
    }

    private void addPaperAuthors(List<PaperAuthor> paperAuthors) {
        if (paperAuthors != null) {
            for (PaperAuthor paperAuthor : paperAuthors) {
                addPaperAuthor(paperAuthor);
            }
        }
    }

    public void addPaperAuthor(PaperAuthor paperAuthor) {
        paperAuthor.setPaper(this);
        this.paperAuthors.add(paperAuthor);
    }
}
