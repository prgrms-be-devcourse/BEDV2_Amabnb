package com.prgrms.amabnb.review.dto.request;

import org.springframework.lang.Nullable;

import lombok.Getter;

@Getter
public class PageReviewRequest {
    @Nullable
    private Integer page;
    @Nullable
    private Integer size;

    public PageReviewRequest(Integer page, Integer size) {
        setPage(page);
        setSize(size);
    }

    public void setPage(Integer page) {
        this.page = (page == null || page <= 0) ? 1 : page;
    }

    public void setSize(Integer size) {
        int DEFAULT_SIZE = 10;
        int MAX_SIZE = 50;
        this.size = (size == null || size > MAX_SIZE) ? DEFAULT_SIZE : size;
    }


    public org.springframework.data.domain.PageRequest of() {
        return org.springframework.data.domain.PageRequest.of(page - 1, size);
    }
}
