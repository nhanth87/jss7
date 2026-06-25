package org.restcomm.protocols.ss7.scheduler.distributed;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * ConcurrentHashMap-backed cache that simulates per-entry TTL for unit tests.
 */
final class TtlMockCache<K, V> extends ConcurrentHashMap<K, V> {

    private final Map<K, Long> expiryAtMillis = new ConcurrentHashMap<K, Long>();
    private final List<Entry<K, V>> expiredEntries = new ArrayList<Entry<K, V>>();

    Method ttlPutMethod() {
        try {
            return getClass().getMethod("put", Object.class, Object.class, long.class, TimeUnit.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unused")
    public V put(K key, V value, long duration, TimeUnit unit) {
        long ttlMillis = unit.toMillis(duration);
        expiryAtMillis.put(key, System.currentTimeMillis() + Math.max(1L, ttlMillis));
        return put(key, value);
    }

    void expireDueEntries() {
        long now = System.currentTimeMillis();
        for (Map.Entry<K, Long> entry : new ArrayList<Map.Entry<K, Long>>(expiryAtMillis.entrySet())) {
            if (entry.getValue() <= now) {
                K key = entry.getKey();
                expiryAtMillis.remove(key);
                V value = remove(key);
                if (value != null) {
                    expiredEntries.add(new SimpleEntry<K, V>(key, value));
                }
            }
        }
    }

    List<Entry<K, V>> drainExpiredEntries() {
        List<Entry<K, V>> drained = new ArrayList<Entry<K, V>>(expiredEntries);
        expiredEntries.clear();
        return drained;
    }

    InfinispanCacheResolver.ResolvedCache<K, V> asResolvedCache() {
        return new InfinispanCacheResolver.ResolvedCache<K, V>(this, ttlPutMethod());
    }

    private static final class SimpleEntry<K, V> implements Entry<K, V> {
        private final K key;
        private final V value;

        SimpleEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }
}
