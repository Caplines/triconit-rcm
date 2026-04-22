package com.tricon.rcm.dto;

import java.util.List;

public class PagedClaimsResponse {

    private List<FreshClaimDataViewDto> claims;
    private long totalCount;
    private int page;
    private int size;
    private int totalPages;

    public PagedClaimsResponse(List<FreshClaimDataViewDto> claims, long totalCount, int page, int size) {
        this.claims = claims;
        this.totalCount = totalCount;
        this.page = page;
        this.size = size;
        this.totalPages = (size > 0) ? (int) Math.ceil((double) totalCount / size) : 0;
    }

    public List<FreshClaimDataViewDto> getClaims() {
        return claims;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
