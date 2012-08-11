/**
 * 
 */
package org.morganm.liftsign;

import javax.inject.Inject;

import org.bukkit.plugin.Plugin;
import org.morganm.liftsign.util.Logger;
import org.morganm.liftsign.util.LoggerImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/** This guice module wires together all dependencies for the plugin.
 * 
 * @author morganm
 *
 */
public class LiftSignModule extends AbstractModule {
	private final Plugin plugin;

	@Inject
	public LiftSignModule(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	protected void configure() {
		bind(Logger.class)
			.to(LoggerImpl.class)
			.in(Scopes.SINGLETON);
		bind(SignCache.class)
			.in(Scopes.SINGLETON);
		
		install(new FactoryModuleBuilder()
			.implement(SignDetail.class, SignDetail.class)
			.build(SignFactory.class)
		);
		
//		com.google.inject.
//		FactoryModelBuilder b;
//		install(new Factor)
	}
	
	@Provides
	Plugin providePlugin() {
		return plugin;
	}

//	@Provides
//	SignCache provideSignCache() {
//		if( signCache == null )
//			injector.
//			signCache = new SignCache();
//		return signCache;
//	}
}
