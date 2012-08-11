/**
 * 
 */
package org.morganm.liftsign.listener;

import javax.inject.Inject;

import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.morganm.liftsign.PermissionCheck;
import org.morganm.liftsign.SignCache;
import org.morganm.liftsign.SignDetail;
import org.morganm.liftsign.SignFactory;
import org.morganm.liftsign.Util;
import org.morganm.mBukkitLib.Logger;

/**
 * @author morganm
 *
 */
public class BlockListener implements Listener {
	private final SignCache cache;
	private final PermissionCheck perm;
	private final SignFactory factory;
	private final Logger log;
	private final Util util;
	
	@Inject
	public BlockListener(SignCache cache, PermissionCheck perm, SignFactory factory, Logger log, Util util) {
		this.cache = cache;
		this.perm = perm;
		this.factory = factory;
		this.log = log;
		this.util = util;
	}

	@EventHandler(ignoreCancelled=true)
	public void onSignChange(SignChangeEvent e) {
		Sign sign = util.getSignState(e.getBlock());
		if( sign != null ) {
			log.debug("Sign change detected");
//			SignDetail signDetail = new SignDetail(cache, (Sign) b);
			String[] lines = e.getLines();
			SignDetail signDetail = factory.create(sign, lines);
			if( signDetail.isLiftSign() ) {
				log.debug("Sign is lift sign");
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
		Sign sign = util.getSignState(e.getBlock());
		if( sign instanceof Sign )
			cache.existingSignDestroyed(sign);
	}
}
