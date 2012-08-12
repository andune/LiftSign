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

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.bukkit.Location;
import org.bukkit.block.Sign;

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
	
	@Inject
	public SignCache(SignFactory factory) {
		this.factory = factory;
	}
	
	public SignDetail getCachedSignDetail(Location location) {
		final String locationString = getLocationKey(location);
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
		SignDetail signDetail = getCachedSignDetail(sign.getLocation());

		// in theory shouldn't happen, but deal with this situation if it does
		if( signDetail != null ) {
			invalidateCacheLocation(signDetail);
			signs.remove(getLocationKey(signDetail.getLocation()));
		}
		
//		signDetail = new SignDetail(this, sign);
		signDetail = factory.create(sign, null);
		signs.put(getLocationKey(signDetail), signDetail);
		return signDetail;
	}
	
	/** Same as {@link #newSignCreated(Sign)} except the SignDetail object
	 * has already been created and is passed in. 
	 * 
	 * @param signDetail
	 * @return
	 */
	public SignDetail newSignCreated(SignDetail signDetail) {
		SignDetail cached = signs.get(getLocationKey(signDetail));

		// in theory shouldn't happen, but deal with this situation if it does
		if( cached != null ) {
			invalidateCacheLocation(cached);
			signs.remove(getLocationKey(cached));
		}
		
		signs.put(getLocationKey(signDetail), signDetail);
		return signDetail;
	}

	public void existingSignDestroyed(Sign sign) {
		SignDetail signDetail = getCachedSignDetail(sign.getLocation());
		if( signDetail != null ) {
			signs.remove(getLocationKey(signDetail));
			if( signDetail.isLiftSign() )
				invalidateCacheLocation(signDetail);
		}
	}
	
	private void invalidateCacheLocation(SignDetail signDetail) {
		for(SignDetail val : signs.values()) {
			val.clearCache(signDetail);
		}
	}
	
	private String getLocationKey(final Location l) {
		return l.getWorld().getName()+","+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ();
	}
	private String getLocationKey(final SignDetail signDetail) {
		return getLocationKey(signDetail.getLocation());
	}
}
