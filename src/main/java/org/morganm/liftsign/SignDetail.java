/**
 * 
 */
package org.morganm.liftsign;

import javax.inject.Inject;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.morganm.mBukkitLib.General;

import com.google.inject.assistedinject.Assisted;

/** Object to track details about a known sign. Details are stored so they
 * only have to be processed once.
 * 
 * @author morganm
 *
 */
public class SignDetail {
	final private SignCache cache;
	final private General general;
	
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
	
	@Inject
	public SignDetail(SignCache cache, General general, @Assisted Sign sign) {
		this.cache = cache;
		this.general = general;
		
		this.location = sign.getLocation();
		this.locationString = this.general.shortLocationString(this.location);
		this.world = this.location.getWorld();
		this.x = this.location.getBlockX();
		this.y = this.location.getBlockY();
		this.z = this.location.getBlockZ();
		
		isLiftSign = false;
		String[] lines = sign.getLines();
		if( lines != null && lines.length > 1 ) {
			if( lines[1].equals("[Lift Up]") ) {
				isLiftUp = true;
			}
			else if( lines[1].equals("[Lift Down]") ) {
				isLiftUp = false;
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
		if( !isLiftSign )
			return false;
		
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
		if( isLiftSign )
			return null;

		if( targetLift == null ) {
			Block b = getLocation().getBlock();
			BlockFace face = BlockFace.UP;
			int max = 254;
			if( !isLiftUp ) {
				face = BlockFace.DOWN;
				max = 1;
			}
			
			// loop up or down looking for the first possible target sign.
			// if/when we find it, store that as our target and break loop
			for(int i=y; i != max;) {
				final Block next = b.getRelative(face);
				final int typeId = next.getTypeId();
				if( typeId == Material.SIGN.getId() ||
						typeId == Material.WALL_SIGN.getId() ) {
					Sign sign = (Sign) b;
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
