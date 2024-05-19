package com.simibubi.create.foundation.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.Create;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.ponder.PonderTooltipHandler;
import com.simibubi.create.foundation.render.SuperRenderTypeBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.CameraAngleAnimationService;
import com.simibubi.create.foundation.utility.worldWrappers.WrappedClientWorld;
import com.simibubi.create.infrastructure.gui.OpenCreateMenuButton;
import io.github.fabricators_of_create.porting_lib.event.client.CameraSetupCallback;
import io.github.fabricators_of_create.porting_lib.event.client.CameraSetupCallback.CameraInfo;
import io.github.fabricators_of_create.porting_lib.event.client.ClientWorldEvents;
import io.github.fabricators_of_create.porting_lib.event.client.ParticleManagerRegistrationCallback;
import io.github.fabricators_of_create.porting_lib.event.client.RenderTooltipBorderColorCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ClientEvents {
    public static void onTick(Minecraft client) {
        if (!isGameActive())
            return;

        AnimationTickHolder.tick();

        PonderTooltipHandler.tick();
        CreateClient.OUTLINER.tickOutlines();
        CameraAngleAnimationService.tick();
    }

    public static void onJoin(ClientPacketListener handler, PacketSender sender, Minecraft client) {
        CreateClient.checkGraphicsFanciness();
    }

    public static void onLoadWorld(Minecraft client, ClientLevel world) {
        if (world.isClientSide() && world instanceof ClientLevel && !(world instanceof WrappedClientWorld)) {
            CreateClient.invalidateRenderers();
            AnimationTickHolder.reset();
        }
    }

    public static void onUnloadWorld(Minecraft client, ClientLevel world) {
        if (world.isClientSide()) {
            CreateClient.invalidateRenderers();
            AnimationTickHolder.reset();
        }
    }

    public static void onRenderWorld(WorldRenderContext event) {
        PoseStack ms = event.matrixStack();
        ms.pushPose();
        SuperRenderTypeBuffer buffer = SuperRenderTypeBuffer.getInstance();
        float partialTicks = AnimationTickHolder.getPartialTicks();
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        CreateClient.OUTLINER.renderOutlines(ms, buffer, camera, partialTicks);

        buffer.draw();
        RenderSystem.enableCull();
        ms.popPose();
    }

    public static boolean onCameraSetup(CameraInfo info) {
        float partialTicks = AnimationTickHolder.getPartialTicks();

        if (CameraAngleAnimationService.isYawAnimating())
            info.yaw = CameraAngleAnimationService.getYaw(partialTicks);

        if (CameraAngleAnimationService.isPitchAnimating())
            info.pitch = CameraAngleAnimationService.getPitch(partialTicks);
        return false;
    }

    public static RenderTooltipBorderColorCallback.BorderColorEntry getItemTooltipColor(ItemStack stack, int originalBorderColorStart, int originalBorderColorEnd) {
        return PonderTooltipHandler.handleTooltipColor(stack, originalBorderColorStart, originalBorderColorEnd);
    }

    public static void addToItemTooltip(ItemStack stack, TooltipFlag iTooltipFlag, List<Component> itemTooltip) {
        Player player = Minecraft.getInstance().player;
        if (player == null)
            return;

        Item item = stack.getItem();
        TooltipModifier modifier = TooltipModifier.REGISTRY.get(item);
        if (modifier != null && modifier != TooltipModifier.EMPTY) {
            modifier.modify(stack, player, iTooltipFlag, itemTooltip);
        }

        PonderTooltipHandler.addToTooltip(stack, itemTooltip);
    }

    protected static boolean isGameActive() {
        return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
    }

    public static class ModBusEvents {
        public static void registerClientReloadListeners() {
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(CreateClient.RESOURCE_RELOAD_LISTENER);
        }
    }

    public static void register() {
        ModBusEvents.registerClientReloadListeners();

        ClientTickEvents.END_CLIENT_TICK.register(ClientEvents::onTick);
        ClientWorldEvents.LOAD.register(ClientEvents::onLoadWorld);
        ClientWorldEvents.UNLOAD.register(ClientEvents::onUnloadWorld);
        ClientWorldEvents.UNLOAD.register(CommonEvents::onUnloadWorld);
        ClientPlayConnectionEvents.JOIN.register(ClientEvents::onJoin);
        WorldRenderEvents.AFTER_TRANSLUCENT.register(ClientEvents::onRenderWorld);
        ItemTooltipCallback.EVENT.register(ClientEvents::addToItemTooltip);
        RenderTooltipBorderColorCallback.EVENT.register(ClientEvents::getItemTooltipColor);
        CameraSetupCallback.EVENT.register(ClientEvents::onCameraSetup);

        // External Events

        ParticleManagerRegistrationCallback.EVENT.register(AllParticleTypes::registerFactories);
        // we need to add our config button after mod menu, so we register our event with a phase that comes later
        ResourceLocation latePhase = Create.asResource("late");
        ScreenEvents.AFTER_INIT.addPhaseOrdering(Event.DEFAULT_PHASE, latePhase);
        ScreenEvents.AFTER_INIT.register(latePhase, OpenCreateMenuButton.OpenConfigButtonHandler::onGuiInit);
    }
}
