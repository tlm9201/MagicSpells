package com.nisovin.magicspells.variables.variabletypes;

import org.bukkit.Location;

import com.nisovin.magicspells.util.Name;

@Name("squareddistancetolocation")
public class SquaredDistanceToLocationVariable extends DistanceToLocationVariable {

	public SquaredDistanceToLocationVariable() {
		super();
	}
	
	@Override
	protected double calculateReportedDistance(double multiplier, Location origin, Location target) {
		return target.distanceSquared(origin) * multiplier;
	}
	
}
