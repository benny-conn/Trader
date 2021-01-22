package net.tolmikarc.trader

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.mineacademy.fo.settings.YamlSectionConfig
import java.util.*

class PlayerCache(uuid: UUID) : YamlSectionConfig(uuid.toString()) {


    var isTrading: Boolean = false

    var itemsInTrade = mutableListOf<ItemStack>()
        private set

    var tradeInvite: Player? = null


    init {
        loadConfiguration(null, "data.db")
    }

    override fun onLoadFinish() {
        if (isSet("Saved-Items"))
            itemsInTrade = getList("Saved-Items", ItemStack::class.java)
    }

    fun addItem(item: ItemStack) {
        itemsInTrade.add(item)
        save("Saved-Items", itemsInTrade)
    }

    fun removeItem(item: ItemStack) {
        itemsInTrade.remove(item)
        save("Saved-Items", itemsInTrade)
    }

    fun saveItems() {
        save("Saved-Items", itemsInTrade)
    }

    companion object {

        val cacheMap = mutableMapOf<UUID, PlayerCache>()

        fun getCache(player: Player): PlayerCache {
            var cache: PlayerCache? =
                cacheMap[player.uniqueId]
            if (cache == null) {
                cache = PlayerCache(player.uniqueId)
                cacheMap[player.uniqueId] = cache
            }
            return cache
        }


    }


}