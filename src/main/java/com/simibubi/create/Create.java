package com.simibubi.create;

import com.iafenvoy.ponder.extra.network.PonderServerNetworkHandler;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.events.CommonEvents;
import com.simibubi.create.foundation.ponder.FabricPonderProcessing;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

import java.util.Random;

public class Create implements ModInitializer {
	public static final String ID = "createponder";
	public static final String NAME = "Create Ponder";
	public static final String VERSION = "0.0.2b";
	public static final Logger LOGGER = LogUtils.getLogger();

	/** Use the {@link Random} of a local {@link Level} or {@link Entity} or create one */
	@Deprecated
	public static final Random RANDOM = new Random();

	@Override
	public void onInitialize() { // onCtor
		AllParticleTypes.register();
		AllConfigs.register();
		CommonEvents.register();
		FabricPonderProcessing.init();

		PonderServerNetworkHandler.register();
	}

	public static ResourceLocation asResource(String path) {
		return new ResourceLocation(ID, path);
	}
}

