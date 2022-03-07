package com.nisovin.magicspells.volatilecode.v1_18_R2

import org.bukkit.Bukkit
import org.bukkit.entity.*
import org.bukkit.Location
import org.bukkit.util.Vector
import org.bukkit.inventory.ItemStack
import org.bukkit.event.entity.ExplosionPrimeEvent

import org.bukkit.craftbukkit.v1_18_R2.entity.*
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld
import org.bukkit.craftbukkit.v1_18_R2.CraftServer
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack

import net.minecraft.world.phys.Vec3
import net.minecraft.world.entity.EntityType
import net.minecraft.network.protocol.game.*
import net.minecraft.network.chat.TextComponent
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.item.alchemy.PotionUtils
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.world.entity.boss.enderdragon.EnderDragon

import com.nisovin.magicspells.MagicSpells
import com.nisovin.magicspells.util.BoundingBox
import com.nisovin.magicspells.util.compat.EventUtil
import com.nisovin.magicspells.volatilecode.VolatileCodeHandle

private typealias nmsItemStack = net.minecraft.world.item.ItemStack

class VolatileCode1_18_R2: VolatileCodeHandle {

    private var entityLivingPotionEffectColor: EntityDataAccessor<Int>? = null

    init {
        try {
            // CHANGE THIS TO SPIGOT MAPPING VERSION OF MOJANG'S - EntityDataAccessor<Integer> DATA_EFFECT_COLOR_ID
            val entityLivingPotionEffectColorField = net.minecraft.world.entity.LivingEntity::class.java.getDeclaredField("bL")
            entityLivingPotionEffectColorField.isAccessible = true
            entityLivingPotionEffectColor = entityLivingPotionEffectColorField.get(null) as EntityDataAccessor<Int>
        } catch (e: Exception) {
            MagicSpells.error("THIS OCCURRED WHEN CREATING THE VOLATILE CODE HANDLE FOR 1.18, THE FOLLOWING ERROR IS MOST LIKELY USEFUL IF YOU'RE RUNNING THE LATEST VERSION OF MAGICSPELLS.")
            e.printStackTrace()
        }
    }

    override fun addPotionGraphicalEffect(entity: LivingEntity, color: Int, duration: Long) {
        val livingEntity = (entity as CraftLivingEntity).handle
        val entityData = livingEntity.entityData
        entityData.set(entityLivingPotionEffectColor, color)

        if (duration > 0) {
            MagicSpells.scheduleDelayedTask({
                var c = 0
                if (livingEntity.getActiveEffects().isNotEmpty()) {
                    c = PotionUtils.getColor(livingEntity.getActiveEffects())
                }
                entityData.set(entityLivingPotionEffectColor, c)
            }, duration)
        }
    }

    override fun sendFakeSlotUpdate(player: Player, slot: Int, item: ItemStack?) {
        val nmsItem: nmsItemStack?
        if (item != null) nmsItem = CraftItemStack.asNMSCopy(item)
        else nmsItem = null

        val packet = ClientboundContainerSetSlotPacket(0, 0, slot.toShort() + 36, nmsItem!!)
        (player as CraftPlayer).handle.connection.send(packet)
    }

    override fun simulateTnt(target: Location, source: LivingEntity, explosionSize: Float, fire: Boolean): Boolean {
        val e = PrimedTnt((target.world as CraftWorld).handle, target.x, target.y, target.z, (source as CraftLivingEntity).handle)
        val c = CraftTNTPrimed(Bukkit.getServer() as CraftServer, e)
        val event = ExplosionPrimeEvent(c, explosionSize, fire)
        EventUtil.call(event)
        return event.isCancelled
    }

    override fun setFallingBlockHurtEntities(block: FallingBlock, damage: Float, max: Int) {
        val efb = (block as CraftFallingBlock).handle
        block.setHurtEntities(true)
        efb.setHurtsEntities(damage, max)
    }

    override fun playDragonDeathEffect(location: Location) {
        val dragon = EnderDragon(EntityType.ENDER_DRAGON, (location.world as CraftWorld).handle)
        dragon.setPos(location.x, location.y, location.z)

        val addMobPacket = ClientboundAddEntityPacket(dragon)
        val entityEventPacket = ClientboundEntityEventPacket(dragon, 3)
        val removeEntityPacket = ClientboundRemoveEntitiesPacket(dragon.id)

        val box = BoundingBox(location, 64.0)
        val players = ArrayList<Player>()
        for (player in location.world!!.players) {
            if (!box.contains(player)) continue
            players.add(player)
            (player as CraftPlayer).handle.connection.send(addMobPacket)
            player.handle.connection.send(addMobPacket)
            player.handle.connection.send(entityEventPacket)
        }

        MagicSpells.scheduleDelayedTask({
            for (player in players) {
                if (!player.isValid) continue
                (player as CraftPlayer).handle.connection.send(removeEntityPacket)
            }
        }, 250)

    }

    override fun setClientVelocity(player: Player, velocity: Vector) {
        val packet = ClientboundSetEntityMotionPacket(player.entityId, Vec3(velocity.x, velocity.y, velocity.z))
        (player as CraftPlayer).handle.connection.send(packet)
    }

    override fun setInventoryTitle(player: Player, title: String) {
        val entityPlayer = (player as CraftPlayer).handle
        val container = entityPlayer.containerMenu
        val packet = ClientboundOpenScreenPacket(container.containerId, container.type, TextComponent(title))

        player.handle.connection.send(packet)
        player.updateInventory()
    }

    override fun startAutoSpinAttack(player: Player?, ticks: Int) {
        val entityPlayer = (player as CraftPlayer).handle
        entityPlayer.startAutoSpinAttack(ticks)
    }

}
