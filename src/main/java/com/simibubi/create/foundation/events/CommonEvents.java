package com.simibubi.create.foundation.events;

import com.mojang.brigadier.CommandDispatcher;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.utility.WorldAttached;
import com.simibubi.create.infrastructure.command.AllCommands;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;

import java.util.concurrent.Executor;

public class CommonEvents {
	public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
		AllCommands.register(dispatcher);
	}

	public static void onUnloadWorld(Executor executor, LevelAccessor world) {
		WorldAttached.invalidateWorld(world);
	}

	public static void addPackFinders() {
		ModContainer create = FabricLoader.getInstance().getModContainer(Create.ID)
				.orElseThrow(() -> new IllegalStateException("Create's ModContainer couldn't be found!"));
		ResourceLocation packId = Create.asResource("legacy_copper");
		ResourceManagerHelper.registerBuiltinResourcePack(packId, create, "Create Legacy Copper", ResourcePackActivationType.NORMAL);
	}

	public static void register() {
		// Fabric Events
		ServerWorldEvents.UNLOAD.register(CommonEvents::onUnloadWorld);
		CommandRegistrationCallback.EVENT.register(CommonEvents::registerCommands);
		// fabric: some features using events on forge don't use events here.
		// they've been left in this class for upstream compatibility.
		CommonEvents.addPackFinders();
	}
}
