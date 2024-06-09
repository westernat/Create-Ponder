package com.iafenvoy.ponder.extra.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

public interface S2CMessage extends MessageBase {
	void handle(Minecraft client, ClientPacketListener handler, PacketSender responseSender);
}
