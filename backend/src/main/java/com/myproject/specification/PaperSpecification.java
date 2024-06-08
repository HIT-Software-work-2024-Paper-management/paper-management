package com.myproject.specification;

import com.myproject.model.Paper;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Date;
import org.springframework.data.jpa.domain.Specification;

public class PaperSpecification {

    public static Specification<Paper> hasTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.isEmpty()) {
                return criteriaBuilder.conjunction(); // 返回一个始终为 true 的条件，不影响查询
            } else {
                return criteriaBuilder.like(root.get("title"), "%" + title + "%");
            }
        };
    }
    public static Specification<Paper> hasAuthors(String authors) {
        return (root, query, criteriaBuilder) -> {
            if (authors == null || authors.isEmpty()) {
                return criteriaBuilder.conjunction(); // 返回一个始终为 true 的条件，不影响查询
            } else {
                return criteriaBuilder.like(root.get("authors"), "%" + authors + "%");
            }
        };
    }


    public static Specification<Paper> hasKeywords(String keywords) {
        return (root, query, criteriaBuilder) -> {
            if (keywords == null || keywords.isEmpty()) {
                return criteriaBuilder.conjunction(); // 返回一个始终为 true 的条件，不影响查询
            } else {
                return criteriaBuilder.like(root.get("keywords"), "%" + keywords + "%");
            }
        };
    }


    public static Specification<Paper> hasDate(Date date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return criteriaBuilder.conjunction(); // 返回一个始终为 true 的条件，不影响查询
            } else {
                return criteriaBuilder.equal(root.get("date"), date);
            }
        };
    }

    public static Specification<Paper> hasJournal(String journal) {
        return (root, query, criteriaBuilder) -> {
            if (journal == null || journal.isEmpty()) {
                return criteriaBuilder.conjunction(); // 返回一个始终为 true 的条件，不影响查询
            } else {
                return criteriaBuilder.like(root.get("journal"), "%" + journal + "%");
            }
        };
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
