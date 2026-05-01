package com.natamus.infinitetrading.mixin;

import com.natamus.collective.functions.EntityFunctions;
import com.natamus.infinitetrading.util.MerchantOfferAccess;
import com.natamus.infinitetrading.util.Util;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractVillager.class, priority = 1001)
public class AbstractVillagerMixin {
	@Inject(method = "getOffers", at = @At("RETURN"))
	private void infinitetrading_stampOwner(CallbackInfoReturnable<MerchantOffers> cir) {
		MerchantOffers offers = cir.getReturnValue();
		if (offers != null) {
			for (MerchantOffer offer : offers) {
				((MerchantOfferAccess) offer).infiniteTrading_setMerchantOfferOwner((AbstractVillager)(Object) this);
			}
		}
	}

	@Inject(method = "notifyTrade", at = @At("TAIL"))
	private void infinitetrading_resetAfterTrade(MerchantOffer offer, CallbackInfo ci) {
		AbstractVillager abstractVillager = (AbstractVillager)(Object)this;
		if (!Util.hasInfiniteTrades(abstractVillager)) {
			return;
		}

		EntityFunctions.resetMerchantOfferUses(offer);
		EntityFunctions.resetMerchantOfferDemand(offer);

		Util.syncMerchantOffersToClient(abstractVillager);
	}
}