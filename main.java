package com.cams.core.rulesui.scm.cache;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryScmCache<V> implements ScmCache<V> {
    private final ConcurrentHashMap<String, V> map = new ConcurrentHashMap<>();

    @Override public Optional<V> get(String key) { return Optional.ofNullable(map.get(key)); }
    @Override public void put(String key, V value) { map.put(key, value); }
    @Override public void evict(String key) { map.remove(key); }
    @Override public void clear() { map.clear(); }
}
