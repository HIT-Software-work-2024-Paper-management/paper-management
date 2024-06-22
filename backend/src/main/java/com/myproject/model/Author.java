package com.myproject.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "author")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

//    @ManyToMany(mappedBy = "authors")
//    private List<Paper> papers;

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public List<Paper> getPapers() {
//        return papers;
//    }
//
//    public void setPapers(List<Paper> papers) {
//        this.papers = papers;
//    }
}
