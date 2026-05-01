package com.natamus.infinitetrading.util;

import net.minecraft.world.entity.npc.villager.AbstractVillager;

public interface MerchantOfferAccess {
	AbstractVillager infiniteTrading_getMerchantOfferOwner();
	void infiniteTrading_setMerchantOfferOwner(AbstractVillager abstractVillager);
}