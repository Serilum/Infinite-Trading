package com.serilum.infinitetrading.mixin;

import com.natamus.collective.functions.EntityFunctions;
import com.serilum.infinitetrading.config.ConfigHandler;
import com.serilum.infinitetrading.util.MerchantOfferAccess;
import com.serilum.infinitetrading.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MerchantOffer.class, priority = 1001)
public class MerchantOfferMixin implements MerchantOfferAccess {
	@Shadow private int uses;
	@Shadow @Final private int maxUses;
	@Shadow private int demand;

	@Unique
	private Merchant infiniteTrading_MerchantOfferOwner;

	@Override
	public Merchant infiniteTrading_getMerchantOfferOwner() {
		return this.infiniteTrading_MerchantOfferOwner;
	}

	@Override
	public void infiniteTrading_setMerchantOfferOwner(Merchant owner) {
		this.infiniteTrading_MerchantOfferOwner = owner;
	}

	@Override
	public void infiniteTrading_setUses(int uses) {
		this.uses = uses;
	}

	@Unique
	private boolean infiniteTrading_hasInfiniteOwner() {
		if (this.infiniteTrading_MerchantOfferOwner == null) {
			return false;
		}

		return Util.hasInfiniteTrades(this.infiniteTrading_MerchantOfferOwner);
	}

	@Inject(method = "satisfiedBy", at = @At("HEAD"))
	private void infinitetrading_resetBeforeCheck(ItemStack buyA, ItemStack buyB, CallbackInfoReturnable<Boolean> cir) {
		if (!ConfigHandler.removeTradeDemand) {
			return;
		}

		if (!this.infiniteTrading_hasInfiniteOwner()) {
			return;
		}

		EntityFunctions.resetMerchantOfferDemand((MerchantOffer)(Object)this);
	}

	@Inject(method = "updateDemand", at = @At("HEAD"), cancellable = true)
	private void infinitetrading_updateDemandWithVanillaUses(CallbackInfo ci) {
		if (!this.infiniteTrading_hasInfiniteOwner()) {
			return;
		}

		int vanillaUses = Math.min(this.uses, this.maxUses);
		this.demand = this.demand + vanillaUses - (this.maxUses - vanillaUses);
		ci.cancel();
	}

	@Inject(method = "isOutOfStock", at = @At("HEAD"), cancellable = true)
	private void infinitetrading_isOutOfStock(CallbackInfoReturnable<Boolean> cir) {
		if (!this.infiniteTrading_hasInfiniteOwner()) {
			return;
		}

		cir.setReturnValue(Util.isOutOfStock(this.infiniteTrading_MerchantOfferOwner, (MerchantOffer)(Object)this));
	}
}