package com.nisovin.magicspells.volatilecode

import java.util.*
import java.util.stream.Collectors

import org.bukkit.*
import org.bukkit.entity.*
import org.bukkit.util.Vector
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

import com.nisovin.magicspells.MagicSpells

import com.destroystokyo.paper.profile.PlayerProfile
import com.destroystokyo.paper.profile.ProfileProperty

class VolatileCodePaper(private val parent: VolatileCodeHandle): VolatileCodeHandle {

    override fun creaturePathToLoc(creature: Creature, loc: Location, speed: Float) {
        creature.pathfinder.moveTo(loc, speed.toDouble())
    }

    override fun getNBTString(item: ItemStack, key: String): String? {
        return parent.getNBTString(item, key)
    }

    override fun simulateTnt(target: Location, source: LivingEntity, explosionSize: Float, fire: Boolean): Boolean {
        return parent.simulateTnt(target, source, explosionSize, fire)
    }

    override fun createExplosionByEntity(entity: Entity, location: Location, size: Float, fire: Boolean, breakBlocks: Boolean): Boolean {
        return parent.createExplosionByEntity(entity, location, size, fire, breakBlocks)
    }

    override fun setTarget(entity: LivingEntity?, target: LivingEntity?) {
        parent.setTarget(entity, target)
    }

    override fun addPotionGraphicalEffect(entity: LivingEntity, color: Int, duration: Int) {
        parent.addPotionGraphicalEffect(entity, color, duration)
    }

    override fun setTexture(meta: SkullMeta, texture: String, signature: String?) {
        val profile = meta.playerProfile!!
        setTexture(profile, texture, signature)
        meta.playerProfile = profile
    }

    override fun setTexture(meta: SkullMeta, texture: String, signature: String, uuid: String?, offlinePlayer: OfflinePlayer) {
        try {
            val profile = Bukkit.createProfile(if (uuid != null) UUID.fromString(uuid) else null, offlinePlayer.name)
            setTexture(profile, texture, signature)
            meta.playerProfile = profile
        } catch (e: SecurityException) {
            MagicSpells.handleException(e)
        } catch (e: IllegalArgumentException) {
            MagicSpells.handleException(e)
        } catch (e: IllegalAccessException) {
            MagicSpells.handleException(e)
        }
    }

    private fun setTexture(profile: PlayerProfile, texture: String, signature: String?): PlayerProfile {
        if (signature == null || signature.isEmpty()) {
            profile.setProperty(ProfileProperty("textures",  texture))
        } else {
            profile.setProperty(ProfileProperty("textures",  texture, signature))
        }
        return profile
    }

    override fun addAILookAtPlayer(entity: LivingEntity, range: Int) {
        parent.addAILookAtPlayer(entity, range)
    }

    override fun setExperienceBar(player: Player, level: Int, percent: Float) {
        parent.setExperienceBar(player, level, percent)
    }

    override fun getSkinData(player: Player): String {
        val skins = player.playerProfile.properties.stream().filter { prop: ProfileProperty -> prop.name == "textures" }.collect(Collectors.toList())
        val latestSkin = skins.first()
        return "Skin: " + latestSkin.value + "\nSignature: " + latestSkin.signature
    }

    override fun setClientVelocity(player: Player, velocity: Vector) {
        parent.setClientVelocity(player, velocity)
    }

    override fun playDragonDeathEffect(location: Location) {
        parent.playDragonDeathEffect(location)
    }

    override fun setSkin(player: Player, skin: String, signature: String) {
        setTexture(player.playerProfile, skin, signature)
    }

    override fun colorize(message: String?): String {
        return parent.colorize(message)
    }

    override fun sendFakeSlotUpdate(player: Player, slot: Int, item: ItemStack) {
        parent.sendFakeSlotUpdate(player, slot, item)
    }

    override fun setKiller(entity: LivingEntity, killer: Player) {
        entity.killer = killer
    }

    override fun setNBTString(item: ItemStack, key: String, value: String): ItemStack {
        return parent.setNBTString(item, key, value)
    }

    override fun setFallingBlockHurtEntities(block: FallingBlock, damage: Float, max: Int) {
        parent.setFallingBlockHurtEntities(block, damage, max)
    }

    override fun setInventoryTitle(player: Player, title: String) {
        parent.setInventoryTitle(player, title)
    }

    override fun createSmithingRecipe(namespaceKey: NamespacedKey?, result: ItemStack?, base: Material?, addition: Material?): Recipe {
        return parent.createSmithingRecipe(namespaceKey, result, base, addition)
    }

    override fun sendActionBarMessage(player: Player, message: String?) {
        player.sendActionBar(message!!)
    }

}
