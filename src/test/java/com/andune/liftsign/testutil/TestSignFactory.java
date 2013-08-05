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
package com.andune.liftsign.testutil;

import org.bukkit.block.Sign;
import com.andune.liftsign.SignCache;
import com.andune.liftsign.SignDetail;
import com.andune.liftsign.SignFactory;
import com.andune.liftsign.Util;

import com.andune.minecraft.commonlib.Logger;

/** A sign factory that generates real SignDetail objects.
 * @author andune
 *
 */
public class TestSignFactory {
	private Util util;
	// SignCache is not part of constructor because there is a chicken/egg
	// problem between SignCache and a Factory. We let the factory be
	// created without it and then assigned to with setCache() after the
	// cache has been initialized with this factory.
	private SignCache cache;
	
	public TestSignFactory(Util util) {
		this.util = util;
	}
	
	public SignFactory newSignFactory() {
		return new SignFactory() {
			public SignDetail create(Sign sign, String[] lines) {
				return new SignDetail(cache, util, sign, lines);
			}
		};
	}
	public void setCache(SignCache cache) { this.cache = cache; }
}
