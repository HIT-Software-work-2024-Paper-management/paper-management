package com.myproject.dto;

public class AuthorResponse {
    private String name;
    private int rank;

    public AuthorResponse(String name, int rank) {
        this.name = name;
        this.rank = rank;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
