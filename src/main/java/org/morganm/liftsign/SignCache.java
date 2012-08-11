/**
 * 
 */
package org.morganm.liftsign;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.bukkit.block.Sign;
import org.morganm.mBukkitLib.General;

/** Class for keeping track of known signs and their lift status. This avoids
 * additional processing as signs are clicked by only processing sign details
 * once.
 * 
 * General strategy: When a lift sign is placed or destroyed, we iterate through
 * existing lifts to find any possible matches for that lift and do the linking.
 * 
 * @author morganm
 *
 */
public class SignCache {
	private final Map<String, SignDetail> signs = new HashMap<String, SignDetail>();
	private final SignFactory factory;
	private final General general;
	
	@Inject
	public SignCache(SignFactory factory, General general) {
		this.factory = factory;
		this.general = general;
	}
	
	public SignDetail getCachedSignDetail(Sign sign) {
		final String locationString = general.shortLocationString(sign.getLocation());
		SignDetail signDetail = signs.get(locationString);
		return signDetail;
	}
	
	/** To be called when a new sign is created or when an existing sign
	 * is noticed in-game and we want to tell the cache about it.
	 * 
	 * @param sign
	 * @return
	 */
	public SignDetail newSignCreated(Sign sign) {
		SignDetail signDetail = getCachedSignDetail(sign);

		// in theory shouldn't happen, but deal with this situation if it does
		if( signDetail != null ) {
			invalidateCacheLocation(signDetail);
			signs.remove(signDetail.getLocationString());
		}
		
//		signDetail = new SignDetail(this, sign);
		signDetail = factory.create(sign, null);
		signs.put(signDetail.getLocationString(), signDetail);
		return signDetail;
	}
	
	/** Same as {@link #newSignCreated(Sign)} except the SignDetail object
	 * has already been created and is passed in. 
	 * 
	 * @param signDetail
	 * @return
	 */
	public SignDetail newSignCreated(SignDetail signDetail) {
		SignDetail cached = signs.get(signDetail.getLocationString());

		// in theory shouldn't happen, but deal with this situation if it does
		if( cached != null ) {
			invalidateCacheLocation(cached);
			signs.remove(cached.getLocationString());
		}
		
		signs.put(signDetail.getLocationString(), signDetail);
		return signDetail;
	}

	public void existingSignDestroyed(Sign sign) {
		SignDetail signDetail = getCachedSignDetail(sign);
		if( signDetail != null ) {
			signs.remove(signDetail.getLocationString());
			if( signDetail.isLiftSign() )
				invalidateCacheLocation(signDetail);
		}
	}
	
	private void invalidateCacheLocation(SignDetail signDetail) {
		for(SignDetail val : signs.values()) {
			val.clearCache(signDetail);
		}
	}
}
