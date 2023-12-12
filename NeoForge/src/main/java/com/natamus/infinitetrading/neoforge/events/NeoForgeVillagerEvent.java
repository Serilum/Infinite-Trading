package com.natamus.infinitetrading.neoforge.events;

import com.natamus.infinitetrading.events.VillagerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class NeoForgeVillagerEvent {
	@SubscribeEvent
	public static void onVillagerClick(PlayerInteractEvent.EntityInteract e) {
		VillagerEvent.onVillagerClick(e.getEntity(), e.getLevel(), e.getHand(), e.getTarget(), null);
	}
}
