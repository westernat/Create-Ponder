package com.simibubi.create;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.utility.AttachedRegistry;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.Random;

@Mod(Create.ID)
public class Create {
    public static final String ID = "create";
    public static final String NAME = "Create Ponder";
    public static final String VERSION = "0.0.1b";
    public static final Logger LOGGER = LogUtils.getLogger();


    /**
     * Use the {@link Random} of a local {@link Level} or {@link Entity} or create one
     */
    @Deprecated
    public static final Random RANDOM = new Random();

    public Create() {
        onCtor();
    }

    public static void onCtor() {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        AllStructureProcessorTypes.register(modEventBus);
        AllPackets.registerPackets();
        AllConfigs.register(modLoadingContext);
        modEventBus.addListener(Create::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CreateClient.onCtorClient(modEventBus));
    }

    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> AttachedRegistry.unwrapAll());
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(ID, path);
    }
}
