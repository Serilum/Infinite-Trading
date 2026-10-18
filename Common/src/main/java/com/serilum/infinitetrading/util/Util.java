package com.serilum.infinitetrading.util;

import com.serilum.infinitetrading.config.ConfigHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class Util {
	public static boolean hasInfiniteTrades(Merchant merchant) {
		if (merchant instanceof Villager villager) {
			if (villager.getVillagerData().getLevel() < ConfigHandler.minimumVillagerLevel) {
				return false;
			}
			return ConfigHandler.villagerInfiniteTrades;
		}
		else if (merchant instanceof WanderingTrader) {
			return ConfigHandler.wanderingTraderInfiniteTrades;
		}
		return ConfigHandler.moddedTraderInfiniteTrades;
	}

	public static boolean hasTradeLimit() {
		return ConfigHandler.tradeUsesMultiplier > 0;
	}

	public static int getTradeLimit(MerchantOffer offer) {
		return Math.max(1, (int)Math.ceil(offer.getMaxUses() * ConfigHandler.tradeUsesMultiplier));
	}

	public static boolean hasReachedTradeLimit(MerchantOffer offer) {
		if (!hasTradeLimit()) {
			return false;
		}
		return offer.getUses() >= getTradeLimit(offer);
	}

	public static boolean isOutOfStock(Merchant merchant, MerchantOffer offer) {
		if (hasReachedTradeLimit(offer)) {
			return true;
		}

		Player tradingPlayer = merchant.getTradingPlayer();
		if (tradingPlayer instanceof ServerPlayer serverPlayer && TradeLoops.isLimitedForPlayer(serverPlayer, offer)) {
			return TradeLoops.getLimitedUses(serverPlayer, offer) >= offer.getMaxUses();
		}
		return false;
	}

	public static int getUsesAsSeenByPlayer(ServerPlayer serverPlayer, MerchantOffer offer) {
		if (hasReachedTradeLimit(offer)) {
			return offer.getMaxUses();
		}

		if (TradeLoops.isLimitedForPlayer(serverPlayer, offer)) {
			return Math.min(TradeLoops.getLimitedUses(serverPlayer, offer), offer.getMaxUses());
		}

		if (!hasTradeLimit()) {
			return 0;
		}

		int scaledUses = (int)Math.floor((double)offer.getUses() * offer.getMaxUses() / getTradeLimit(offer));
		return Math.min(scaledUses, offer.getMaxUses() - 1);
	}

	public static MerchantOffers getOffersAsSeenByPlayer(ServerPlayer serverPlayer, MerchantOffers offers) {
		MerchantOffers offersAsSeenByPlayer = new MerchantOffers();
		for (MerchantOffer offer : offers) {
			MerchantOffer offerCopy = offer.copy();

			Merchant owner = ((MerchantOfferAccess) offer).infiniteTrading_getMerchantOfferOwner();
			if (owner != null && hasInfiniteTrades(owner)) {
				((MerchantOfferAccess) offerCopy).infiniteTrading_setUses(getUsesAsSeenByPlayer(serverPlayer, offer));
			}

			offersAsSeenByPlayer.add(offerCopy);
		}
		return offersAsSeenByPlayer;
	}

	public static void syncMerchantOffersToClient(ServerPlayer serverPlayer, Merchant merchant) {
		int level = ((ServerPlayerAccess) serverPlayer).infiniteTrading_getLastMerchantLevel();
		if (merchant instanceof Villager villager) {
			level = villager.getVillagerData().getLevel();
		}

		serverPlayer.sendMerchantOffers(
			serverPlayer.containerMenu.containerId,
			merchant.getOffers(),
			level,
			merchant.getVillagerXp(),
			merchant.showProgressBar(),
			merchant.canRestock()
		);
	}
}