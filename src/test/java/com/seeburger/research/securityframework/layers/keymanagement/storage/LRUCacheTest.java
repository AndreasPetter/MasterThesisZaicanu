package com.seeburger.research.securityframework.layers.keymanagement.storage;

import static org.junit.Assert.*;

import org.junit.Test;

import com.seeburger.research.securityframework.layers.keymanagement.storage.LRUCache;

public class LRUCacheTest {

	@Test
	public void testLRUCache() {
		LRUCache<Integer, String> cache = new LRUCache<Integer, String>(10);
		for (int i = 1; i <= 15; i++){
			cache.put(i, "");
			if (i < 10){
				assertEquals(i, cache.size());
			}
			else if (i == 10){
				cache.get(3);
			}
			else{
				assertEquals(10, cache.size());
			}
		}
		
		for (int i = 7; i<=15; i++){
			assertTrue(cache.containsKey(i));
		}
		
		for (int i = 1; i<=6; i++){
			if (i == 3)
				assertTrue(cache.containsKey(i));
			else assertTrue(!cache.containsKey(i)); 
		}
			
	}

}
