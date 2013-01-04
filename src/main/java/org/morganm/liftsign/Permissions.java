/**
 * 
 */
package org.morganm.liftsign;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.morganm.mBukkitLib.PermissionSystem;

/** Wrapper for mBukkitLib PermissionSystem, since Guice scans all member
 * variables looking for @Inject annotations and causes a CNFE on
 * PermissionSystem since it references plugins that may not exist at
 * runtime (such as Vault and WEPIF).
 * 
 * @author morganm
 *
 */
@Singleton
public class Permissions {
	private final Plugin plugin;
	private PermissionSystem permSystem;
	
	@Inject
	public Permissions(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public void init() {
		permSystem = new PermissionSystem(plugin, plugin.getLogger());
		permSystem.setupPermissions();
	}

	public boolean has(CommandSender sender, String permission) {
		return permSystem.has(sender, permission);
	}
}
