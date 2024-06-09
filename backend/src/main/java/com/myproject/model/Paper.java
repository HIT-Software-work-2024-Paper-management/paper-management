package com.myproject.model;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "papers")
public class Paper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String authors; // 将作者改为字符串
    private String keywords;
    private Date date;
    private String journal;
    private String fileUrl;
//    private String filePath;

    private double workloadScore; // 工作分数
    private double impactFactor; // 期刊影响因子
    private int authorRank; // 作者排名

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String type; // 'paper' or 'reference'

    // Getters and Setters

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

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public double getWorkloadScore() {
        return workloadScore;
    }

    public void setWorkloadScore(double workloadScore) {
        this.workloadScore = workloadScore;
    }

    public double getImpactFactor() {
        return impactFactor;
    }

    public void setImpactFactor(double impactFactor) {
        this.impactFactor = impactFactor;
    }

    public int getAuthorRank() {
        return authorRank;
    }

    public void setAuthorRank(int authorRank) {
        this.authorRank = authorRank;
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

//    public String getFilePath() {
//        return filePath;
//    }
//
//    public void setFilePath(String filePath) {
//        this.filePath = filePath;
//    }
}
