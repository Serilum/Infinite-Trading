package com.natamus.infinitetrading.forge.events;

import com.natamus.infinitetrading.events.VillagerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

import java.lang.invoke.MethodHandles;

public class ForgeVillagerEvent {
	public static void registerEventsInBus() {
		// BusGroup.DEFAULT.register(MethodHandles.lookup(), ForgeVillagerEvent.class);

		PlayerInteractEvent.EntityInteract.BUS.addListener(ForgeVillagerEvent::onVillagerClick);
	}

	@SubscribeEvent
	public static void onVillagerClick(PlayerInteractEvent.EntityInteract e) {
		VillagerEvent.onVillagerClick(e.getEntity(), e.getLevel(), e.getHand(), e.getTarget(), null);
	}
}
