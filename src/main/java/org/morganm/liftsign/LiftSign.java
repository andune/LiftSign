/**
 * 
 */
package org.morganm.liftsign;

import java.util.logging.Logger;

import javax.inject.Inject;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author morganm
 *
 */
public class LiftSign extends JavaPlugin {
	private Logger log;
//	private SignCache cache;
//	private PermissionSystem permSystem;
//	private PermissionCheck permCheck;
	private PlayerListener playerListener;
	private BlockListener blockListener;
	private Injector injector = Guice.createInjector();
	
	private int buildNumber = -1;
	
	@Override
	public void onEnable() {
		injector = Guice.createInjector(new LiftSignModule(this));
		injector.injectMembers(this);
		
		getServer().getPluginManager().registerEvents(playerListener, this);
		getServer().getPluginManager().registerEvents(blockListener, this);
		
//		injector.get
		
//		permSystem = new PermissionSystem(this, log);
//		permSystem.setupPermissions(true);
//		permCheck = new PermissionCheck(permSystem);
		
//		cache = new SignCache();
//		getServer().getPluginManager().registerEvents(new PlayerListener(cache, permCheck), this);
//		getServer().getPluginManager().registerEvents(new BlockListener(cache, permCheck), this);
		
		log.info("version "+getDescription().getVersion()+", build "+buildNumber+" is enabled");
	}
	
	@Override
	public void onDisable() {
		log.info("version "+getDescription().getVersion()+", build "+buildNumber+" is disabled");
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
}
