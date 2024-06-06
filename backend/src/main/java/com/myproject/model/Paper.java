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
    private List<String> authors; // 将作者改为列表
    private String keywords;
    private Date date;
    private String journal;
    private String fileUrl;

    private double workloadScore;//工作分数
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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
}
