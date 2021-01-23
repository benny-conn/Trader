package net.tolmikarc.trader.listener

import net.tolmikarc.trader.PlayerCache
import net.tolmikarc.trader.menu.TradeMenu
import net.tolmikarc.trader.settings.Localization
import net.tolmikarc.trader.settings.Settings
import net.tolmikarc.trader.task.CooldownTask
import net.tolmikarc.trader.task.CooldownTask.Companion.addCooldownTimer
import net.tolmikarc.trader.task.CooldownTask.Companion.hasCooldown
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.mineacademy.fo.Messenger
import org.mineacademy.fo.event.MenuOpenEvent

class PlayerListener : Listener {

    @EventHandler
    fun onPlayerShiftClickPlayer(e: PlayerInteractEntityEvent) {
        if (!Settings.SHIFT_CLICK_TRADE) return
        val player = e.player
        if (hasCooldown(player, CooldownTask.CooldownType.TRADE)) return
        if (!player.isSneaking) return
        if (e.rightClicked !is Player) return
        val otherPlayer = e.rightClicked as Player
        val otherPlayerCache = PlayerCache.getCache(otherPlayer)
        otherPlayerCache.tradeInvite = player
        Messenger.info(
            otherPlayer,
            Localization.INVITE_NOTIFICATION.replace("{player}", player.displayName)
        )
        Messenger.success(player, Localization.TRADE_SENT.replace("{player}", otherPlayer.displayName))
        addCooldownTimer(player, CooldownTask.CooldownType.TRADE)
    }

    @EventHandler
    fun onMenuOpen(e: MenuOpenEvent) {
        val menu = e.menu
        if (menu !is TradeMenu) return
        val firstCache = PlayerCache.getCache(menu.firstPlayer)
        val secondCache = PlayerCache.getCache(menu.secondPlayer)
        firstCache.tradeInvite = null
        secondCache.tradeInvite = null
        firstCache.isTrading = true
        secondCache.isTrading = true
    }

    @EventHandler
    fun onPickUpItem(e: EntityPickupItemEvent) {
        val entity = e.entity
        if (entity !is Player) return
        val playerCache = PlayerCache.getCache(entity)
        if (playerCache.isTrading) e.isCancelled = true
    }


}