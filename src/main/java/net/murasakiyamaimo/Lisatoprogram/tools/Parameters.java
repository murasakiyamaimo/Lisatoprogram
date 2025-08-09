package net.murasakiyamaimo.Lisatoprogram.tools;

import java.util.*;

public class Parameters<K, V> {
    private final List<Map.Entry<K, V>> entries = new ArrayList<>();
    private final Map<K, List<Integer>> indexMap = new HashMap<>();

    public void add(K key, V value) {
        Map.Entry<K, V> entry = new AbstractMap.SimpleEntry<>(key, value);
        entries.add(entry);

        indexMap.computeIfAbsent(key, k -> new ArrayList<>())
                .add(entries.size() - 1);
    }

    public Map.Entry<K, V> get(int index) {
        return entries.get(index);
    }

    public int size() {
        return entries.size();
    }

    public Iterable<Map.Entry<K, V>> entries() {
        return Collections.unmodifiableList(entries);
    }
}
