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
package org.morganm.liftsign.listener;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.morganm.liftsign.PermissionCheck;
import org.morganm.liftsign.SignCache;
import org.morganm.liftsign.SignDetail;
import org.morganm.liftsign.SignFactory;
import org.morganm.liftsign.Util;
import org.morganm.liftsign.testutil.TestUtility;
import org.morganm.mBukkitLib.Logger;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author morganm
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SignChangeEvent.class })
public class TestBlockListener {
	private TestUtility testUtility;
	private Logger log;
	private Util util;
	
	@Before
	public void setup() {
		testUtility = new TestUtility();

		// best practice would be to create a mock for this, but at this
		// time the utility object is simple and it has it's own tests
		// for validity, so saves a lot of mock code dancing to just use
		// it directly.
		this.util = new Util();
		
		log = PowerMockito.mock(Logger.class);
//		log = testUtility.systemOutLogger();
	}
	
	@Test
	public void testSignBreakClearsCache() {
		final SignCache cache = PowerMockito.mock(SignCache.class);
		final World mockWorld = testUtility.createPopulatedMockWorld(1,1,1);
		final String[] lines = new String[] {"","[Lift up]","",""};
		
		// now create a sign block to test with
		final Block signBlock = testUtility.newSign(mockWorld, 0,0,0, lines, false).getBlock();
		final BlockBreakEvent event = PowerMockito.mock(BlockBreakEvent.class);
		when(event.getBlock()).thenReturn(signBlock);
		
		BlockListener blockListener = new BlockListener(cache,
				PowerMockito.mock(PermissionCheck.class),
				PowerMockito.mock(SignFactory.class), log, util);
		blockListener.onBlockBreak(event);
		
		verify(cache).existingSignDestroyed(any(Sign.class));
	}
	
	@Test
	public void testNoAccessSignCreate() {
		// create permCheck that denies the check
		final PermissionCheck perm = PowerMockito.mock(PermissionCheck.class);
		when(perm.canCreateNormalLift(any(CommandSender.class))).thenReturn(false);

		SignChangeObjects sco = commonSignChangeSetup(perm);
		sco.blockListener.onSignChange(sco.event);
		
		verify(sco.event).setCancelled(true);
		verify(sco.player).sendMessage(anyString());
		verify(sco.factory).create(any(Sign.class), any(String[].class));
		verify(sco.cache, never()).newSignCreated(any(Sign.class));
	}
	
	@Test
	public void testAccessAllowedSignCreate() {
		// create permCheck that allows the check
		final PermissionCheck perm = PowerMockito.mock(PermissionCheck.class);
		when(perm.canCreateNormalLift(any(CommandSender.class))).thenReturn(true);

		SignChangeObjects sco = commonSignChangeSetup(perm);
		sco.blockListener.onSignChange(sco.event);
		
		verify(sco.event, never()).setCancelled(true);
		verify(sco.factory).create(any(Sign.class), any(String[].class));
		verify(sco.cache).newSignCreated(any(SignDetail.class));
	}
	
	private class SignChangeObjects {
		final BlockListener blockListener;
		final SignChangeEvent event;
		final SignFactory factory;
		final SignCache cache;
		final Player player;
		public SignChangeObjects(BlockListener blockListener, SignChangeEvent event,
				SignFactory factory, SignCache cache, Player player) {
			this.blockListener = blockListener;
			this.event = event;
			this.factory = factory;
			this.cache = cache;
			this.player = player;
		}
	}
	/** Setup code common to signChange tests.
	 * 
	 * @param perm
	 * @return
	 */
	private SignChangeObjects commonSignChangeSetup(final PermissionCheck perm) {
		final SignCache cache = PowerMockito.mock(SignCache.class);
		final World mockWorld = testUtility.createPopulatedMockWorld(1,1,1);
		final Player player = testUtility.createDummyPlayer();
		final String[] lines = new String[] {"","[Lift up]","",""};
		
		// now create a sign block to test with
		final Block signBlock = testUtility.newSign(mockWorld, 0,0,0, lines, false).getBlock();
		final SignChangeEvent event = mockSignChangeEvent(signBlock, lines, player);
		
		final SignFactory factory = PowerMockito.mock(SignFactory.class);
		when(factory.create(any(Sign.class), any(String[].class)))
		.thenAnswer(new Answer<SignDetail>() {
			public SignDetail answer(InvocationOnMock invocation) throws Throwable {
				SignDetail signDetail = PowerMockito.mock(SignDetail.class);
				when(signDetail.isLiftSign()).thenReturn(true);
				return signDetail;
			}
			});
		
		BlockListener blockListener = new BlockListener(cache, perm, factory, log, util);
		
		return new SignChangeObjects(blockListener, event, factory, cache, player);
	}
	
	private SignChangeEvent mockSignChangeEvent(final Block block, final String[] lines, final Player player) {
		SignChangeEvent e = PowerMockito.mock(SignChangeEvent.class);
		when(e.getBlock()).thenReturn(block);
		when(e.isCancelled()).thenReturn(false);
		when(e.getPlayer()).thenReturn(player);
		when(e.getLines()).thenReturn(lines);
		return e;
	}
	
	/* Early testing/learning, not really a test
	@Test
	public void testing() {
		final World mockWorld = testUtility.createPopulatedMockWorld(2,5,2);
		int x = 1;
		int z = 1;
		Block b = mockWorld.getBlockAt(x, 0, z);
		assertNotNull(b);
		assertEquals(Material.BEDROCK.getId(), b.getTypeId());
		
		b = mockWorld.getBlockAt(x, 4, z);
		assertNotNull(b);
		assertEquals(1, b.getTypeId());
		
		b.setType(Material.DIRT);
		assertEquals(Material.DIRT.getId(), b.getTypeId());
	}
	*/
}
