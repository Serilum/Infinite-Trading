package com.serilum.infinitetrading.util;

import com.serilum.infinitetrading.config.ConfigHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.item.trading.MerchantOffer;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class TradeLoops {
	private static WeakReference<RecipeManager> relatedItemsSource = new WeakReference<>(null);
	private static final HashMap<Item, HashSet<Item>> relatedItems = new HashMap<>();

	private static WeakReference<MinecraftServer> ledgerServer = new WeakReference<>(null);
	private static long ledgerDay = -1;
	private static final HashMap<UUID, HashMap<Item, Integer>> emeraldsSpent = new HashMap<>();
	private static final HashMap<UUID, HashMap<Item, Integer>> emeraldsEarned = new HashMap<>();
	private static final HashMap<UUID, HashMap<Item, Integer>> limitedSales = new HashMap<>();

	public static void recordTrade(ServerPlayer serverPlayer, MerchantOffer offer) {
		if (!ConfigHandler.preventTradeLoops) {
			return;
		}

		ServerLevel serverLevel = serverPlayer.level();
		startNewDayIfNeeded(serverLevel);

		UUID playerUUID = serverPlayer.getUUID();
		ItemStack result = offer.getResult();

		int emeraldsPaid = getEmeraldCount(offer.getCostA()) + getEmeraldCount(offer.getCostB());
		if (emeraldsPaid > 0 && !result.is(Items.EMERALD)) {
			HashMap<Item, Integer> playerSpent = getPlayerMap(emeraldsSpent, playerUUID);
			for (Item relatedItem : getRelatedItems(serverLevel, result.getItem())) {
				playerSpent.merge(relatedItem, emeraldsPaid, Integer::sum);
			}
			return;
		}

		Item soldItem = getSoldItem(offer);
		if (soldItem == null) {
			return;
		}

		if (isLimitedForPlayer(serverPlayer, offer)) {
			getPlayerMap(limitedSales, playerUUID).merge(soldItem, 1, Integer::sum);
		}

		getPlayerMap(emeraldsEarned, playerUUID).merge(soldItem, result.getCount(), Integer::sum);
	}

	public static boolean isLimitedForPlayer(ServerPlayer serverPlayer, MerchantOffer offer) {
		if (!ConfigHandler.preventTradeLoops) {
			return false;
		}

		Item soldItem = getSoldItem(offer);
		if (soldItem == null) {
			return false;
		}

		startNewDayIfNeeded(serverPlayer.level());

		UUID playerUUID = serverPlayer.getUUID();
		int spent = getPlayerMap(emeraldsSpent, playerUUID).getOrDefault(soldItem, 0);
		if (spent <= 0) {
			return false;
		}

		return getPlayerMap(emeraldsEarned, playerUUID).getOrDefault(soldItem, 0) > spent;
	}

	public static int getLimitedUses(ServerPlayer serverPlayer, MerchantOffer offer) {
		Item soldItem = getSoldItem(offer);
		if (soldItem == null) {
			return 0;
		}
		return getPlayerMap(limitedSales, serverPlayer.getUUID()).getOrDefault(soldItem, 0);
	}

	public static HashSet<Item> getRelatedItems(ServerLevel serverLevel, Item item) {
		RecipeManager recipeManager = serverLevel.recipeAccess();
		if (recipeManager != relatedItemsSource.get()) {
			buildRelatedItems(serverLevel, recipeManager);
		}

		HashSet<Item> itemAndRelatedItems = new HashSet<>();
		itemAndRelatedItems.add(item);
		if (relatedItems.containsKey(item)) {
			itemAndRelatedItems.addAll(relatedItems.get(item));
		}
		return itemAndRelatedItems;
	}

	private static void buildRelatedItems(ServerLevel serverLevel, RecipeManager recipeManager) {
		relatedItems.clear();

		ContextMap displayContext = SlotDisplayContext.fromLevel(serverLevel);
		for (RecipeHolder<?> recipeHolder : recipeManager.getRecipes()) {
			Recipe<?> recipe = recipeHolder.value();
			if (!(recipe instanceof CraftingRecipe) || recipe.isSpecial()) {
				continue;
			}

			try {
				linkRecipeItems(recipe, displayContext);
			}
			catch (Exception ignored) { }
		}

		relatedItemsSource = new WeakReference<>(recipeManager);
	}

	private static void linkRecipeItems(Recipe<?> recipe, ContextMap displayContext) {
		for (RecipeDisplay recipeDisplay : recipe.display()) {
			Item resultItem = recipeDisplay.result().resolveForFirstStack(displayContext).getItem();
			if (resultItem == Items.AIR) {
				continue;
			}

			for (Ingredient ingredient : recipe.placementInfo().ingredients()) {
				for (ItemStack ingredientStack : ingredient.display().resolveForStacks(displayContext)) {
					linkItems(resultItem, ingredientStack.getItem());
					linkItems(ingredientStack.getItem(), resultItem);
				}
			}
		}
	}

	private static void linkItems(Item item, Item relatedItem) {
		if (item == relatedItem) {
			return;
		}

		if (!relatedItems.containsKey(item)) {
			relatedItems.put(item, new HashSet<>());
		}
		relatedItems.get(item).add(relatedItem);
	}

	private static void startNewDayIfNeeded(ServerLevel serverLevel) {
		MinecraftServer server = serverLevel.getServer();
		long day = serverLevel.getGameTime() / 24000L;
		if (day == ledgerDay && server == ledgerServer.get()) {
			return;
		}

		ledgerServer = new WeakReference<>(server);
		ledgerDay = day;
		emeraldsSpent.clear();
		emeraldsEarned.clear();
		limitedSales.clear();
	}

	private static Item getSoldItem(MerchantOffer offer) {
		if (!offer.getResult().is(Items.EMERALD)) {
			return null;
		}

		ItemStack costA = offer.getCostA();
		if (costA.isEmpty() || costA.is(Items.EMERALD)) {
			return null;
		}
		return costA.getItem();
	}

	private static int getEmeraldCount(ItemStack itemStack) {
		if (!itemStack.is(Items.EMERALD)) {
			return 0;
		}
		return itemStack.getCount();
	}

	private static <K> HashMap<K, Integer> getPlayerMap(HashMap<UUID, HashMap<K, Integer>> map, UUID playerUUID) {
		if (!map.containsKey(playerUUID)) {
			map.put(playerUUID, new HashMap<>());
		}
		return map.get(playerUUID);
	}
}