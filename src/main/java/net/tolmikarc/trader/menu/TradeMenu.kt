package net.tolmikarc.trader.menu

import net.tolmikarc.trader.PlayerCache
import net.tolmikarc.trader.conversation.MoneyConversation
import net.tolmikarc.trader.settings.Localization
import net.tolmikarc.trader.settings.Settings
import net.tolmikarc.trader.util.PlayerUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.mineacademy.fo.Common
import org.mineacademy.fo.Messenger
import org.mineacademy.fo.menu.Menu
import org.mineacademy.fo.menu.button.Button
import org.mineacademy.fo.menu.model.ItemCreator
import org.mineacademy.fo.menu.model.MenuClickLocation
import org.mineacademy.fo.model.HookManager
import org.mineacademy.fo.remain.CompMaterial
import java.text.NumberFormat

class TradeMenu(val firstPlayer: Player, val secondPlayer: Player) : Menu() {

    private val itemSlotMap = mutableMapOf<Int, ItemStack>()


    private val firstPlayerCache = PlayerCache.getCache(firstPlayer)
    private val secondPlayerCache = PlayerCache.getCache(secondPlayer)

    private val firstPlayerTrade
        get() = firstPlayerCache.itemsInTrade
    private val secondPlayerTrade
        get() = secondPlayerCache.itemsInTrade

    var firstPlayerMoney = 0.0
    var secondPlayerMoney = 0.0

    private var firstPlayerConfirmed = false
    private var secondPlayerConfirmed = false

    private val firstPlayerButton = PlayerButton(firstPlayer)
    private val secondPlayerButton = PlayerButton(secondPlayer)

    private val firstPlayerConfirmButton = ConfirmButton(firstPlayer)
    private val secondPlayerConfirmButton = ConfirmButton(secondPlayer)
    private val divider =
        Button.DummyButton.makeDummy(ItemCreator.of(CompMaterial.RED_STAINED_GLASS_PANE, "").glow(true).hideTags(true))

    private val firstPlayerMoneyButton = MoneyButton(firstPlayer)
    private val secondPlayerMoneyButton = MoneyButton(secondPlayer)


    private val cancelButton: Button = object : Button() {
        override fun onClickedInMenu(player: Player?, p1: Menu?, p2: ClickType?) {
            cancelTransaction()

        }

        override fun getItem(): ItemStack {
            return ItemCreator.of(CompMaterial.REDSTONE_BLOCK, Localization.Menu.CANCEL).build().make()
        }
    }

    private fun isConfirmed(player: Player): Boolean {
        return if (player == firstPlayer)
            firstPlayerConfirmed
        else
            secondPlayerConfirmed
    }


    private fun tradeItems() {
        val firstIterator = secondPlayerTrade.iterator()
        val secondIterator = firstPlayerTrade.iterator()
        while (firstIterator.hasNext()) {
            val item = firstIterator.next()
            if (PlayerUtil.invFull(firstPlayer)) {
                Messenger.error(
                    firstPlayer,
                    Localization.INVENTORY_SPACE
                )
                break
            }
            firstPlayer.inventory.addItem(item)
            firstIterator.remove()
        }
        while (secondIterator.hasNext()) {
            val item = secondIterator.next()
            if (PlayerUtil.invFull(secondPlayer)) {
                Messenger.error(
                    secondPlayer,
                    Localization.INVENTORY_SPACE
                )
                break
            }
            secondPlayer.inventory.addItem(item)
            secondIterator.remove()
        }

        HookManager.withdraw(firstPlayer, firstPlayerMoney)
        HookManager.deposit(secondPlayer, firstPlayerMoney)
        HookManager.withdraw(secondPlayer, secondPlayerMoney)
        HookManager.deposit(firstPlayer, secondPlayerMoney)
        firstPlayer.closeInventory()
        secondPlayer.closeInventory()
        firstPlayerCache.isTrading = false
        secondPlayerCache.isTrading = false
        firstPlayerCache.saveItems()
        secondPlayerCache.saveItems()
        Messenger.success(firstPlayer, Localization.Menu.SUCCESS)
        Messenger.success(secondPlayer, Localization.Menu.SUCCESS)
    }


    private fun addAndSave(item: ItemStack, player: Player) {
        val playerCache = PlayerCache.getCache(player)
        playerCache.addItem(item)
    }

    private fun removeAndSave(item: ItemStack, player: Player) {
        val playerCache = PlayerCache.getCache(player)
        playerCache.removeItem(item)
    }

    private fun Double.toMoneyFormat(): String {
        val doubleFormat = NumberFormat.getCurrencyInstance()
        return doubleFormat.format(this)
    }

    private fun cancelTransaction() {
        val firstIterator = firstPlayerTrade.iterator()
        val secondIterator = secondPlayerTrade.iterator()
        while (firstIterator.hasNext()) {
            val item = firstIterator.next()
            if (PlayerUtil.invFull(firstPlayer)) {
                Messenger.error(
                    firstPlayer,
                    Localization.INVENTORY_SPACE
                )
                break
            }
            firstPlayer.inventory.addItem(item)
            firstIterator.remove()
        }
        while (secondIterator.hasNext()) {
            val item = secondIterator.next()
            if (PlayerUtil.invFull(secondPlayer)) {
                Messenger.error(
                    secondPlayer,
                    Localization.INVENTORY_SPACE
                )
                break
            }
            secondPlayer.inventory.addItem(item)
            secondIterator.remove()
        }
        firstPlayer.closeInventory()
        secondPlayer.closeInventory()
        firstPlayerCache.tradeInvite = null
        secondPlayerCache.tradeInvite = null
        firstPlayerCache.isTrading = false
        secondPlayerCache.isTrading = false
        firstPlayerCache.saveItems()
        secondPlayerCache.saveItems()
        Messenger.info(firstPlayer, Localization.Menu.CANCELLED)
        Messenger.info(secondPlayer, Localization.Menu.CANCELLED)
    }

    override fun isActionAllowed(
        location: MenuClickLocation?,
        slot: Int,
        clicked: ItemStack?,
        cursor: ItemStack?
    ): Boolean {
        return location == MenuClickLocation.PLAYER_INVENTORY
    }


    override fun onMenuClick(
        player: Player,
        slot: Int,
        action: InventoryAction?,
        click: ClickType?,
        cursor: ItemStack?,
        clicked: ItemStack?,
        cancelled: Boolean
    ) {
        if (getButton(clicked) != null) return
        if (clicked != null && cursor != null) {
            if (action == InventoryAction.SWAP_WITH_CURSOR) {
                if (isConfirmed(player)) return
                if (IntRange(0, 4).any { (slot - it) % 9 == 0 } && player == firstPlayer) {
                    if (clicked.type == Material.GRAY_STAINED_GLASS_PANE) {
                        itemSlotMap[slot] = cursor
                        addAndSave(cursor, player)
                        player.setItemOnCursor(null)
                    } else {
                        itemSlotMap[slot] = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
                        removeAndSave(clicked, player)
                        player.inventory.addItem(clicked)
                        clicked.type = Material.GRAY_STAINED_GLASS_PANE
                    }

                }
                if (IntRange(4, 9).any { (slot - it) % 9 == 0 } && player == secondPlayer) {
                    if (clicked.type == Material.GRAY_STAINED_GLASS_PANE) {
                        itemSlotMap[slot] = cursor
                        addAndSave(cursor, player)
                        player.setItemOnCursor(null)
                    } else {
                        itemSlotMap[slot] = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
                        removeAndSave(clicked, player)
                        player.inventory.addItem(clicked)
                        clicked.type = Material.GRAY_STAINED_GLASS_PANE
                    }
                }
            } else {
                if (isConfirmed(player)) return
                if (IntRange(0, 4).any { (slot - it) % 9 == 0 } && player == firstPlayer) {
                    if (clicked.type == Material.GRAY_STAINED_GLASS_PANE) return
                    itemSlotMap[slot] = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
                    removeAndSave(clicked, player)
                    player.setItemOnCursor(clicked)
                }
                if (IntRange(4, 9).any { (slot - it) % 9 == 0 } && player == secondPlayer) {
                    if (clicked.type == Material.GRAY_STAINED_GLASS_PANE) return
                    itemSlotMap[slot] = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
                    removeAndSave(clicked, player)
                    player.setItemOnCursor(clicked)
                }
            }
            restartMenu()
        }
    }

    override fun onMenuClose(player: Player, inventory: Inventory?) {
        Common.runLater(20) {
            if (!player.isConversing && PlayerCache.getCache(player).isTrading)
                cancelTransaction()
        }
    }

    override fun getItemAt(slot: Int): ItemStack? {
        return when (slot) {
            4 -> cancelButton.item
            9 * 5 + 2 -> when {
                Settings.ECONOMY_ENABLED -> firstPlayerMoneyButton.item
                itemSlotMap.containsKey(slot) -> itemSlotMap[slot]
                else -> ItemStack(
                    Material.GRAY_STAINED_GLASS_PANE
                )
            }
            9 * 6 - 3 -> when {
                Settings.ECONOMY_ENABLED -> secondPlayerMoneyButton.item
                itemSlotMap.containsKey(
                    slot
                ) -> itemSlotMap[slot]
                else -> ItemStack(Material.GRAY_STAINED_GLASS_PANE)
            }
            9 * 5 + 1 -> firstPlayerConfirmButton.item
            9 * 6 - 2 -> secondPlayerConfirmButton.item
            9 * 5 -> firstPlayerButton.item
            9 * 6 - 1 -> secondPlayerButton.item
            else -> {
                when {
                    (slot - 4) % 9 == 0 -> divider.item
                    itemSlotMap.containsKey(slot) -> itemSlotMap[slot]
                    else -> ItemStack(Material.GRAY_STAINED_GLASS_PANE)
                }
            }
        }
    }

    override fun addInfoButton(): Boolean {
        return false
    }

    override fun addReturnButton(): Boolean {
        return false
    }

    init {
        title = Localization.Menu.TITLE
        size = 9 * 6
    }


    inner class PlayerButton(owningPlayer: Player) : Button() {
        private val playerName = if (owningPlayer == firstPlayer) firstPlayer.name else secondPlayer.name
        override fun onClickedInMenu(player: Player?, p1: Menu?, p2: ClickType?) {

        }

        override fun getItem(): ItemStack {
            return ItemCreator.of(CompMaterial.PLAYER_HEAD).build().makeSkull(playerName)
        }
    }

    inner class ConfirmButton(private val owningPlayer: Player) : Button() {
        override fun onClickedInMenu(player: Player, p1: Menu, p2: ClickType) {

            if (player == firstPlayer)
                firstPlayerConfirmed = !firstPlayerConfirmed
            else
                secondPlayerConfirmed = !secondPlayerConfirmed

            restartMenu()
            if (firstPlayerConfirmed && secondPlayerConfirmed)
                tradeItems()
        }

        override fun getItem(): ItemStack {
            return if (owningPlayer == firstPlayer) {
                if (firstPlayerConfirmed) {
                    ItemCreator.of(
                        CompMaterial.GLOWSTONE,
                        Localization.Menu.CONFIRM_BUTTON_TITLE,
                        Localization.Menu.CONFIRM_BUTTON_LORE
                    ).build().make()
                } else
                    ItemCreator.of(CompMaterial.REDSTONE_LAMP, Localization.Menu.NOT_CONFIRMED).build().make()
            } else if (secondPlayerConfirmed) {
                ItemCreator.of(
                    CompMaterial.GLOWSTONE,
                    Localization.Menu.CONFIRM_BUTTON_TITLE,
                    Localization.Menu.CONFIRM_BUTTON_LORE
                ).build().make()
            } else
                ItemCreator.of(CompMaterial.REDSTONE_LAMP, Localization.Menu.NOT_CONFIRMED).build().make()

        }

    }

    inner class MoneyButton(private val owningPlayer: Player) : Button() {


        override fun onClickedInMenu(player: Player, p1: Menu, p2: ClickType) {
            player.closeInventory()
            MoneyConversation(this@TradeMenu).start(player)
        }

        override fun getItem(): ItemStack {
            val firstLore =
                Localization.Menu.MONEY_BUTTON_LORE.map { it.replace("{amount}", firstPlayerMoney.toMoneyFormat()) }
            val secondLore =
                Localization.Menu.MONEY_BUTTON_LORE.map { it.replace("{amount}", secondPlayerMoney.toMoneyFormat()) }
            return if (owningPlayer == firstPlayer) {
                ItemCreator.of(
                    CompMaterial.EMERALD,
                    Localization.Menu.MONEY_BUTTON_TITLE,
                    firstLore
                ).build().make()
            } else {
                ItemCreator.of(
                    CompMaterial.EMERALD,
                    Localization.Menu.MONEY_BUTTON_TITLE,
                    secondLore
                ).build().make()
            }

        }

    }


}