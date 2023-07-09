package com.ordoacerbus.cuneiform;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class Counter<T extends Comparable<T>> implements Iterable<T> {
    private final Comparator<Map.Entry<T, Integer>> cmp = Map.Entry.<T, Integer>comparingByValue()
            .reversed()
            .thenComparing(Map.Entry.comparingByKey());
    private final Map<T, Integer> map = new LinkedHashMap<>();

    public void add(T item) {
        map.merge(item, 1, Integer::sum);
    }

    public void removeIf(BiPredicate<T, Integer> condition) {
        map.entrySet().removeIf(entry -> condition.test(entry.getKey(), entry.getValue()));
    }

    public Optional<T> max() {
        return map.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    public void add(List<T> items) {
        items.forEach(this::add);
    }

    @Override
    public Iterator<T> iterator() {
        return min(0).iterator();
    }

    public Iterable<T> min(int minCount) {
        return map.entrySet().stream().sorted(cmp).filter(e -> e.getValue() >= minCount).map(Map.Entry::getKey).toList();
    }

    @Override
    public String toString() {
        return map.entrySet().stream()
                .sorted(cmp)
                .filter(e -> e.getValue() > 1)
                .map(e -> "%s [%d]".formatted(e.getKey(), e.getValue()))
                .collect(Collectors.joining(", ", "Counter{", "}"));
    }

    public boolean contains(T item) {
        return map.containsKey(item);
    }
}
