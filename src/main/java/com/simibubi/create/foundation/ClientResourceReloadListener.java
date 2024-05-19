package com.simibubi.create.foundation;

import com.simibubi.create.Create;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.utility.LangNumberFormat;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.util.Collection;
import java.util.Set;

public class ClientResourceReloadListener implements ResourceManagerReloadListener, IdentifiableResourceReloadListener {
	public static final ResourceLocation ID = Create.asResource("client_reload_listener");
	// fabric: make sure number format is updated after languages load
	public static final Set<ResourceLocation> DEPENDENCIES = Set.of(ResourceReloadListenerKeys.LANGUAGES);

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		CreateClient.invalidateRenderers();
		LangNumberFormat.numberFormat.update();
	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	@Override
	public Collection<ResourceLocation> getFabricDependencies() {
		return DEPENDENCIES;
	}
}
