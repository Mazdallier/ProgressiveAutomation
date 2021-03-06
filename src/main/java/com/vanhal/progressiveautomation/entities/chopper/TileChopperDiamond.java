package com.vanhal.progressiveautomation.entities.chopper;

import com.vanhal.progressiveautomation.ref.ToolHelper;
import com.vanhal.progressiveautomation.upgrades.UpgradeType;

public class TileChopperDiamond extends TileChopper {

	public TileChopperDiamond() {
		super();
		setAllowedUpgrades(UpgradeType.DIAMOND, UpgradeType.WITHER);
		setUpgradeLevel(10);
	}
}