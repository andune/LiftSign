/**
 * 
 */
package org.morganm.liftsign;

import java.util.logging.Logger;

import javax.inject.Inject;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author morganm
 *
 */
public class LiftSign extends JavaPlugin {
	private Logger log;
	private PlayerListener playerListener;
	private BlockListener blockListener;
	private PermissionCheck permCheck;
	private Injector injector = Guice.createInjector();
	
	private int buildNumber = -1;
	
	@Override
	public void onEnable() {
		injector = Guice.createInjector(new LiftSignModule(this));
		injector.injectMembers(this);
		
		getServer().getPluginManager().registerEvents(playerListener, this);
		getServer().getPluginManager().registerEvents(blockListener, this);
		
		log.info("version "+getDescription().getVersion()+", build "+buildNumber+" is enabled");
	}
	
	@Override
	public void onDisable() {
		log.info("version "+getDescription().getVersion()+", build "+buildNumber+" is disabled");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if( label.equals("liftsign") ) {
			sender.sendMessage("test canCreateNormalLift: "+permCheck.canCreateNormalLift(sender));
			return true;
		}

		return false;
	}
	
	@Inject
	public void setLogger(Logger logger) {
		this.log = logger;
	}
	
	@Inject
	public void setPlayerListener(PlayerListener playerListener) {
		this.playerListener = playerListener;
	}
	
	@Inject
	public void setBlockListener(BlockListener blockListener) {
		this.blockListener = blockListener;
	}
	
	@Inject
	public void setPermissionCheck(PermissionCheck permCheck) {
		this.permCheck = permCheck;
	}
}
