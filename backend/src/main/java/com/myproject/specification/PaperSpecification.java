package com.myproject.specification;

import com.myproject.model.Paper;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Date;

public class PaperSpecification {

    public static Specification<Paper> hasTitle(String title) {
        return (root, query, criteriaBuilder) ->
                title == null ? null : criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    public static Specification<Paper> hasAuthor(String author) {
        return (root, query, criteriaBuilder) ->
                author == null ? null : criteriaBuilder.like(root.get("author"), "%" + author + "%");
    }

    public static Specification<Paper> hasKeywords(String keywords) {
        return (root, query, criteriaBuilder) ->
                keywords == null ? null : criteriaBuilder.like(root.get("keywords"), "%" + keywords + "%");
    }

    public static Specification<Paper> hasDate(Date date) {
        return (root, query, criteriaBuilder) ->
                date == null ? null : criteriaBuilder.equal(root.get("date"), date);
    }

    public static Specification<Paper> hasJournal(String journal) {
        return (root, query, criteriaBuilder) ->
                journal == null ? null : criteriaBuilder.like(root.get("journal"), "%" + journal + "%");
    }

    public static Specification<Paper> hasCategory(Long categoryId) {
        return (root, query, criteriaBuilder) ->
                categoryId == null ? null : criteriaBuilder.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Paper> hasType(String type) {
        return (root, query, criteriaBuilder) ->
                type == null ? null : criteriaBuilder.equal(root.get("type"), type);
    }
}
