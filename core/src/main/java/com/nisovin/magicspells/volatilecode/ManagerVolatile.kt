package com.nisovin.magicspells.volatilecode

import org.bukkit.Bukkit

import com.nisovin.magicspells.MagicSpells
import com.nisovin.magicspells.volatilecode.latest.VolatileCodeLatest

object ManagerVolatile {

    private val helper = object: VolatileCodeHelper {
        override fun error(message: String) {
            MagicSpells.error(message)
        }

        override fun scheduleDelayedTask(task: Runnable?, delay: Long): Int {
            return MagicSpells.scheduleDelayedTask(task, delay)
        }
    }

    fun constructVolatileCodeHandler(): VolatileCodeHandle {
        var handle: VolatileCodeHandle
        try {
            val mcVersion = Bukkit.getMinecraftVersion()
            val version = "v" + mcVersion.replace(".", "_")
            val volatileCode = Class.forName("com.nisovin.magicspells.volatilecode.$version.VolatileCode_$version")

            handle = volatileCode.getConstructor(VolatileCodeHelper::class.java).newInstance(helper) as VolatileCodeHandle
            MagicSpells.log("Found volatile code handler for $mcVersion.")
        } catch (_: Throwable) {
            try {
                handle = VolatileCodeLatest(helper)
                MagicSpells.log("Using latest volatile code handler.")
            } catch (_: Throwable) {
                handle = VolatileCodeDisabled()
                MagicSpells.error("Volatile code handler could not be initialized.")
            }
        }

        return handle
    }
}
