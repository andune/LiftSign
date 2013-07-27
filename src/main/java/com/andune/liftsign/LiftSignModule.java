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

import java.io.IOException;

import org.bukkit.plugin.Plugin;

import com.andune.minecraft.commonlib.BukkitLoggerImpl;
import com.andune.minecraft.commonlib.Logger;
import com.andune.minecraft.commonlib.Teleport;
import com.andune.minecraft.commonlib.i18n.Colors;
import com.andune.minecraft.commonlib.i18n.Locale;
import com.andune.minecraft.commonlib.i18n.LocaleConfig;
import com.andune.minecraft.commonlib.i18n.LocaleFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.reflections.Reflections;

import javax.inject.Singleton;

/** This module tells Guice how to wire together all dependencies
 * for the plugin.
 * 
 * @author andune
 *
 */
public class LiftSignModule extends AbstractModule {
	private final Plugin plugin;
	private final LocaleConfig localeConfig;
	private Locale locale;
    private Reflections reflections;

	public LiftSignModule(Plugin plugin, LocaleConfig localeConfig) {
		this.plugin = plugin;
		this.localeConfig = localeConfig;
	}
	
	@Override
	protected void configure() {
		bind(Logger.class)
			.toInstance(new BukkitLoggerImpl(plugin));
//		bind(java.util.logging.Logger.class)
//			.toInstance(plugin.getLogger());
		bind(SignCache.class)
			.in(Scopes.SINGLETON);
		bind(Teleport.class)
			.in(Scopes.SINGLETON);
		bind(Colors.class)
			.in(Scopes.SINGLETON);
		
		
		install(new FactoryModuleBuilder()
			.implement(SignDetail.class, SignDetail.class)
			.build(SignFactory.class)
		);
	}
	
//	@Provides
//	@Singleton
//	protected PermissionSystem providePermissionSystem() {
//		if( permSystem == null )
//			permSystem = new PermissionSystem(plugin, plugin.getLogger());
//		return permSystem;
//	}
	
	@Provides
	protected Plugin providePlugin() {
		return plugin;
	}
	
	@Provides
	protected Locale provideLocale() throws IOException {
        if( locale == null ) {
			locale = LocaleFactory.getLocale();
			locale.load(localeConfig);
        }
		
		return locale;
	}

    @Provides
    @Singleton
    protected Reflections provideReflections() {
        if (reflections == null) {
            this.reflections = new Reflections("com.andune.liftsign");
        }
        return reflections;
    }
}
