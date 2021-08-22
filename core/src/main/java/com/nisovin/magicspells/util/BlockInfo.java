package com.nisovin.magicspells.util;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public class BlockInfo {

	private Material material;

	private BlockData blockData;

	private String blockDataString;

	public BlockInfo(Material material, BlockData data, String blockDataString) {
		this.material = material;
		this.blockData = data;
		this.blockDataString = blockDataString;
	} 

	public BlockInfo() {

	}

	public String getBlockDataString() {
		return blockDataString;
	}

	public BlockData getBlockData() {
		return blockData;
	}

	public Material getMaterial() {
		return material;
	}

	public void setBlockDataString(String blockDataString) {
		this.blockDataString = blockDataString;
	}

	public void setBlockData(BlockData blockData) {
		this.blockData = blockData;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public boolean blockDataMatches(BlockData blockData) {
		return this.blockData != null && this.blockData.matches(blockData);
	}

}
