/**
 * 
 */
package org.morganm.liftsign;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

/** Common utility methods for LiftSign classes.
 * 
 * @author morganm
 *
 */
public class Util {
	/** If a given block is a sign, this will return the Sign state object.
	 * If it's not a sign, null will be returned.
	 * 
	 * @param b
	 * @return
	 */
	public Sign getSignState(final Block b) {
		Sign sign = null;
		
		final int typeId = b.getTypeId();
		if( typeId == Material.SIGN.getId() || typeId == Material.WALL_SIGN.getId() ) {
			BlockState bs = b.getState();
			sign = (Sign) bs;
		}
		
		return sign;
	}
}
