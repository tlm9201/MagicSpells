package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.castmodifiers.Condition;

public class HasScoreboardTagCondition extends Condition {

    private String tag;

    @Override
    public boolean initialize(String var) {
        if (var == null || var.isEmpty()) return false;
        tag = var;
        return true;
    }

    @Override
    public boolean check(LivingEntity caster) {
        return checkTags(caster);
    }

    @Override
    public boolean check(LivingEntity caster, LivingEntity target) {
        return checkTags(target);
    }

    @Override
    public boolean check(LivingEntity livingEntity, Location location) {
        return false;
    }

    private boolean checkTags(LivingEntity entity) {
        String localTag = tag;
        if (entity instanceof Player && localTag.contains("%")) localTag = MagicSpells.doVariableReplacements((Player) entity, localTag);
        return entity.getScoreboardTags().contains(localTag);
    }

}
