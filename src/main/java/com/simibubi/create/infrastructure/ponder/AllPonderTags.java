package com.simibubi.create.infrastructure.ponder;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.ponder.PonderTag;

public class AllPonderTags {
	private static PonderTag create(String id) {
		return new PonderTag(Create.asResource(id));
	}

	public static void register() {
		// Add items to tags here
	}
}
