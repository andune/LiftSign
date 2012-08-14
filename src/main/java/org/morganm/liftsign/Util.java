/*******************************************************************************
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
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
 * Contributors:
 *     Mark Morgan - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package org.morganm.liftsign;

import javax.inject.Inject;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.morganm.mBukkitLib.i18n.MessageUtil;

/** Common utility methods for LiftSign classes.
 * 
 * @author morganm
 *
 */
public class Util {
	private MessageUtil messageUtil;
	
	@Inject
	public Util(MessageUtil messageUtil) {
		this.messageUtil = messageUtil;
	}
	
	/** If a given block is a sign, this will return the Sign state object.
	 * If it's not a sign, null will be returned.
	 * 
	 * @param b
	 * @return
	 */
	public Sign getSignState(final Block b) {
		Sign sign = null;
		
		final int typeId = b.getTypeId();
		if( typeId == Material.SIGN_POST.getId() || typeId == Material.WALL_SIGN.getId() ) {
			BlockState bs = b.getState();
			sign = (Sign) bs;
		}
		
		return sign;
	}
	
	public MessageUtil getMessageUtil() {
		return messageUtil;
	}
	
	public static final String MSG_NO_PERMISSION = "no_permission";
	public static final String MSG_DESTINATION_NOT_SAFE = "destination_not_safe";
	public static final String MSG_NO_VALID_LIFT_TARGET = "no_valid_lift_target";
	public static final String MSG_NO_PERM_CREATE_LIFT_SIGN = "no_permission_create_lift_sign";
	public static final String MSG_UP_ONE_FLOOR = "up_one_floor";
	public static final String MSG_DOWN_ONE_FLOOR = "down_one_floor";
	
}
