package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import com.nisovin.magicspells.util.Util;

import net.kyori.adventure.text.Component;

import com.nisovin.magicspells.castmodifiers.Condition;

public class DisplayNameCondition extends Condition {

    private Component displayName;

    @Override
    public boolean initialize(String var) {
        if (var == null || var.isEmpty()) return false;
        displayName = Util.getMiniMessage(var);
        return true;
    }

    @Override
    public boolean check(LivingEntity livingEntity) {
        return check(livingEntity, livingEntity);
    }

    @Override
    public boolean check(LivingEntity livingEntity, LivingEntity target) {
        if (!(target instanceof Player player)) return false;
        return player.displayName().equals(displayName);
    }

    @Override
    public boolean check(LivingEntity livingEntity, Location location) {
        return false;
    }

}
