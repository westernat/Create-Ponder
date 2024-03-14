package com.simibubi.create.infrastructure.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class CClient extends ConfigBase {

	public final ConfigGroup client = group(0, "client", Comments.client);

	public final ConfigInt ingameMenuConfigButtonRow = i(3, 0, 5, "ingameMenuConfigButtonRow",
		Comments.ingameMenuConfigButtonRow);
	public final ConfigInt ingameMenuConfigButtonOffsetX = i(-4, Integer.MIN_VALUE, Integer.MAX_VALUE, "ingameMenuConfigButtonOffsetX",
		Comments.ingameMenuConfigButtonOffsetX);

	//ponder group
	public final ConfigGroup ponder = group(1, "ponder", Comments.ponder);
	public final ConfigBool comfyReading = b(false, "comfyReading", Comments.comfyReading);
	public final ConfigBool editingMode = b(false, "editingMode", Comments.editingMode);

	@Override
	public String getName() {
		return "client";
	}

	private static class Comments {
		static String client = "Client-only settings - If you're looking for general settings, look inside your worlds serverconfig folder!";
		static String ponder = "Ponder settings";
		static String comfyReading = "Slow down a ponder scene whenever there is text on screen.";
		static String editingMode = "Show additional info in the ponder view and reload scene scripts more frequently.";
		static String[] ingameMenuConfigButtonRow = new String[]{
			"Choose the menu row that the Create config button appears on in the in-game menu",
			"Set to 0 to disable the button altogether"
		};
		static String[] ingameMenuConfigButtonOffsetX = new String[]{
			"Offset the Create config button in the in-game menu by this many pixels on the X axis",
			"The sign (-/+) of this value determines what side of the row the button appears on (left/right)"
		};
	}

}
