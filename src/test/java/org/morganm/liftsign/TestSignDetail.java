/**
 * 
 */
package org.morganm.liftsign;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.junit.Before;
import org.junit.Test;
import org.morganm.liftsign.testutil.TestSignFactory;
import org.morganm.liftsign.testutil.TestUtility;
import org.morganm.mBukkitLib.Logger;
import org.powermock.api.mockito.PowerMockito;

/**
 * @author morganm
 *
 */
public class TestSignDetail {
	private TestUtility testUtility;
	private Logger log;
	private Util util;
	private TestSignFactory testSignFactory;
	private World mockWorld;
	private Sign signUp;
	private Sign signDown;
	private Sign normalSign;
	
	@Before
	public void setup() {
		testUtility = new TestUtility();
		log =  PowerMockito.mock(Logger.class);
//		log = testUtility.systemOutLogger();
		util = new Util();
		testSignFactory = new TestSignFactory(log, util);
		
		mockWorld = testUtility.createPopulatedMockWorld(3,10,3);
		signUp = testUtility.newSign(mockWorld, 1,2,1, new String[] {"","[Lift up]","",""}, false);
		signDown = testUtility.newSign(mockWorld, 1,5,1, new String[] {"","[Lift down]","",""}, false);
		normalSign = testUtility.newSign(mockWorld, 1,1,1, new String[] {"Blah","Foo","",""}, false);
		
//		cache = PowerMockito.mock(SignCache.class);
		
	}

	@Test
	public void testIsLiftSign() {
		final SignFactory factory = testSignFactory.newSignFactory();
		final SignCache cache = new SignCache(factory);
		testSignFactory.setCache(cache);
		
		SignDetail signDetailSignUp = new SignDetail(cache, log, util, signUp, null);
		assertTrue(signDetailSignUp.isLiftSign());
		SignDetail signDetailSignDown = new SignDetail(cache, log, util, signDown, null);
		assertTrue(signDetailSignDown.isLiftSign());
		SignDetail signDetailUpNormalSign = new SignDetail(cache, log, util, normalSign, null);
		assertFalse(signDetailUpNormalSign.isLiftSign());
		Sign sign4 = testUtility.newSign(mockWorld, 1,2,1, new String[] {"","[Lift]","",""}, false);
		SignDetail signDetail4 = cache.newSignCreated(sign4);
		assertTrue(signDetail4.isLiftSign());
	}
	
	@Test
	public void testIsPossibleTargetMatch() {
		final SignFactory factory = testSignFactory.newSignFactory();
		final SignCache cache = new SignCache(factory);
		testSignFactory.setCache(cache);
		SignDetail signDetailSignUp = new SignDetail(cache, log, util, signUp, null);
		SignDetail signDetailSignDown = new SignDetail(cache, log, util, signDown, null);
		SignDetail signDetailNormalSign = new SignDetail(cache, log, util, normalSign, null);
		
		assertTrue(signDetailSignUp.isPossibleTargetMatch(signDetailSignDown));
		assertFalse(signDetailSignDown.isPossibleTargetMatch(signDetailNormalSign));
	}
	
	@Test
	public void testGetTargetLift() {
		final SignFactory factory = testSignFactory.newSignFactory();
		final SignCache cache = new SignCache(factory);
		testSignFactory.setCache(cache);
		SignDetail signDetailSignUp = cache.newSignCreated(signUp);
		SignDetail signDetailSignDown = cache.newSignCreated(signDown);
		
		assertEquals(signDetailSignDown, signDetailSignUp.getTargetLift());
		assertEquals(signDetailSignUp, signDetailSignDown.getTargetLift());
	}
	
	@Test
	public void testClearCache() {
		Sign sign1 = testUtility.newSign(mockWorld, 2,2,1, new String[] {"","[Lift up]","",""}, false);
		Sign sign2 = testUtility.newSign(mockWorld, 2,4,1, new String[] {"","[Lift down]","",""}, false);
		Sign sign3 = testUtility.newSign(mockWorld, 2,5,1, new String[] {"","[Lift down]","",""}, false);
		Sign sign4 = testUtility.newSign(mockWorld, 2,3,1, new String[] {"Not a","lift sign","",""}, false);
		
		final SignFactory factory = testSignFactory.newSignFactory();
		final SignCache cache = new SignCache(factory);
		testSignFactory.setCache(cache);
		SignDetail signDetailSign1 = cache.newSignCreated(sign1);
		SignDetail signDetailSign2 = cache.newSignCreated(sign2);
		
		// make sure sign1 is cached as pointing to sign2
		assertEquals(signDetailSign2, signDetailSign1.getTargetLift());
		
		// change sign2 to stone block
		Location l = sign2.getLocation();
		Block b = sign2.getBlock();
		b.setType(Material.STONE);
		assertEquals(Material.STONE, mockWorld.getBlockAt(l).getType());
		
		// sign1 should still be pointed at "sign2" because it is cached,
		// even though it's now a stone block
		assertEquals(signDetailSign2, signDetailSign1.getTargetLift());
		
		// now invoke sign1's clearCache as if sign4 were destroyed (should do nothing)
		SignDetail signDetailSign4 = cache.newSignCreated(sign4);
		signDetailSign1.clearCache(signDetailSign4);
		assertEquals(signDetailSign2, signDetailSign1.getTargetLift());
		
		// now clear sign1's cache as if sign2 were destroyed (should clear cache)
		signDetailSign1.clearCache(signDetailSign2);
		
		// sign1 should no longer be pointed at sign2 (it should be
		// pointed at sign3 now)
		Location l1 = sign3.getLocation();
		Location l2 = signDetailSign1.getTargetLift().getLocation();
		assertEquals(l1.getWorld(), l2.getWorld());
		assertEquals(l1.getBlockX(), l2.getBlockX());
		assertEquals(l1.getBlockY(), l2.getBlockY());
		assertEquals(l1.getBlockZ(), l2.getBlockZ());
	}
}
