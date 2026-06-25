package org.restcomm.protocols.ss7.scheduler.distributed;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;

/**
 * Resolves WildFly-managed Infinispan caches via JNDI without requiring callers to hold a
 * compile-time {@code org.infinispan.Cache} reference.
 */
final class InfinispanCacheResolver {

    private static final Logger LOGGER = Logger.getLogger(InfinispanCacheResolver.class.getName());

    private InfinispanCacheResolver() {
    }

    @SuppressWarnings("unchecked")
    static <K, V> ResolvedCache<K, V> resolve(String containerJndiName, String cacheName) throws Exception {
        InitialContext ctx = new InitialContext();
        Object container = ctx.lookup(containerJndiName);
        if (container == null) {
            throw new IllegalStateException("Infinispan container not found at " + containerJndiName);
        }
        Method getCache = container.getClass().getMethod("getCache", String.class);
        Object resolved = getCache.invoke(container, cacheName);
        if (!(resolved instanceof ConcurrentMap)) {
            throw new IllegalStateException("Resolved cache is not a ConcurrentMap: " + resolved);
        }
        ConcurrentMap<K, V> map = (ConcurrentMap<K, V>) resolved;
        Method ttlPut = resolveTtlPut(resolved);
        LOGGER.info("Resolved Infinispan cache " + containerJndiName + "/" + cacheName
                + ", nativeTtl=" + (ttlPut != null));
        return new ResolvedCache<K, V>(map, ttlPut);
    }

    static void registerListener(Object cache, Object listener) {
        if (cache == null || listener == null) {
            return;
        }
        try {
            Method addListener = cache.getClass().getMethod("addListener", Object.class);
            addListener.invoke(cache, listener);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to register Infinispan cache listener", e);
        }
    }

    private static Method resolveTtlPut(Object cache) {
        try {
            return cache.getClass().getMethod("put", Object.class, Object.class, long.class, TimeUnit.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    static final class ResolvedCache<K, V> {
        final ConcurrentMap<K, V> map;
        final Method ttlPut;

        ResolvedCache(ConcurrentMap<K, V> map, Method ttlPut) {
            this.map = map;
            this.ttlPut = ttlPut;
        }

        void putWithTtl(K key, V value, long ttlMillis) {
            if (ttlPut != null) {
                try {
                    ttlPut.invoke(map, key, value, ttlMillis, TimeUnit.MILLISECONDS);
                    return;
                } catch (Exception e) {
                    LOGGER.log(Level.FINE, "TTL put failed, falling back to plain put", e);
                }
            }
            map.put(key, value);
        }
    }
}
