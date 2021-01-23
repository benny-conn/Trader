package net.tolmikarc.trader

import net.tolmikarc.trader.command.TradeCommand
import net.tolmikarc.trader.listener.PlayerListener
import net.tolmikarc.trader.settings.Localization
import net.tolmikarc.trader.settings.Settings
import net.tolmikarc.trader.task.CooldownTask
import org.mineacademy.fo.Common
import org.mineacademy.fo.plugin.SimplePlugin
import org.mineacademy.fo.settings.YamlStaticConfig

class TraderPlugin : SimplePlugin() {
    override fun onPluginStart() {
        Common.log("Trader Plugin registering commands, events, and tasks")
        registerEvents(PlayerListener())
        registerCommand(TradeCommand())
        Common.runTimerAsync(20, CooldownTask())
        Common.ADD_TELL_PREFIX = true
        Common.log("Registered Commands, Events, and Tasks successfully.")
    }


    override fun getSettings(): List<Class<out YamlStaticConfig?>> {
        return listOf(Settings::class.java, Localization::class.java)
    }

    override fun getFoundedYear(): Int {
        return 2021
    }


}