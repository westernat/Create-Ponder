package com.iafenvoy.ponder.extra.network;

import java.util.HashMap;
import java.util.function.Supplier;

import com.simibubi.create.infrastructure.command.SConfigureConfigPacket;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class PonderServerNetworkHandler implements ServerPlayNetworking.PlayChannelHandler {
	public static final ResourceLocation CHANNEL_NAME = new ResourceLocation("create_ponder", "server_handler");
	public static final PonderServerNetworkHandler INSTANCE = new PonderServerNetworkHandler();
	private final HashMap<ResourceLocation, Supplier<C2SMessage>> types = new HashMap<>();

	public static void send(S2CMessage message, ServerPlayer... target) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeUtf(message.getId().toString());
		message.encode(buf);
		for (ServerPlayer entity : target)
			ServerPlayNetworking.send(entity, PonderClientNetworkHandler.CHANNEL_NAME, buf);
	}

	public static void register() {
		ServerPlayNetworking.registerGlobalReceiver(CHANNEL_NAME, INSTANCE);
		INSTANCE.registerMessage(SConfigureConfigPacket::new);
	}

	@Override
	public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
		ResourceLocation id = new ResourceLocation(buf.readUtf());
		if (!this.types.containsKey(id)) return;
		C2SMessage message = this.types.get(id).get();
		message.decode(buf);
		message.handle(server, player, handler, responseSender);
	}

	public void registerMessage(Supplier<C2SMessage> messageSupplier) {
		C2SMessage message = messageSupplier.get();
		this.types.put(message.getId(), messageSupplier);
	}
}
