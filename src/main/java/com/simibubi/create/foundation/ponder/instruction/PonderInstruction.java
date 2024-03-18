package com.simibubi.create.foundation.ponder.instruction;

import com.almostreliable.ponderjs.util.PonderErrorHelper;
import com.simibubi.create.foundation.ponder.PonderScene;
import dev.latvian.mods.rhino.RhinoException;
import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

public abstract class PonderInstruction {

    public boolean isBlocking() {
        return false;
    }

    public void reset(PonderScene scene) {
    }

    public abstract boolean isComplete();

    public void onScheduled(PonderScene scene) {
    }

    public abstract void tick(PonderScene scene);

    public static PonderInstruction simple(Consumer<PonderScene> callback) {
        return new Simple(callback);
    }

    private static class Simple extends PonderInstruction {

        private Consumer<PonderScene> callback;

        public Simple(Consumer<PonderScene> callback) {
            this.callback = ponderScene -> {
                try {
                    callback.accept(ponderScene);
                } catch (RhinoException e) {
                    PonderErrorHelper.yeet(e);
                    if (Minecraft.getInstance() != null) {
                        Minecraft.getInstance().setScreen(null);
                    }
                }
            };
        }

        @Override
        public boolean isComplete() {
            return true;
        }

        @Override
        public void tick(PonderScene scene) {
            callback.accept(scene);
        }

    }

}
