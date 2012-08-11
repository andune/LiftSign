/**
 * 
 */
package org.morganm.liftsign.util;

import java.util.logging.Level;

import javax.inject.Inject;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;

/**
 * @author morganm
 *
 */
public class LoggerImpl implements Logger {
	// class version: 2

	private final Plugin plugin;
	private final java.util.logging.Logger log;
	private final Debug debug;
	private String logPrefix;
	
	@Inject
	public LoggerImpl(Plugin plugin) {
		this.plugin = plugin;
		this.log = this.plugin.getLogger();

		// PluginLogger handles prefix automatically. Otherwise set a prefix
		if( !(plugin.getLogger() instanceof PluginLogger) ) {
			String prefix = plugin.getDescription().getPrefix();
			if( prefix == null )
				prefix = "["+plugin.getDescription().getName()+"] ";
			setLogPrefix(prefix);
		}

		this.debug = Debug.getInstance();
	}
	
	public void setLogPrefix(String logPrefix) {
		if( !logPrefix.endsWith(" ") )
			logPrefix = logPrefix + " ";
		this.logPrefix = logPrefix;
	}

	private String concatStrings(StringBuilder sb, Object...msgs) {
		for(Object o : msgs) {
			sb.append(o);
		}
		return sb.toString();
	}
	
	@Override
	public void info(Object... msg) {
		if( log.isLoggable(Level.INFO) ) {
			log.info(concatStrings(new StringBuilder(logPrefix), msg));
		}
	}

	@Override
	public void warn(Object... msg) {
		if( log.isLoggable(Level.WARNING) ) {
			log.warning(concatStrings(new StringBuilder(logPrefix), msg));
		}
	}
	@Override
	public void warn(Throwable t, Object... msg) {
		if( log.isLoggable(Level.WARNING) ) {
			log.log(Level.WARNING, concatStrings(new StringBuilder(logPrefix), msg), t);
		}
	}
	

	@Override
	public void severe(Object... msg) {
		if( log.isLoggable(Level.SEVERE) ) {
			log.severe(concatStrings(new StringBuilder(logPrefix), msg));
		}
	}
	@Override
	public void severe(Throwable t, Object... msg) {
		if( log.isLoggable(Level.SEVERE) ) {
			log.log(Level.SEVERE, concatStrings(new StringBuilder(logPrefix), msg), t);
		}
	}

	@Override
	public void debug(Object... msg) {
		debug.debug(msg);
	}

	@Override
	public void devDebug(Object... msg) {
		debug.devDebug(msg);
	}

}
