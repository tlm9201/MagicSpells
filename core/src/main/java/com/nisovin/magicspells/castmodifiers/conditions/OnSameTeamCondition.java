package com.nisovin.magicspells.castmodifiers.conditions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scoreboard.ScoreboardManager;

import com.nisovin.magicspells.castmodifiers.Condition;

public class OnSameTeamCondition extends Condition {
	
	@Override
	public boolean initialize(String var) {
		return true;
	}

	@Override
	public boolean check(LivingEntity livingEntity) {
		return false;
	}

	@Override
	public boolean check(LivingEntity livingEntity, LivingEntity target) {
		return checkTeam(livingEntity, target);
	}

	@Override
	public boolean check(LivingEntity livingEntity, Location location) {
		return false;
	}

	private boolean checkTeam(LivingEntity livingEntity, LivingEntity target) {
		if (target instanceof Player c && livingEntity instanceof Player t) {
			ScoreboardManager manager = Bukkit.getScoreboardManager();
			Team team1 = manager.getMainScoreboard().getEntryTeam(c.getName());
			Team team2 = manager.getMainScoreboard().getEntryTeam(t.getName());
			return (team1 != null && team2 != null) && team1.equals(team2);
		}
		return false;
	}
	
}
