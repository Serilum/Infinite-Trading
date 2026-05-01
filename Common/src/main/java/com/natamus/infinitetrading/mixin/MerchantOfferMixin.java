package com.natamus.infinitetrading.mixin;

import com.natamus.collective.functions.EntityFunctions;
import com.natamus.infinitetrading.util.MerchantOfferAccess;
import com.natamus.infinitetrading.util.Util;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MerchantOffer.class, priority = 1001)
public class MerchantOfferMixin implements MerchantOfferAccess {
	@Unique
	private AbstractVillager infiniteTrading_MerchantOfferOwner;

	@Override
	public AbstractVillager infiniteTrading_getMerchantOfferOwner() {
		return this.infiniteTrading_MerchantOfferOwner;
	}

	@Override
	public void infiniteTrading_setMerchantOfferOwner(AbstractVillager owner) {
		this.infiniteTrading_MerchantOfferOwner = owner;
	}

	@Inject(method = "satisfiedBy", at = @At("HEAD"))
	private void infinitetrading_resetBeforeCheck(ItemStack buyA, ItemStack buyB, CallbackInfoReturnable<Boolean> cir) {
		if (!Util.hasInfiniteTrades(this.infiniteTrading_MerchantOfferOwner)) {
			return;
		}

		EntityFunctions.resetMerchantOfferDemand((MerchantOffer)(Object)this);
	}
}