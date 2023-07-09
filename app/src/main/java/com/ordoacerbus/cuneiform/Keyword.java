package com.ordoacerbus.cuneiform;

public record Keyword(String keyword) implements Comparable<Keyword> {
    public Keyword {
        keyword = keyword.toUpperCase();
    }

    static Keyword kw(String keyword) {
        return new Keyword(keyword);
    }

    @Override
    public int compareTo(Keyword o) {
        return keyword.compareTo(o.keyword);
    }
}
