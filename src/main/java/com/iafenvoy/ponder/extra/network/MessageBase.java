package com.iafenvoy.ponder.extra.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface MessageBase {
	ResourceLocation getId();

	/**
	 * Use when send message
	 */
	void encode(FriendlyByteBuf buf);


	/**
	 * Use when receive message
	 */
	void decode(FriendlyByteBuf buf);
}
