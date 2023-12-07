package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scoreboard.Team;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scoreboard.Scoreboard;

import com.nisovin.magicspells.castmodifiers.Condition;

public class OnSameTeamCondition extends Condition {
	
	@Override
	public boolean initialize(String var) {
		return true;
	}

	@Override
	public boolean check(LivingEntity caster) {
		return false;
	}

	@Override
	public boolean check(LivingEntity caster, LivingEntity target) {
		return checkTeam(caster, target);
	}

	@Override
	public boolean check(LivingEntity caster, Location location) {
		return false;
	}

	private boolean checkTeam(LivingEntity caster, LivingEntity target) {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		Team team1 = scoreboard.getEntryTeam(caster.getName());
		Team team2 = scoreboard.getEntryTeam(target.getName());
		return (team1 != null && team2 != null) && team1.equals(team2);
	}
	
}
