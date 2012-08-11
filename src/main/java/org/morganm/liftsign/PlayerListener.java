/**
 * 
 */
package org.morganm.liftsign;

import javax.inject.Inject;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.morganm.liftsign.util.Teleport;

/**
 * @author morganm
 *
 */
public class PlayerListener implements Listener {
	private final SignCache cache;
	private final PermissionCheck perm;
	private final Teleport teleport = Teleport.getInstance();

	@Inject
	public PlayerListener(SignCache cache, PermissionCheck perm) {
		this.cache = cache;
		this.perm = perm;
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if( event.getAction() != Action.RIGHT_CLICK_BLOCK )
			return;

		Block b = event.getClickedBlock();
		if( b instanceof Sign ) {
			Sign sign = (Sign) b;
			SignDetail signDetail = cache.getCachedSignDetail(sign);
			SignDetail targetLift = null;
			if( signDetail != null )
				targetLift = signDetail.getTargetLift();
			
			// abort if player doesn't have permission
			if( !perm.canUseNormalLift(event.getPlayer()) ) {
				// TODO: print message
				return;
			}

			if( targetLift != null ) {
				World world = targetLift.getWorld();
				Block signBlock = world.getBlockAt(targetLift.getLocation());
				Sign targetSign = (Sign) signBlock;
				
				// check to make sure targetblock is safe
				Location playerLocation = event.getPlayer().getLocation();
				Location finalLocation = null;
				for(int y=targetSign.getY(); y >= targetSign.getY()-1; y--) {
					Location newLocation = new Location(playerLocation.getWorld(), playerLocation.getX(),
							y, playerLocation.getZ(), playerLocation.getYaw(), 
							playerLocation.getPitch());
					Block testBlock = world.getBlockAt(newLocation);
					if( teleport.isSafeBlock(testBlock, 0) )
						finalLocation = newLocation;
				}
				
				if( finalLocation != null ) {
					event.getPlayer().teleport(finalLocation);
				}
				else {
					;	// TODO: print block unsafe message
				}
			}
			else
				;	// TODO: print some "no lift target" message
		}
	}
}
