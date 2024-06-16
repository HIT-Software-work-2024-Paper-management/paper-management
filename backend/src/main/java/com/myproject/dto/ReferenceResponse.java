//用于将reference表中搜索到的id转换为id和name的数据对象

package com.myproject.dto;

public class ReferenceResponse {
    private Long id;
    private Long referenceId;
    private String referenceName;

    public ReferenceResponse(Long id, Long referenceId, String referenceName) {
        this.id = id;
        this.referenceId = referenceId;
        this.referenceName = referenceName;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }
}
