package com.natamus.infinitetrading.config;

import com.natamus.collective.config.DuskConfig;
import com.natamus.infinitetrading.util.Reference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConfigHandler extends DuskConfig {
	public static HashMap<String, List<String>> configMetaData = new HashMap<String, List<String>>();

	@Entry public static boolean wanderingTraderInfiniteTrades = true;
	@Entry public static boolean villagerInfiniteTrades = true;

	public static void initConfig() {
		configMetaData.put("wanderingTraderInfiniteTrades", Arrays.asList(
			"When enabled, the trades of the wandering trader will never lock up."
		));
		configMetaData.put("villagerInfiniteTrades", Arrays.asList(
			"When enabled, the trades of villagers will never lock up."
		));

		DuskConfig.init(Reference.NAME, Reference.MOD_ID, ConfigHandler.class);
	}
}