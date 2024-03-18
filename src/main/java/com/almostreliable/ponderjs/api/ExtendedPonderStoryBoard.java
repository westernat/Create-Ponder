package com.almostreliable.ponderjs.api;

@FunctionalInterface
public interface ExtendedPonderStoryBoard {
    void program(ExtendedSceneBuilder scene, SceneBuildingUtilDelegate util);
}
