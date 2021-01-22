package net.tolmikarc.trader.conversation

import net.tolmikarc.trader.menu.TradeMenu
import org.bukkit.conversations.ConversationAbandonedEvent
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.entity.Player
import org.mineacademy.fo.conversation.SimpleConversation
import org.mineacademy.fo.conversation.SimpleDecimalPrompt
import org.mineacademy.fo.model.HookManager

class MoneyConversation(val menu: TradeMenu) : SimpleConversation() {

    var player: Player? = null

    override fun getFirstPrompt(): Prompt {
        return MoneyPrompt()
    }


    inner class MoneyPrompt : SimpleDecimalPrompt() {
        override fun getPrompt(ctx: ConversationContext?): String {
            player = getPlayer(ctx)
            return "How much would you like to offer?"
        }

        override fun acceptValidatedInput(context: ConversationContext?, input: Double): Prompt? {
            player = getPlayer(context)
            if (HookManager.getBalance(player) - input < 0) {
                tellBoxed(player, "Cannot input more money than you have")
                return END_OF_CONVERSATION
            }

            if (menu.firstPlayer == player)
                menu.firstPlayerMoney = input
            else
                menu.secondPlayerMoney = input
            return END_OF_CONVERSATION
        }

        override fun onConversationEnd(conversation: SimpleConversation?, event: ConversationAbandonedEvent?) {
            menu.displayTo(player)
            menu.restartMenu()
        }

    }

}