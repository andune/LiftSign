/*******************************************************************************
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
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
 * Contributors:
 *     Mark Morgan - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package org.morganm.liftsign.testutil;

import static org.mockito.Mockito.when;

import org.bukkit.Location;
import org.bukkit.World;
import org.powermock.api.mockito.PowerMockito;

/** General utility routines common to multiple tests.
 * 
 * @author morganm
 *
 */
public class TestUtility {
	private static int nextInt=1;

	public World createMockWorld() {
		World world = PowerMockito.mock(World.class);
		when(world.getName()).thenReturn("MockWorld"+nextInt++);
		return world;
	}
	
	public Location newLocation(final World world, final int x, final int y, final int z) {
		Location loc = PowerMockito.mock(Location.class);
		when(loc.getWorld()).thenReturn(world);
		when(loc.getBlockX()).thenReturn(x);
		when(loc.getBlockY()).thenReturn(y);
		when(loc.getBlockZ()).thenReturn(z);
		when(loc.getX()).thenReturn((double) x);
		when(loc.getY()).thenReturn((double) y);
		when(loc.getZ()).thenReturn((double) z);
		return loc;
	}
	
	/** Return a new, unique location with every invocation. We just
	 * increment the x axis by 1 with each call.
	 * 
	 * @param world
	 * @return
	 */
	public Location newUniqueLocation(World world) {
		Location loc = newLocation(world, nextInt++, 0, 0);
		return loc;
	}

}
