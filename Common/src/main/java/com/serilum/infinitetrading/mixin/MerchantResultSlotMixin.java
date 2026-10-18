package com.serilum.infinitetrading.mixin;

import com.serilum.infinitetrading.util.TradeLoops;
import com.serilum.infinitetrading.util.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MerchantResultSlot.class, priority = 1001)
public class MerchantResultSlotMixin {
	@Shadow @Final private MerchantContainer slots;
	@Shadow @Final private Merchant merchant;

	@Inject(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/trading/Merchant;notifyTrade(Lnet/minecraft/world/item/trading/MerchantOffer;)V", shift = At.Shift.AFTER))
	private void infinitetrading_afterTrade(Player player, ItemStack carried, CallbackInfo ci) {
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return;
		}

		MerchantOffer offer = this.slots.getActiveOffer();
		if (offer == null) {
			return;
		}

		if (!Util.hasInfiniteTrades(this.merchant)) {
			return;
		}

		TradeLoops.recordTrade(serverPlayer, offer);

		Util.syncMerchantOffersToClient(serverPlayer, this.merchant);
	}
}