/**
 * 
 */
package org.morganm.liftsign.util;


/** 
 * 
 * @author morganm
 *
 */
public interface Logger {
	public abstract void info(Object...msg);
	public abstract void warn(Object...msg);
	public abstract void warn(Throwable t, Object... msg);
	public abstract void severe(Object...msg);
	public abstract void severe(Throwable t, Object... msg);
	
	public abstract void debug(Object...msg);
	public abstract void devDebug(Object...msg);
}
