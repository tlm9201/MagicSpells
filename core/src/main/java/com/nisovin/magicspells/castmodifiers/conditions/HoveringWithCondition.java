package com.nisovin.magicspells.castmodifiers.conditions;

import com.nisovin.magicspells.util.Util;
import com.nisovin.magicspells.castmodifiers.Condition;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class HoveringWithCondition extends Condition {

    private ItemStack item;

    @Override
    public boolean setVar(String var) {
        if(var == null || var.isEmpty()) return false;
        ItemStack itemStack = Util.getItemStackFromString(var);
        if(itemStack == null) return false;
        item = itemStack;
        return true;
    }

    @Override
    public boolean check(LivingEntity livingEntity) {
        if(!(livingEntity instanceof Player)) return false;
        Player player = (Player) livingEntity;
        ItemStack itemStack = player.getOpenInventory().getCursor();
        return itemStack != null && itemStack.isSimilar(item);
    }

    @Override
    public boolean check(LivingEntity livingEntity, LivingEntity target) {
        return check(target);
    }

    @Override
    public boolean check(LivingEntity livingEntity, Location location) {
        return false;
    }
}
