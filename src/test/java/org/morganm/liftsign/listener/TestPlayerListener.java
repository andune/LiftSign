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
package org.morganm.liftsign.listener;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.morganm.liftsign.PermissionCheck;
import org.morganm.liftsign.SignCache;
import org.morganm.liftsign.SignFactory;
import org.morganm.liftsign.Util;
import org.morganm.liftsign.testutil.TestSignFactory;
import org.morganm.liftsign.testutil.TestUtility;
import org.morganm.mBukkitLib.Logger;
import org.morganm.mBukkitLib.Teleport;
import org.morganm.mBukkitLib.i18n.MessageUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author morganm
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SignChangeEvent.class, PlayerInteractEvent.class })
public class TestPlayerListener {
	private TestUtility testUtility;
	private Logger log;
	private Util util;
	private MessageUtil msgUtil;
	private TestSignFactory testSignFactory;
	private World mockWorld;
	private Sign signUp1;
	private Sign signDown1;
	private Sign signNormalSign;
	private Sign signUp2;
	
	@Before
	public void setup() {
		testUtility = new TestUtility();
		log = PowerMockito.mock(Logger.class);
		msgUtil = PowerMockito.mock(MessageUtil.class);
		util = new Util(msgUtil);
		testSignFactory = new TestSignFactory(log, util);
		
		mockWorld = testUtility.createPopulatedMockWorld(4,7,4);
		signUp1 = testUtility.newSign(mockWorld, 1,1,1, new String[] {"","[Lift up]","",""}, false);
		signDown1 = testUtility.newSign(mockWorld, 1,5,1, new String[] {"","[Lift down]","",""}, false);
		signUp2 = testUtility.newSign(mockWorld, 2,1,1, new String[] {"","[Lift up]","",""}, false);
		signNormalSign = testUtility.newSign(mockWorld, 3,1,1, new String[] {"Normal","Sign","",""}, false);
	}
	
	@After
	public void teardown() {
		testUtility.cleanupMockWorld(mockWorld);
	}

	@Test
	public void testLeftClickSign() {
		// setup
		InstanceTestObjects ito = new InstanceTestObjects();
		Block b = signUp1.getBlock();
		when(ito.event.getClickedBlock()).thenReturn(b);
		
		// test
		ito.listener.onPlayerInteract(ito.event);
		
		// verify
		verify(ito.event).getAction();
		verifyNoMoreInteractions(ito.event);
	}
	
	@Test
	public void testRightClickNonSignBlock() {
		// setup
		InstanceTestObjects ito = new InstanceTestObjects();
		Block b = mockWorld.getBlockAt(0,0,0);
		when(ito.event.getClickedBlock()).thenReturn(b);
		when(ito.event.getAction()).thenReturn(Action.RIGHT_CLICK_BLOCK);
		when(ito.teleport.isSafeBlock(any(Block.class), anyInt())).thenReturn(true);
		when(ito.perm.canUseNormalLift(any(CommandSender.class))).thenReturn(true);
		
		// test
		ito.listener.onPlayerInteract(ito.event);
		
		// verify
		// TODO
	}
	
	@Test
	public void testRightClickNormalSign() {
		// setup
		InstanceTestObjects ito = new InstanceTestObjects();
		Block b = signNormalSign.getBlock();
		when(ito.event.getClickedBlock()).thenReturn(b);
		when(ito.event.getAction()).thenReturn(Action.RIGHT_CLICK_BLOCK);
		when(ito.teleport.isSafeBlock(any(Block.class), anyInt())).thenReturn(true);
		when(ito.perm.canUseNormalLift(any(CommandSender.class))).thenReturn(true);
		
		// test
		ito.listener.onPlayerInteract(ito.event);
		
		// verify
		// TODO
	}
	
	@Test
	public void testRightClickLiftUpNoPermission() {
		// setup
		InstanceTestObjects ito = new InstanceTestObjects();
		Block b = signUp1.getBlock();
		when(ito.event.getClickedBlock()).thenReturn(b);
		when(ito.event.getAction()).thenReturn(Action.RIGHT_CLICK_BLOCK);
		when(ito.teleport.isSafeBlock(any(Block.class), anyInt())).thenReturn(true);
		when(ito.perm.canUseNormalLift(any(CommandSender.class))).thenReturn(false);	// no permission
		Location playerLoc = testUtility.newLocation(mockWorld, 3, 1, 3);
		when(ito.player.getLocation()).thenReturn(playerLoc);
		
		// test
		ito.listener.onPlayerInteract(ito.event);
		
		// verify
		verify(msgUtil).sendLocalizedMessage(any(CommandSender.class), anyString()); // got sent a message
		verify(ito.player, never()).teleport(any(Location.class));	// and did not get teleported
	}
	
	@Test
	public void testRightClickLiftUp() {
		// setup
		InstanceTestObjects ito = new InstanceTestObjects();
		Location playerLoc = testUtility.newLocation(mockWorld, 3, 1, 3);
		ito.setupRightClickWithPermission(signUp1.getBlock(), playerLoc);
		
		// test
		ito.listener.onPlayerInteract(ito.event);
		
		// verify
		verify(ito.player).teleport(any(Location.class));
	}
	
	@Test
	public void testRightClickLiftDown() {
		// setup
		InstanceTestObjects ito = new InstanceTestObjects();
		Location playerLoc = testUtility.newLocation(mockWorld, 3, 5, 3);
		ito.setupRightClickWithPermission(signDown1.getBlock(), playerLoc);
		
		// test
		ito.listener.onPlayerInteract(ito.event);
		
		// verify
		verify(ito.player).teleport(any(Location.class));
	}
	
	@Test
	public void testRightClickLiftUpUnsafe() {
		// setup
		InstanceTestObjects ito = new InstanceTestObjects();
		Block b = signUp1.getBlock();
		when(ito.event.getClickedBlock()).thenReturn(b);
		when(ito.event.getAction()).thenReturn(Action.RIGHT_CLICK_BLOCK);
		when(ito.perm.canUseNormalLift(any(CommandSender.class))).thenReturn(true);
		Location playerLoc = testUtility.newLocation(mockWorld, 3, 1, 3);
		when(ito.player.getLocation()).thenReturn(playerLoc);
		when(ito.teleport.isSafeBlock(any(Block.class), anyInt())).thenReturn(false);	// block not safe
		
		// test
		ito.listener.onPlayerInteract(ito.event);
		
		// verify
		verify(msgUtil).sendLocalizedMessage(any(CommandSender.class), anyString()); // got sent a message
		verify(ito.player, never()).teleport(any(Location.class));	// and did not get teleported
	}
	
	@Test
	public void testRightClickLiftUpNoTarget() {
		// setup
		InstanceTestObjects ito = new InstanceTestObjects();
		Location playerLoc = testUtility.newLocation(mockWorld, 3, 1, 3);
		ito.setupRightClickWithPermission(signUp2.getBlock(), playerLoc);
		
		// test
		ito.listener.onPlayerInteract(ito.event);
		
		// verify
		verify(msgUtil).sendLocalizedMessage(any(CommandSender.class), anyString()); // got sent a message
		verify(ito.player, never()).teleport(any(Location.class));	// and did not get teleported
	}
	
	private class InstanceTestObjects {
		final SignFactory factory = testSignFactory.newSignFactory();
		final SignCache cache = new SignCache(factory);
		final PlayerInteractEvent event = PowerMockito.mock(PlayerInteractEvent.class);
		final Player player = PowerMockito.mock(Player.class);
		final PermissionCheck perm = PowerMockito.mock(PermissionCheck.class);
		final Teleport teleport = PowerMockito.mock(Teleport.class);
		final PlayerListener listener;
		
		public InstanceTestObjects() {
			 testSignFactory.setCache(cache);
			 listener = new PlayerListener(cache, factory, perm, teleport, log, util);
			 when(event.getPlayer()).thenReturn(player);
		}
		
		public void setupRightClickWithPermission(final Block b, final Location playerLocation) {
			when(event.getClickedBlock()).thenReturn(b);
			when(event.getAction()).thenReturn(Action.RIGHT_CLICK_BLOCK);
			when(teleport.isSafeBlock(any(Block.class), anyInt())).thenReturn(true);
			when(perm.canUseNormalLift(any(CommandSender.class))).thenReturn(true);
			when(player.getLocation()).thenReturn(playerLocation);
		}
	}
}
