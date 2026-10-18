package com.serilum.infinitetrading.mixin;

import com.serilum.infinitetrading.util.ServerPlayerAccess;
import com.serilum.infinitetrading.util.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlayer.class, priority = 1001)
public class ServerPlayerMixin implements ServerPlayerAccess {
	@Unique
	private int infiniteTrading_lastMerchantLevel = 1;

	@Override
	public int infiniteTrading_getLastMerchantLevel() {
		return this.infiniteTrading_lastMerchantLevel;
	}

	@Inject(method = "sendMerchantOffers", at = @At("HEAD"))
	private void infinitetrading_rememberMerchantLevel(int containerId, MerchantOffers offers, int merchantLevel, int merchantXp, boolean showProgressBar, boolean canRestock, CallbackInfo ci) {
		this.infiniteTrading_lastMerchantLevel = merchantLevel;
	}

	@ModifyVariable(method = "sendMerchantOffers", at = @At("HEAD"), argsOnly = true)
	private MerchantOffers infinitetrading_sendOffersAsSeenByPlayer(MerchantOffers offers) {
		return Util.getOffersAsSeenByPlayer((ServerPlayer)(Object)this, offers);
	}
}