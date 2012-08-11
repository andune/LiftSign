/**
 * 
 */
package org.morganm.liftsign.listener;

import javax.inject.Inject;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.morganm.liftsign.PermissionCheck;
import org.morganm.liftsign.SignCache;
import org.morganm.liftsign.SignDetail;
import org.morganm.liftsign.SignFactory;
import org.morganm.liftsign.Util;
import org.morganm.mBukkitLib.Logger;
import org.morganm.mBukkitLib.Teleport;

/**
 * @author morganm
 *
 */
public class PlayerListener implements Listener {
	private final SignCache cache;
	private final PermissionCheck perm;
	private final Teleport teleport;
	private final Logger log;
	private final Util util;
	private final SignFactory factory;

	@Inject
	public PlayerListener(SignCache cache, SignFactory factory, PermissionCheck perm, Teleport teleport, Logger log, Util util) {
		this.cache = cache;
		this.factory = factory;
		this.perm = perm;
		this.teleport = teleport;
		this.log = log;
		this.util = util;
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if( event.getAction() != Action.RIGHT_CLICK_BLOCK )
			return;

		Sign sign = util.getSignState(event.getClickedBlock());
		if( sign != null ) {
			final Player p = event.getPlayer();
			
			log.debug("Sign right clicked");
			SignDetail signDetail = cache.getCachedSignDetail(sign);
			if( signDetail == null ) {
				log.debug("Instantiating SignDetail object");
				signDetail = factory.create(sign, null);
			}
			
			SignDetail targetLift = null;
			if( signDetail != null )
				targetLift = signDetail.getTargetLift();
			
			// abort if player doesn't have permission
			if( !perm.canUseNormalLift(p) ) {
				p.sendMessage("No permission.");
				// TODO: print message
				return;
			}
			log.debug("has permission");
			
			Sign targetSign = null;
			World world = null;
			if( targetLift != null ) {
				world = targetLift.getWorld();
				Block signBlock = world.getBlockAt(targetLift.getLocation());
				targetSign = util.getSignState(signBlock);
			}
			
			if( targetLift != null && targetSign != null ) {
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
				log.debug("no target");
				;	// TODO: print some "no lift target" message
		}
	}
}
