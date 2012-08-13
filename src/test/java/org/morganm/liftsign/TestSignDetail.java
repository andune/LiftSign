/**
 * 
 */
package org.morganm.liftsign;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.junit.Before;
import org.junit.Test;
import org.morganm.liftsign.testutil.MockSignFactory;
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
		testSignFactory = new TestSignFactory();
		
		mockWorld = testUtility.createPopulatedMockWorld(2,12,2);
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
	
	private class TestSignFactory {
		private SignCache cache;
		public SignFactory newSignFactory() {
			return new SignFactory() {
				public SignDetail create(Sign sign, String[] lines) {
					return new SignDetail(cache, log, util, sign, lines);
				}
			};
		}
		public void setCache(SignCache cache) { this.cache = cache; }
	}
}
