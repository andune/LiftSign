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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import com.andune.minecraft.commonlib.Logger;

/** General utility routines common to multiple tests.
 * 
 * @author andune
 *
 */
public class TestUtility {
	private static int nextInt=1;
	private Map<World, WorldBlocks> worldBlocksMap = new HashMap<World, WorldBlocks>();

	/** Create a minimal mock world.
	 * 
	 * @return
	 */
	public World createMockWorld() {
		World world = PowerMockito.mock(World.class);
		when(world.getName()).thenReturn("MockWorld"+nextInt++);
		return world;
	}
	
	@SuppressWarnings("rawtypes")
	public Player createDummyPlayer() {
		final PlayerState playerState = new PlayerState();
		playerState.name = "Player"+nextInt++;
		playerState.location = null;
		
		final Player player = PowerMockito.mock(Player.class);
		when(player.getName()).thenReturn(playerState.name);
		when(player.getLocation()).thenReturn(playerState.location);
		doAnswer(new Answer() {
			public Object answer(InvocationOnMock invocation) {
				Location location = (Location) invocation.getArguments()[0];
				playerState.location = location;
				return null;
			}})
			.when(player).teleport(any(Location.class));
		
		return player;
	}
	
	/** Create a mock world that implements stateful .getBlockAt() and
	 * a 8x16x8 world (x,y,z).
	 * 
	 * @return
	 */
	public World createPopulatedMockWorld(final int xBlocks, final int yBlocks, final int zBlocks) {
		final World world = PowerMockito.mock(World.class);
		final WorldBlocks worldBlocks = new WorldBlocks(xBlocks, yBlocks, zBlocks);
		worldBlocksMap.put(world, worldBlocks);
		
		for(int x=0; x < xBlocks; x++) {
			for(int y=0; y < yBlocks; y++) {
				for(int z=0; z < zBlocks; z++) {
					worldBlocks.blocks[x][y][z] = newMockBlock(world,x,y,z);
				}
			}
		}
		
		when(world.getMaxHeight()).thenReturn(yBlocks-1);
		when(world.getBlockAt(any(Location.class)))
		.thenAnswer(new Answer<Block>() {
			public Block answer(InvocationOnMock invocation) throws Throwable {
				Location l = (Location) invocation.getArguments()[0];
				Block b = worldBlocks.blocks[l.getBlockX()][l.getBlockY()][l.getBlockZ()];
				return b;
			}
			});
		when(world.getBlockAt(anyInt(), anyInt(), anyInt()))
		.thenAnswer(new Answer<Block>() {
			public Block answer(InvocationOnMock invocation) throws Throwable {
				int x = (Integer) invocation.getArguments()[0];
				int y = (Integer) invocation.getArguments()[1];
				int z = (Integer) invocation.getArguments()[2];
				Block b = worldBlocks.blocks[x][y][z];
				return b;
			}
			});
		return world;
	}
	
	public void cleanupMockWorld(World w) {
		worldBlocksMap.remove(w);
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
		when(loc.getBlock())
		.thenAnswer(new Answer<Block>() {
			public Block answer(InvocationOnMock invocation) throws Throwable {
				Location l = (Location) invocation.getMock();
				WorldBlocks worldBlocks = getWorldBlocksObject(l.getWorld());
				Block b = worldBlocks.blocks[l.getBlockX()][l.getBlockY()][l.getBlockZ()];
				return b;
			}
			});
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
	
	/** Create a new block at a given location and store it in our mock
	 * worldBlocks map.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	@SuppressWarnings("rawtypes")
	private Block newMockBlock(World world, int x, int y, int z) {
		WorldBlocks worldBlocks = getWorldBlocksObject(world);
		if( worldBlocks == null )
			throw new NullPointerException("worldBlocks is null");
		
		Block b = PowerMockito.mock(Block.class);
		
		Material type = null;
		if( y == 0 ) {
			type = Material.BEDROCK;
		}
		else if( (y % 4) == 0 ) {		// every 4th later is all stone
			type = Material.STONE;
		}
		else if( x == 0 && z == 0 ) {	// 0,y,0 is vertical stone column
			type = Material.STONE;
		}
		else {							// everything else is air
			type = Material.AIR;
		}
		
		worldBlocks.blocks[x][y][z] = b;
		worldBlocks.blockType[x][y][z] = type;
		
		final Location loc = newLocation(world,x,y,z);
		when(b.getLocation()).thenReturn(loc);
		when(b.getType())
		.thenAnswer(new Answer<Material>() {
			public Material answer(InvocationOnMock invocation) throws Throwable {
				Block b = (Block) invocation.getMock();
				Location l = b.getLocation();
				WorldBlocks worldBlocks = getWorldBlocksObject(l.getWorld());
				return worldBlocks.blockType[l.getBlockX()][l.getBlockY()][l.getBlockZ()];
			}
			});
		when(b.getTypeId())
		.thenAnswer(new Answer<Integer>() {
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				Block b = (Block) invocation.getMock();
				Location l = b.getLocation();
				WorldBlocks worldBlocks = getWorldBlocksObject(l.getWorld());
				return worldBlocks.blockType[l.getBlockX()][l.getBlockY()][l.getBlockZ()].getId();
			}
			});
		when(b.getRelative(any(BlockFace.class)))
		.thenAnswer(new Answer<Block>() {
			public Block answer(InvocationOnMock invocation) throws Throwable {
				Block b = (Block) invocation.getMock();
				Location l = b.getLocation();
				int y = l.getBlockY();
				
				BlockFace face = (BlockFace) invocation.getArguments()[0];
				if( BlockFace.UP.equals(face) )
					y++;
				else if( BlockFace.DOWN.equals(face) )
					y--;
//				System.out.print("getRelative: y="+y);
				
				WorldBlocks worldBlocks = getWorldBlocksObject(l.getWorld());
				return worldBlocks.blocks[l.getBlockX()][y][l.getBlockZ()];
			}
			});
		
		doAnswer(new Answer() {
			public Object answer(InvocationOnMock invocation) {
				Block b = (Block) invocation.getMock();
				Location l = b.getLocation();
				Material material = (Material) invocation.getArguments()[0];
				WorldBlocks worldBlocks = getWorldBlocksObject(l.getWorld());
				worldBlocks.blockType[l.getBlockX()][l.getBlockY()][l.getBlockZ()] = material;
				return null;
			}})
			.when(b).setType(any(Material.class));
		doAnswer(new Answer() {
			public Object answer(InvocationOnMock invocation) {
				Integer id = (Integer) invocation.getArguments()[0];
				Block b = (Block) invocation.getMock();
				b.setType(Material.getMaterial(id));
				return null;
			}})
			.when(b).setTypeId(anyInt());
		
		return b;
	}
	
	public Sign newSign(World world, int x, int y, int z, final String[] lines, boolean wallSign) {
		final Block block = newMockBlock(world, x, y, z);
		
		Material material = Material.SIGN_POST;
		if( wallSign )
			material = Material.WALL_SIGN;
		block.setType(material);
		
		final Location loc = block.getLocation();
		final Sign sign = PowerMockito.mock(Sign.class);
		when(sign.getBlock()).thenReturn(block);
		when(sign.getLines()).thenReturn(lines);
		when(sign.getLocation()).thenReturn(loc);
		when(sign.getY()).thenReturn(y);
		when(sign.getLine(anyInt()))
		.thenAnswer(new Answer<String>() {
			public String answer(InvocationOnMock invocation) throws Throwable {
				int index = (Integer) invocation.getArguments()[0];
				Sign sign = (Sign) invocation.getMock();
				return sign.getLines()[index];
			}
			});
		
		when(block.getState()).thenReturn(sign);
		
		return sign;
	}
	
	/** Return a Logger that spits out any calls to System.out. Useful for
	 * debugging tests.
	 * 
	 * @return
	 */
	public Logger systemOutLogger() {
		Logger log = PowerMockito.mock(Logger.class);
		@SuppressWarnings("rawtypes")
		final Answer answer = new Answer() {
			public Object answer(InvocationOnMock invocation) {
				StringBuffer sb = new StringBuffer();
				for(Object o : invocation.getArguments())
					sb.append(o.toString());
				System.out.println(sb.toString());
				return null;
			}};
			
		doAnswer(answer).when(log).debug(anyString());
		doAnswer(answer).when(log).debug(anyString(), anyObject());
		doAnswer(answer).when(log).debug(anyString(), anyObject(), anyObject());
		doAnswer(answer).when(log).debug(anyString(), anyVararg());
		doAnswer(answer).when(log).info(anyString());
		doAnswer(answer).when(log).info(anyString(), anyObject());
		doAnswer(answer).when(log).info(anyString(), anyObject(), anyObject());
		doAnswer(answer).when(log).info(anyString(), anyVararg());
		
		return log;
	}

	private WorldBlocks getWorldBlocksObject(World world) {
		return worldBlocksMap.get(world);
	}
	
	private class WorldBlocks {
		final Block[][][] blocks;
		final Material[][][] blockType;
		
		public WorldBlocks(final int xBlocks, final int yBlocks, final int zBlocks) {
			blocks = new Block[xBlocks][yBlocks][zBlocks];
			blockType = new Material[xBlocks][yBlocks][zBlocks];
		}
	}
	
	private class PlayerState {
		Location location;
		String name;
	}
}
