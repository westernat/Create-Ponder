package com.simibubi.create.infrastructure.config;

import com.simibubi.create.foundation.config.ConfigBase;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class AllConfigs {

	private static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);

	private static CClient client;

	public static CClient client() {
		return client;
	}

	private static <T extends ConfigBase> T register(Supplier<T> factory) {
		Pair<T, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(builder -> {
			T config = factory.get();
			config.registerAll(builder);
			return config;
		});

		T config = specPair.getLeft();
		config.specification = specPair.getRight();
		CONFIGS.put(ModConfig.Type.CLIENT, config);
		return config;
	}

	public static void register(ModLoadingContext context) {
		client = register(CClient::new);

		for (Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet()) {
			context.registerConfig(pair.getKey(), pair.getValue().specification);
		}
	}

	@SubscribeEvent
	public static void onLoad(ModConfigEvent.Loading event) {
		for (ConfigBase config : CONFIGS.values()) {
			if (config.specification == event.getConfig()
				.getSpec())
				config.onLoad();
		}
	}

	@SubscribeEvent
	public static void onReload(ModConfigEvent.Reloading event) {
		for (ConfigBase config : CONFIGS.values()) {
			if (config.specification == event.getConfig()
				.getSpec())
				config.onReload();
		}
	}
}
