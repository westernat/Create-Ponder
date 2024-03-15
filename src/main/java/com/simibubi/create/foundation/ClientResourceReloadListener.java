package com.simibubi.create.foundation;

import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.utility.LangNumberFormat;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

public class ClientResourceReloadListener implements ResourceManagerReloadListener {
    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        CreateClient.invalidateRenderers();
        LangNumberFormat.numberFormat.update();
    }
}
