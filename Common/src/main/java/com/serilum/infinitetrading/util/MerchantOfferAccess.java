package com.serilum.infinitetrading.util;

import net.minecraft.world.item.trading.Merchant;

public interface MerchantOfferAccess {
	Merchant infiniteTrading_getMerchantOfferOwner();
	void infiniteTrading_setMerchantOfferOwner(Merchant merchant);
	void infiniteTrading_setUses(int uses);
}