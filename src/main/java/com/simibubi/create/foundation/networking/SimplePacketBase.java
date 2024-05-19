package com.simibubi.create.foundation.networking;

import com.simibubi.create.foundation.mixin.fabric.BlockableEventLoopAccessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.Nullable;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.thread.BlockableEventLoop;

public abstract class SimplePacketBase implements C2SPacket, S2CPacket {

	public abstract void write(FriendlyByteBuf buffer);

	public abstract boolean handle(Context context);

	@Override
	public final void encode(FriendlyByteBuf buffer) {
		write(buffer);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void handle(Minecraft client, ClientPacketListener listener, PacketSender responseSender, SimpleChannel channel) {
		handle(new Context(client, listener, null));
	}

	@Override
	public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl listener, PacketSender responseSender, SimpleChannel channel) {
		handle(new Context(server, listener, player));
	}

	public enum NetworkDirection {
		PLAY_TO_CLIENT,
		PLAY_TO_SERVER
	}

	public record Context(BlockableEventLoop<? extends Runnable> executor, PacketListener listener, @Nullable ServerPlayer sender) {
		public void enqueueWork(Runnable runnable) {
			// Matches Forge's enqueueWork behavior.
			// MC will sometimes defer tasks even if already on the right thread.
			if (executor.isSameThread()) {
				runnable.run();
			} else {
				// skip extra checks
				((BlockableEventLoopAccessor) executor).callSubmitAsync(runnable);
			}
		}

		@Nullable
		public ServerPlayer getSender() {
			return sender();
		}

		public NetworkDirection getDirection() {
			return sender() == null ? NetworkDirection.PLAY_TO_SERVER : NetworkDirection.PLAY_TO_CLIENT;
		}
	}
}
