package com.iafenvoy.ponder.extra.network;

import com.simibubi.create.infrastructure.command.SConfigureConfigPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.function.Supplier;

public class PonderClientNetworkHandler implements ClientPlayNetworking.PlayChannelHandler {
	public static final ResourceLocation CHANNEL_NAME = new ResourceLocation("create_ponder", "client_handler");
	public static final PonderClientNetworkHandler INSTANCE = new PonderClientNetworkHandler();
	private final HashMap<ResourceLocation, Supplier<S2CMessage>> types = new HashMap<>();

	public static void send(C2SMessage message) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeUtf(message.getId().toString());
		message.encode(buf);
		ClientPlayNetworking.send(PonderServerNetworkHandler.CHANNEL_NAME, buf);
	}

	public static void register() {
		ClientPlayNetworking.registerGlobalReceiver(CHANNEL_NAME, INSTANCE);
		INSTANCE.registerMessage(SConfigureConfigPacket::new);
	}

	@Override
	public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
		ResourceLocation id = new ResourceLocation(buf.readUtf());
		if (!this.types.containsKey(id)) return;
		S2CMessage message = this.types.get(id).get();
		message.decode(buf);
		message.handle(client, handler, responseSender);
	}

	public void registerMessage(Supplier<S2CMessage> messageSupplier) {
		S2CMessage message = messageSupplier.get();
		this.types.put(message.getId(), messageSupplier);
	}
}
