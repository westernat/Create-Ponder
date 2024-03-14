package com.simibubi.create.infrastructure.ponder;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;

public class PonderIndex {
    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(Create.ID);

    public static void register() {
        // Register storyboards here
        // (!) Added entries require re-launch
        // (!) Modifications inside storyboard methods only require re-opening the ui
    }

    public static boolean editingModeActive() {
        return AllConfigs.client().editingMode.get();
    }

}
