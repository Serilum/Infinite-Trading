package com.serilum.infinitetrading.mixin;

import com.natamus.collective.functions.EntityFunctions;
import com.serilum.infinitetrading.config.ConfigHandler;
import com.serilum.infinitetrading.util.MerchantOfferAccess;
import com.serilum.infinitetrading.util.Util;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractVillager.class, priority = 1001)
public class AbstractVillagerMixin {
	@Inject(method = "getOffers", at = @At("RETURN"))
	private void infinitetrading_stampOwner(CallbackInfoReturnable<MerchantOffers> cir) {
		MerchantOffers offers = cir.getReturnValue();
		if (offers == null) {
			return;
		}

		AbstractVillager abstractVillager = (AbstractVillager)(Object)this;
		boolean resetDemand = ConfigHandler.removeTradeDemand && Util.hasInfiniteTrades(abstractVillager);
		for (MerchantOffer offer : offers) {
			((MerchantOfferAccess) offer).infiniteTrading_setMerchantOfferOwner(abstractVillager);

			if (resetDemand) {
				EntityFunctions.resetMerchantOfferDemand(offer);
			}
		}
	}
}