/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2013 Andune (andune.alleria@gmail.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
/**
 * 
 */
package com.andune.liftsign;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.junit.Before;
import org.junit.Test;
import com.andune.liftsign.testutil.MockSignFactory;
import com.andune.liftsign.testutil.TestUtility;
import org.powermock.api.mockito.PowerMockito;

/**
 * @author andune
 *
 */
public class TestSignCache {
	private SignFactory factory;
	private TestUtility testUtility;
	
	@Before
	public void setup() {
		testUtility = new TestUtility();
		factory = new MockSignFactory();
	}
	
	@Test
	public void testNewSignCreated() {
		SignCache cache = newCache();
		Sign sign = newTestSign();
		
		// first validate getCachedDetail is empty
		SignDetail signDetail = cache.getCachedSignDetail(sign.getLocation());
		assertNull(signDetail);
		
		// now create a sign and make sure it's in the cache
		signDetail = cache.newSignCreated(sign);
		SignDetail cached = cache.getCachedSignDetail(sign.getLocation());
		assertSame(signDetail, cached);
	}
	
	@Test
	public void testExistingSignDestroyed() {
		SignCache cache = newCache();
		Sign sign = newTestSign();
		
		// now create a sign and put it into the cache
		SignDetail signDetail = cache.newSignCreated(sign);
		SignDetail cached = cache.getCachedSignDetail(sign.getLocation());
		assertSame(signDetail, cached);
		
		// now destroy the sign and make sure it's no longer in the cache
		cache.existingSignDestroyed(sign);
		cached = cache.getCachedSignDetail(sign.getLocation());
		assertNull(cached);
	}
	
	private Sign newTestSign() {
		World world = testUtility.createMockWorld();
		Location loc = testUtility.newUniqueLocation(world);
		
		Sign sign = PowerMockito.mock(Sign.class);
		when(sign.getLocation()).thenReturn(loc);
		return sign;
	}
	
	/** Generate and setup new cache object for testing.
	 * 
	 * @return
	 */
	private SignCache newCache() {
		return new SignCache(factory);
	}
}
