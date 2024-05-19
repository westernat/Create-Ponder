package com.simibubi.create.infrastructure.config;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.config.ConfigBase;
import io.github.fabricators_of_create.porting_lib.config.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

public class AllConfigs {

	private static final Map<ConfigType, ConfigBase> CONFIGS = new EnumMap<>(ConfigType.class);

	private static CClient client;

	public static CClient client() {
		return client;
	}

	private static <T extends ConfigBase> T register(Supplier<T> factory, ConfigType side) {
		Pair<T, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(builder -> {
			T config = factory.get();
			config.registerAll(builder);
			return config;
		});

		T config = specPair.getLeft();
		config.specification = specPair.getRight();
		CONFIGS.put(side, config);
		return config;
	}

	public static void register() {
		client = register(CClient::new, ConfigType.CLIENT);

		for (Entry<ConfigType, ConfigBase> pair : CONFIGS.entrySet())
			ConfigRegistry.registerConfig(Create.ID, pair.getKey(), pair.getValue().specification);

		ConfigEvents.LOADING.register(AllConfigs::onLoad);
		ConfigEvents.RELOADING.register(AllConfigs::onReload);
	}

	public static void onLoad(ModConfig modConfig) {
		for (ConfigBase config : CONFIGS.values())
			if (config.specification == modConfig
				.getSpec())
				config.onLoad();
	}

	public static void onReload(ModConfig modConfig) {
		for (ConfigBase config : CONFIGS.values())
			if (config.specification == modConfig
				.getSpec())
				config.onReload();
	}
}
