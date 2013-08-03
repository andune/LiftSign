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
package com.andune.liftsign.listener;

import com.andune.liftsign.*;
import com.andune.minecraft.commonlib.Logger;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

import javax.inject.Inject;

/**
 * @author andune
 */
public class BlockListener implements Listener {
    private final SignCache cache;
    private final PermissionCheck perm;
    private final SignFactory factory;
    private final Logger log;
    private final Util util;

    @Inject
    public BlockListener(SignCache cache, PermissionCheck perm, SignFactory factory, Logger log, Util util) {
        this.cache = cache;
        this.perm = perm;
        this.factory = factory;
        this.log = log;
        this.util = util;
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignChange(SignChangeEvent e) {
        Sign sign = util.getSignState(e.getBlock());
        if (sign != null) {
            log.debug("Sign change detected");
            String[] lines = e.getLines();

            SignDetail signDetail = factory.create(sign, lines);
            if (signDetail.isLiftSign()) {
                log.debug("Sign is lift sign");
                if (!perm.canCreateNormalLift(e.getPlayer())) {
                    util.getMessageUtil().sendLocalizedMessage(e.getPlayer(), Util.MSG_NO_PERM_CREATE_LIFT_SIGN);
                    e.setCancelled(true);
                }
            }

            if (!e.isCancelled())
                cache.newSignCreated(signDetail);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Sign sign = util.getSignState(e.getBlock());
        if (sign != null)
            cache.existingSignDestroyed(sign);
    }
}
