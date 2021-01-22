package net.tolmikarc.trader.util

import org.bukkit.entity.Player

object PlayerUtil {

    fun invFull(p: Player): Boolean {
        return p.inventory.firstEmpty() == -1
    }
}