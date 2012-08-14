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
