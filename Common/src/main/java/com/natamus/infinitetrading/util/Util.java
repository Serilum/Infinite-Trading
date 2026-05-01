package com.natamus.infinitetrading.util;

import com.natamus.infinitetrading.config.ConfigHandler;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
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
			level = villager.getVillagerData().level();
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
