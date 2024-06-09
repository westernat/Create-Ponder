package com.simibubi.create;

import com.iafenvoy.ponder.extra.network.PonderClientNetworkHandler;
import com.iafenvoy.ponder.extra.network.PonderServerNetworkHandler;
import com.simibubi.create.foundation.ClientResourceReloadListener;
import com.simibubi.create.foundation.events.ClientEvents;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.foundation.outliner.Outliner;
import com.simibubi.create.foundation.ponder.element.WorldSectionElement;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBufferCache;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;
import com.simibubi.create.infrastructure.ponder.PonderIndex;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

public class CreateClient implements ClientModInitializer {
	public static final SuperByteBufferCache BUFFER_CACHE = new SuperByteBufferCache();
	public static final Outliner OUTLINER = new Outliner();

	public static final ClientResourceReloadListener RESOURCE_RELOAD_LISTENER = new ClientResourceReloadListener();

	@Override
	public void onInitializeClient() {
		// clientInit start

		BUFFER_CACHE.registerCompartment(CachedBufferer.GENERIC_BLOCK);
		BUFFER_CACHE.registerCompartment(CachedBufferer.PARTIAL);
		BUFFER_CACHE.registerCompartment(CachedBufferer.DIRECTIONAL_PARTIAL);
		BUFFER_CACHE.registerCompartment(WorldSectionElement.DOC_WORLD_SECTION, 20);

		AllPonderTags.register();
		PonderIndex.register();
		UIRenderHelper.init();

		// fabric exclusive
		ClientEvents.register();

		PonderClientNetworkHandler.register();
	}

	public static void invalidateRenderers() {
		BUFFER_CACHE.invalidate();
	}

	public static void checkGraphicsFanciness() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null)
			return;

		if (mc.options.graphicsMode().get() != GraphicsStatus.FABULOUS)
			return;

		MutableComponent text = ComponentUtils.wrapInSquareBrackets(Components.literal("WARN"))
			.withStyle(ChatFormatting.GOLD)
			.append(Components.literal(
				" Some of Create's visual features will not be available while Fabulous graphics are enabled!"))
			.withStyle(style -> style
				.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/create dismissFabulousWarning"))
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					Components.literal("Click here to disable this warning"))));

		mc.player.displayClientMessage(text, false);
	}

}
