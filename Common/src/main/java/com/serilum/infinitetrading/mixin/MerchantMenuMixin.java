package com.serilum.infinitetrading.mixin;

import com.serilum.infinitetrading.util.MerchantOfferAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MerchantMenu.class, priority = 1001)
public class MerchantMenuMixin {
	@Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/trading/Merchant;)V", at = @At("TAIL"))
	private void infinitetrading_stampOwner(int containerId, Inventory inventory, Merchant merchant, CallbackInfo ci) {
		if (merchant.isClientSide()) {
			return;
		}

		for (MerchantOffer offer : merchant.getOffers()) {
			((MerchantOfferAccess) offer).infiniteTrading_setMerchantOfferOwner(merchant);
		}
	}
}