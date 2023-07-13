/*
 * Copyright (c) 2021-2021 Tolmikarc All Rights Reserved
 */

package net.tolmikarc.trader.task

import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class CooldownTask : BukkitRunnable() {
    override fun run() {
        val currentTime = System.currentTimeMillis()
        if (cooldowns.isNotEmpty()) {
            for (entry in cooldowns.keys) {
                val time = cooldowns[entry]!!
                if (time < currentTime) {
                    cooldowns.remove(entry)
                }
            }
        }
    }


    companion object {

        private fun Int.toMilis(): Long {
            return (this * 1000).toLong()
        }

        private val cooldowns: MutableMap<AbstractMap.SimpleEntry<Player, CooldownType>, Long> =
            ConcurrentHashMap()


        fun addCooldownTimer(entity: Player, type: CooldownType) {
            val cooldown = AbstractMap.SimpleEntry(entity, type)
            cooldowns[cooldown] = System.currentTimeMillis() + type.seconds.toMilis()
        }


        fun hasCooldown(entity: Player, type: CooldownType): Boolean {
            val cooldown = AbstractMap.SimpleEntry(entity, type)
            return cooldowns.containsKey(cooldown)
        }
    }

    enum class CooldownType(val seconds: Int) {
        TRADE(1)
    }

}