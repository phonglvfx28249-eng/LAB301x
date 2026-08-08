package com.windle.blockchaintrading.common;

import org.springframework.data.domain.Page;

import java.util.List;


public class PageResponse<T> {

    private List<T> content;
    private int page;          // current page, 0-indexed
    private int size;          // page size
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    public static <T> PageResponse<T> of(Page<T> page) {
        PageResponse<T> res = new PageResponse<>();
        res.content = page.getContent();
        res.page = page.getNumber();
        res.size = page.getSize();
        res.totalElements = page.getTotalElements();
        res.totalPages = page.getTotalPages();
        res.first = page.isFirst();
        res.last = page.isLast();
        return res;
    }

    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public boolean isFirst() { return first; }
    public void setFirst(boolean first) { this.first = first; }
    public boolean isLast() { return last; }
    public void setLast(boolean last) { this.last = last; }
}
