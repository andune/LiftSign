/**
 * 
 */
package org.morganm.liftsign;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.morganm.mBukkitLib.General;
import org.morganm.mBukkitLib.Logger;

import com.google.inject.assistedinject.Assisted;

/** Object to track details about a known sign. Details are stored so they
 * only have to be processed once.
 * 
 * @author morganm
 *
 */
public class SignDetail {
	private final SignCache cache;
	private final General general;
	private final Logger log;
	private final Util util;
	
	final private Location location;
	final private String locationString;
	
	// world, x y, and z are cached internally for efficient vertical processing
	final private World world;
	final private int x;
	final private int y;
	final private int z;
	
	private boolean isLiftSign;
	private boolean isLiftUp;	// true = up, false = down
	private SignDetail targetLift;
	
	/** Create a new SignDetail object.
	 * 
	 * @param cache
	 * @param general
	 * @param log
	 * @param util
	 * @param sign the Sign backing this SignDetail object
	 * @param lines intended to be used when called from SignChangeEvent, so
	 * the new lines can be passed in even though they aren't part of the block
	 * state yet. Leave null if you want this method to just use the lines from
	 * the Sign state object.
	 */
	@Inject
	public SignDetail(SignCache cache, General general, Logger log, Util util,
			@Assisted Sign sign, @Assisted @Nullable String[] lines) {
		this.cache = cache;
		this.log = log;
		this.util = util;
		this.general = general;
		
		this.location = sign.getLocation();
		this.locationString = this.general.shortLocationString(this.location);
		this.world = this.location.getWorld();
		this.x = this.location.getBlockX();
		this.y = this.location.getBlockY();
		this.z = this.location.getBlockZ();
		
		// fill lines from Sign if it is null
		if( lines == null )
			lines = sign.getLines();
		
		isLiftSign = false;
		log.debug("new SignDetail object. lines=",lines);
		if( lines != null && lines.length > 1 ) {
			log.debug("lines[1]=",lines[1]);
			if( lines[1].equals("[Lift up]") ) {
				log.debug("Sign is lift up");
				isLiftUp = true;
				isLiftSign = true;
			}
			else if( lines[1].equals("[Lift down]") ) {
				log.debug("Sign is lift up");
				isLiftUp = false;
				isLiftSign = true;
			}
			else {
				log.debug("Sign is not a lift sign");
			}
			// TODO: add "null lift" (target-only)
		}
	}
	
	/** Check a given sign to see if it's a possible target match for this
	 * sign. To be a possible match, the given sign must be on the same
	 * world and at the same x/z as this sign, as well as vertically above
	 * or below depending on the sign direction.
	 * 
	 * Note a return of true does NOT mean the given sign is exactly where
	 * this sign would send a player; it's possible there is another sign
	 * vertically in between the two. This just means that, in the absence
	 * of another sign in between, the given sign is a POSSIBLE target for
	 * this current sign.
	 * 
	 * @param sign
	 * @return
	 */
	public boolean isPossibleTargetMatch(SignDetail sign) {
		// if this current sign is not a lift sign, then it's impossible
		// for it to target the given sign.
		if( !isLiftSign ) {
			log.debug("isPossibleTargetMatch(): sign is not a lift sign");
			return false;
		}
		
		// we can only target other lift signs.
		if( !sign.isLiftSign() )
			return false;
		
		Location location = sign.getLocation();
		if( !location.getWorld().equals(world) ||
				location.getBlockX() != x ||
				location.getBlockZ() != z ) {
			return false;
		}
		
		// if the sign is appropriately above or below us, then it is
		// a possible target
		if( isLiftUp && location.getBlockY() > y ) {
			return true;
		}
		else if( !isLiftUp && location.getBlockY() < y ) {
			return true;
		}
		
		return false;
	}
	
	/** Return the target lift of this sign, if any (can be null).
	 * 
	 * @return
	 */
	public SignDetail getTargetLift() {
		if( !isLiftSign ) {
			log.debug("getTargetLift(): sign is not a lift sign");
			return null;
		}

		if( targetLift == null ) {
			log.debug("getTargetLift(): targetLift is null, looking for new target");
			Block b = getLocation().getBlock();
			BlockFace face = BlockFace.UP;
			int max = 254;
			if( !isLiftUp ) {
				face = BlockFace.DOWN;
				max = 1;
			}
			log.debug("getTargetLift(): isLiftUp=",isLiftUp,", face=",face,", max=",max);
			
			Block next = b;
			// loop up or down looking for the first possible target sign.
			// if/when we find it, store that as our target and break loop
			for(int i=y; i != max;) {
				next = next.getRelative(face);
				
				Sign sign = util.getSignState(next);
				if( sign != null ) {
					SignDetail detail = cache.getCachedSignDetail(sign);
					
					// if no cached object exists for this sign, then create one
					if( detail == null )
						detail = cache.newSignCreated(sign);
					
					if( isPossibleTargetMatch(detail) ) {
						targetLift = detail;
						break;
					}
				}
				
				// increment/decrement depending on direction
				if( isLiftUp )
					i++;
				else
					i--;
			}
		}
		
		return targetLift;
	}
	
	/** Called for each cached SignDetail object when a new Lift Sign has
	 * been created. It should invalidate any cached target on the same
	 * vertical path so that the up/down locations are recalculated on
	 * next use.
	 * 
	 * @param signDetail
	 */
	public void clearCache(SignDetail signDetail) {
		// don't do anything if we aren't a lift sign
		if( signDetail.isLiftSign )
			return;

		// are we in the same vertical location as given sign? if yes, reset cache object
		if( signDetail.world == world &&
				signDetail.x == x &&
				signDetail.z == z ) {
			targetLift = null;
		}
	}
	
	public World getWorld() {
		return world;
	}
	public boolean isLiftSign() {
		return isLiftSign;
	}
	public Location getLocation() {
		return location;
	}
	public String getLocationString() {
		return locationString;
	}
}
