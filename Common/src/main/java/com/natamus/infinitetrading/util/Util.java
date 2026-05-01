package com.natamus.infinitetrading.util;

import com.natamus.infinitetrading.config.ConfigHandler;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;

public class Util {
	public static boolean hasInfiniteTrades(AbstractVillager abstractVillager) {
		if (abstractVillager instanceof Villager) {
			return ConfigHandler.villagerInfiniteTrades;
		}
		else if (abstractVillager instanceof WanderingTrader) {
			return ConfigHandler.wanderingTraderInfiniteTrades;
		}
		return ConfigHandler.fallbackInfiniteTrades;
	}

	public static void syncMerchantOffersToClient(AbstractVillager abstractVillager) {
		Player player = abstractVillager.getTradingPlayer();
		if (player == null) {
			return;
		}

		int level = 1;
		int xp = 0;
		if (abstractVillager instanceof Villager villager) {
			level = villager.getVillagerData().getLevel();
			xp = villager.getVillagerXp();
		}

		player.sendMerchantOffers(
			player.containerMenu.containerId,
			abstractVillager.getOffers(),
			level,
			xp,
			abstractVillager.showProgressBar(),
			abstractVillager.canRestock()
		);
	}
}
