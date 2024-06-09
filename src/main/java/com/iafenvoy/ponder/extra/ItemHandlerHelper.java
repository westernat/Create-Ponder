package com.iafenvoy.ponder.extra;
//Pick from io.github.fabricators_of_create.porting_lib.transfer

import net.minecraft.world.item.ItemStack;

public class ItemHandlerHelper {
	public static boolean canItemStacksStack(ItemStack first, ItemStack second) {
		return ItemStack.isSameItemSameTags(first, second);
	}
}

