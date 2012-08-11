/**
 * 
 */
package org.morganm.liftsign;

import javax.inject.Inject;

import org.bukkit.command.CommandSender;
import org.morganm.liftsign.util.PermissionSystem;

/** Centralized permission checks.
 * 
 * @author morganm
 *
 */
public class PermissionCheck {
	private static final String BASE = "signlift";
	private static final String CREATE_NORMAL = BASE + ".normal.create";
	private static final String USE_NORMAL = BASE + ".normal.use";
	
	private PermissionSystem permissionSystem;
	
	@Inject
	public PermissionCheck(PermissionSystem permissionSystem) {
		this.permissionSystem = permissionSystem;
	}
	
	public boolean canUseNormalLift(CommandSender sender) {
		return permissionSystem.has(sender, USE_NORMAL);
	}
	
	public boolean canCreateNormalLift(CommandSender sender) {
		return permissionSystem.has(sender, CREATE_NORMAL);
	}
}
