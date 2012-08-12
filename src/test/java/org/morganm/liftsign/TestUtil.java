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
package org.morganm.liftsign;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

/**
 * @author morganm
 *
 */
public class TestUtil {
	private Block notASign;
	private Block normalSign;
	private Block wallSign;
	private Sign dummySign;
	
	@Before
	public void setup() {
		dummySign = PowerMockito.mock(Sign.class);
		
		notASign = PowerMockito.mock(Block.class);
		when(notASign.getTypeId()).thenReturn(1);
		when(notASign.getState()).thenReturn(dummySign);
		
		normalSign = PowerMockito.mock(Block.class);
		when(normalSign.getTypeId()).thenReturn(63);
		when(normalSign.getState()).thenReturn(dummySign);
		
		wallSign = PowerMockito.mock(Block.class);
		when(wallSign.getTypeId()).thenReturn(68);
		when(wallSign.getState()).thenReturn(dummySign);
	}
	
	@Test
	public void testGetSignState() {
		Util util = new Util();
		
		Sign sign = util.getSignState(notASign);
		assertNull(sign);
		
		sign = util.getSignState(normalSign);
		assertNotNull(sign);
		
		sign = util.getSignState(wallSign);
		assertNotNull(sign);
	}
}
