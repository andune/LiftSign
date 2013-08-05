/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2013 Andune (andune.alleria@gmail.com)
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
 */
/**
 *
 */
package com.andune.liftsign;

import com.andune.liftsign.listener.BlockListener;
import com.andune.liftsign.listener.PlayerListener;
import com.andune.minecraft.commonlib.JarUtils;
import com.andune.minecraft.commonlib.Logger;
import com.andune.minecraft.commonlib.LoggerFactory;
import com.andune.minecraft.commonlib.PermissionSystemImpl;
import com.andune.minecraft.commonlib.i18n.LocaleConfig;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

/**
 * @author andune
 */
public class LiftSign extends JavaPlugin {
    private Logger log;
    private PlayerListener playerListener;
    private BlockListener blockListener;
    private PermissionSystemImpl permSystem;

    private String buildNumber = "unknown";

    @Override
    public void onEnable() {
        LoggerFactory.setLoggerPrefix("[LiftSign] ");
        log = LoggerFactory.getLogger(LiftSign.class);

        JarUtils jarUtil = new JarUtils(getDataFolder(), getFile());
        // copy default config.yml into place if needed
        try {
            jarUtil.copyConfigFromJar("config.yml", new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            log.warn("Error copying default config file into place: " + e.getMessage());
        }
        buildNumber = jarUtil.getBuild();

        if( getConfig().getBoolean("debug", false) )
            LoggerFactory.getLogUtil().enableDebug("com.andune.liftsign");

        // load localized strings for the configured locale
        LocaleConfig localeConfig = new LocaleConfig(getConfig().getString("locale", "en"),
                getDataFolder(), "liftsign", getFile(), null);

        // build object graph using Guice dependency injection. This injects
        // all dependencies for us using the @Inject annotations
        Injector injector = Guice.createInjector(new LiftSignModule(this, localeConfig));
        injector.injectMembers(this);

        permSystem.setupPermissions(true, getConfig().getStringList("permissions"));

        getServer().getPluginManager().registerEvents(playerListener, this);
        getServer().getPluginManager().registerEvents(blockListener, this);

        log.info("version " + getDescription().getVersion() + ", build " + buildNumber + " is enabled");
    }

    @Override
    public void onDisable() {
        log.info("version " + getDescription().getVersion() + ", build " + buildNumber + " is disabled");
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
    public void setPermissionSystem(PermissionSystemImpl permSystem) {
        this.permSystem = permSystem;
    }
}
