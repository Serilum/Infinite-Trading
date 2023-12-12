package com.natamus.infinitetrading.forge.events;

import com.natamus.infinitetrading.events.VillagerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ForgeVillagerEvent {
	@SubscribeEvent
	public void onVillagerClick(PlayerInteractEvent.EntityInteract e) {
		VillagerEvent.onVillagerClick(e.getPlayer(), e.getWorld(), e.getHand(), e.getTarget(), null);
	}
}
