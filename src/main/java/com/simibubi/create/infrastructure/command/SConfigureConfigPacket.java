package com.simibubi.create.infrastructure.command;

import com.mojang.logging.LogUtils;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.ui.PonderIndexScreen;
import com.simibubi.create.foundation.ponder.ui.PonderUI;
import com.tterrag.registrate.fabric.EnvExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SConfigureConfigPacket extends SimplePacketBase {
	private static final Logger LOGGER = LogUtils.getLogger();

	private final String option;
	private final String value;

	public SConfigureConfigPacket(String option, String value) {
		this.option = option;
		this.value = value;
	}

	public SConfigureConfigPacket(FriendlyByteBuf buffer) {
		this.option = buffer.readUtf(32767);
		this.value = buffer.readUtf(32767);
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(option);
		buffer.writeUtf(value);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> {
			try {
				Actions.valueOf(option)
					.performAction(value);
			} catch (IllegalArgumentException e) {
				LOGGER.warn("Received ConfigureConfigPacket with invalid Option: " + option);
			}
		}));
		return true;
	}

	public enum Actions {
		openPonder(() -> Actions::openPonder);

		private final Supplier<Consumer<String>> consumer;

		Actions(Supplier<Consumer<String>> action) {
			this.consumer = action;
		}

		void performAction(String value) {
			consumer.get()
				.accept(value);
		}

		@Environment(EnvType.CLIENT)
		private static void openPonder(String value) {
			if (value.equals("index")) {
				ScreenOpener.transitionTo(new PonderIndexScreen());
				return;
			}

			ResourceLocation id = new ResourceLocation(value);
			if (!PonderRegistry.ALL.containsKey(id)) {
				Create.LOGGER.error("Could not find ponder scenes for item: " + id);
				return;
			}

			ScreenOpener.transitionTo(PonderUI.of(id));
		}
	}
}
