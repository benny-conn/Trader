package net.tolmikarc.trader.command

import net.tolmikarc.trader.PlayerCache
import net.tolmikarc.trader.menu.TradeMenu
import net.tolmikarc.trader.util.PlayerUtil
import org.mineacademy.fo.Messenger
import org.mineacademy.fo.command.SimpleCommand

class TradeCommand : SimpleCommand("trade") {
    override fun onCommand() {

        val playerCache = PlayerCache.getCache(player)
        when (args[0].toLowerCase()) {
            "accept" -> {
                checkNotNull(playerCache.tradeInvite, "You do not have a pending trade invite")
                playerCache.tradeInvite?.let {
                    val tradeMenu = TradeMenu(it, player)
                    tradeMenu.displayTo(player)
                    tradeMenu.displayTo(it)
                }
            }
            "decline" -> {
                Messenger.info(player, "Declined any pending trade invites")
                if (playerCache.tradeInvite != null)
                    Messenger.info(playerCache.tradeInvite, "${player.name} declined your request to trade.")
                playerCache.tradeInvite = null
            }
            "return" -> {
                val iterator = playerCache.itemsInTrade.iterator()
                while (iterator.hasNext()) {
                    val item = iterator.next()
                    if (PlayerUtil.invFull(player)) {
                        tellError(
                            "You do not have enough inventory slots to complete this transaction. Please clear inventory slots and use /trade return to get your items back."
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
                tellSuccess("Successfully sent trade invite to ${otherPlayer.name}")
                Messenger.info(
                    otherPlayer,
                    "You have received an invite to trade from ${player.name}. Type /trade accept to accept or /trade decline to decline"
                )
            }
        }
    }

    init {
        minArguments = 1
        usage = "<player | accept | decline>"
    }
}