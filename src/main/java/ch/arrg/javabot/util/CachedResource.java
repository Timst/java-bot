package ch.arrg.javabot.util;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class CachedResource<T> {
	private final Supplier<T> supplierFn;
	private final long cacheDurationMs;
	
	private T cachedInstance;
	private long lastUpdated;

	public static <T> CachedResource<T> make(Supplier<T> supplierFn, int cacheDuration, TimeUnit cacheTimeUnit) {
		return new CachedResource(supplierFn, cacheDuration, cacheTimeUnit);
	}
	
	public CachedResource(Supplier<T> supplierFn, int cacheDuration, TimeUnit cacheTimeUnit) {
		this.supplierFn = supplierFn;
		this.cacheDurationMs = cacheTimeUnit.toMillis(cacheDuration);
		update();
	}

	public void update() {
		T newValue = supplierFn.get();

		if(newValue != null) {
			cachedInstance = newValue;
			lastUpdated = System.currentTimeMillis();
		}
	}

	public void invalidate() {
		lastUpdated = 0;
	}
	
	public T get() {
		if(System.currentTimeMillis() > lastUpdated + cacheDurationMs) {
			update();
		}
		
		return cachedInstance;
	}
}
