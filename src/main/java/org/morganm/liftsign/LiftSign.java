/*******************************************************************************
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (c) 2012 Mark Morgan.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Contributors:
 *     Mark Morgan - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package org.morganm.liftsign;

import java.io.File;

import javax.inject.Inject;

import org.bukkit.plugin.java.JavaPlugin;
import org.morganm.liftsign.listener.BlockListener;
import org.morganm.liftsign.listener.PlayerListener;
import org.morganm.mBukkitLib.Debug;
import org.morganm.mBukkitLib.JarUtils;
import org.morganm.mBukkitLib.Logger;
import org.morganm.mBukkitLib.PermissionSystem;
import org.morganm.mBukkitLib.i18n.LocaleConfig;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author morganm
 *
 */
public class LiftSign extends JavaPlugin {
	private Logger log;
	private Debug debug;
	private PlayerListener playerListener;
	private BlockListener blockListener;
	private PermissionSystem permSystem;
	
	private int buildNumber = -1;
	
	@Override
	public void onEnable() {
		JarUtils jarUtil = new JarUtils(this, getLogger(), getFile());
		// copy default config.yml into place if needed
		jarUtil.copyConfigFromJar("config.yml", new File(getDataFolder(), "config.yml"));
		buildNumber = jarUtil.getBuildNumber();
		
		// load localized strings for the configured locale
		LocaleConfig localeConfig = new LocaleConfig(getConfig().getString("locale", "en"),
				getDataFolder(), "liftsign", getFile(), getLogger(), null);
		
		// build object graph using Guice dependency injection. This injects
		// all dependencies for us using the @Inject annotations
		Injector injector = Guice.createInjector(new LiftSignModule(this, localeConfig));
		injector.injectMembers(this);
		
		debug.setLogFileName("plugins/LiftSign/debug.log");
		debug.setDebug(getConfig().getBoolean("debug", false));
		
		permSystem.setupPermissions();
		
		getServer().getPluginManager().registerEvents(playerListener, this);
		getServer().getPluginManager().registerEvents(blockListener, this);
		
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
	public void setDebug(Debug debug) {
		this.debug = debug;
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
	public void setPermissionSystem(PermissionSystem permSystem) {
		this.permSystem = permSystem;
	}
}
