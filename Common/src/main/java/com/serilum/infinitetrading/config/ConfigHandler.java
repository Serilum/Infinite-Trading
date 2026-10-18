package com.serilum.infinitetrading.config;

import com.natamus.collective.config.DuskConfig;
import com.serilum.infinitetrading.util.Reference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConfigHandler extends DuskConfig {
	public static HashMap<String, List<String>> configMetaData = new HashMap<String, List<String>>();

	@Entry public static boolean wanderingTraderInfiniteTrades = true;
	@Entry public static boolean villagerInfiniteTrades = true;
	@Entry public static boolean moddedTraderInfiniteTrades = true;
	@Entry(min = 1, max = 5) public static int minimumVillagerLevel = 1;
	@Entry(min = 0, max = 1000.0) public static double tradeUsesMultiplier = 0.0;
	@Entry public static boolean removeTradeDemand = true;
	@Entry public static boolean preventTradeLoops = false;

	public static void initConfig() {
		configMetaData.put("wanderingTraderInfiniteTrades", Arrays.asList(
			"When enabled, the trades of the wandering trader will never lock up."
		));
		configMetaData.put("villagerInfiniteTrades", Arrays.asList(
			"When enabled, the trades of villagers will never lock up."
		));
		configMetaData.put("moddedTraderInfiniteTrades", Arrays.asList(
			"When enabled, the trades of traders from other mods will never lock up. For example custom villagers, or the goblins from Goblin Traders."
		));
		configMetaData.put("minimumVillagerLevel", Arrays.asList(
			"The level a villager needs before its trades become infinite. 1 is Novice, 5 is Master. Villagers below it keep their vanilla trade limits."
		));
		configMetaData.put("tradeUsesMultiplier", Arrays.asList(
			"When above 0, trades are no longer infinite, but can be used this many times the vanilla amount before they lock up. They restock like vanilla. For example 10 allows ten times as many trades. 0 keeps trades infinite."
		));
		configMetaData.put("removeTradeDemand", Arrays.asList(
			"When enabled, prices don't go up when a trade is used a lot. Disable to keep vanilla's demand prices, which update when a villager restocks."
		));
		configMetaData.put("preventTradeLoops", Arrays.asList(
			"When enabled, players can't endlessly earn emeralds with trade loops, like buying bookshelves and selling the books back. Per player, once selling an item earns more emeralds than that player spent on buying it or items crafted from or into it, their trades selling that item fall back to vanilla limits. This is counted per 20 minutes of play (the length of a Minecraft day), sleeping or changing the time does not reset it."
		));

		DuskConfig.init(Reference.NAME, Reference.MOD_ID, ConfigHandler.class);
	}
}