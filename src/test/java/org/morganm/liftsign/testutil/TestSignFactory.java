/*******************************************************************************
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (c) 2012 Mark Morgan.
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
 * 
 * Contributors:
 *     Mark Morgan - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package org.morganm.liftsign.testutil;

import org.bukkit.block.Sign;
import org.morganm.liftsign.SignCache;
import org.morganm.liftsign.SignDetail;
import org.morganm.liftsign.SignFactory;
import org.morganm.liftsign.Util;
import org.morganm.mBukkitLib.Logger;

/** A sign factory that generates real SignDetail objects.
 * @author morganm
 *
 */
public class TestSignFactory {
	private Logger log;
	private Util util;
	// SignCache is not part of constructor because there is a chicken/egg
	// problem between SignCache and a Factory. We let the factory be
	// created without it and then assigned to with setCache() after the
	// cache has been initialized with this factory.
	private SignCache cache;
	
	public TestSignFactory(Logger log, Util util) {
		this.log = log;
		this.util = util;
	}
	
	public SignFactory newSignFactory() {
		return new SignFactory() {
			public SignDetail create(Sign sign, String[] lines) {
				return new SignDetail(cache, log, util, sign, lines);
			}
		};
	}
	public void setCache(SignCache cache) { this.cache = cache; }
}
