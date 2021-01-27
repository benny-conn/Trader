package net.tolmikarc.trader.command

import net.tolmikarc.trader.PlayerCache
import net.tolmikarc.trader.menu.TradeMenu
import net.tolmikarc.trader.settings.Localization
import net.tolmikarc.trader.util.PlayerUtil
import org.mineacademy.fo.Messenger
import org.mineacademy.fo.command.SimpleCommand

class TradeCommand : SimpleCommand("trade") {
    override fun onCommand() {

        val playerCache = PlayerCache.getCache(player)
        when (args[0].toLowerCase()) {
            "accept" -> {
                checkNotNull(playerCache.tradeInvite, Localization.NO_PENDING_INVITE)
                playerCache.tradeInvite?.let {
                    val tradeMenu = TradeMenu(it, player)
                    tradeMenu.displayTo(player)
                    tradeMenu.displayTo(it)
                }
            }
            "decline" -> {
                Messenger.info(player, Localization.DECLINE)
                if (playerCache.tradeInvite != null)
                    Messenger.info(
                        playerCache.tradeInvite,
                        Localization.DECLINE_NOTIFICATION.replace("{player}", player.displayName)
                    )
                playerCache.tradeInvite = null
            }
            "return" -> {
                val iterator = playerCache.itemsInTrade.iterator()
                while (iterator.hasNext()) {
                    val item = iterator.next()
                    if (PlayerUtil.invFull(player)) {
                        tellError(
                            Localization.INVENTORY_SPACE
                        )
                        break
                    }
                    player.inventory.addItem(item)
                    iterator.remove()
                }
                tellSuccess("Completed return!")
            }
            else -> {
                val otherPlayer = findPlayer(args[0])
                val otherPlayerCache = PlayerCache.getCache(otherPlayer)
                otherPlayerCache.tradeInvite = player
                tellSuccess(Localization.TRADE_SENT.replace("{player}", otherPlayer.displayName))
                Messenger.info(
                    otherPlayer,
                    Localization.INVITE_NOTIFICATION.replace("{player}", player.displayName)
                )
            }
        }
    }


    override fun tabComplete(): List<String> {
        return when (args.size) {
            1 -> listOf("accept", "decline", "return")
            else -> super.tabComplete()
        }
    }

    init {
        minArguments = 1
        usage = "<player | accept | decline>"
        description = Localization.COMMAND_DESCRIPTION
        permission = null
    }
}