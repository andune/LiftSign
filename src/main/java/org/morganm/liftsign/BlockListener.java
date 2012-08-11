/**
 * 
 */
package org.morganm.liftsign;

import javax.inject.Inject;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

/**
 * @author morganm
 *
 */
public class BlockListener implements Listener {
	private final SignCache cache;
	private final PermissionCheck perm;
	private final SignFactory factory;
	
	@Inject
	public BlockListener(SignCache cache, PermissionCheck perm, SignFactory factory) {
		this.cache = cache;
		this.perm = perm;
		this.factory = factory;
	}

	@EventHandler(ignoreCancelled=true)
	public void onSignChange(SignChangeEvent e) {
		Block b = e.getBlock();
		if( b instanceof Sign ) {
//			SignDetail signDetail = new SignDetail(cache, (Sign) b);
			SignDetail signDetail = factory.create((Sign) b);
			if( signDetail.isLiftSign() ) {
				if( !perm.canCreateNormalLift(e.getPlayer()) ) {
					// TODO: print error
					e.setCancelled(true);
				}
			}
			
			if( !e.isCancelled() )
				cache.newSignCreated(signDetail);
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onBlockBreak(BlockBreakEvent e) {
		Block b = e.getBlock();
		if( b instanceof Sign )
			cache.existingSignDestroyed((Sign) b);
	}
}
