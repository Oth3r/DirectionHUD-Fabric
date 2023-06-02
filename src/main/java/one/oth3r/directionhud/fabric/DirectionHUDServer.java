package one.oth3r.directionhud.fabric;

import net.fabricmc.api.DedicatedServerModInitializer;
import one.oth3r.directionhud.fabric.files.LangReader;

public class DirectionHUDServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        DirectionHUD.isClient = false;
        LangReader.loadLanguageFile();
        DirectionHUD.initializeCommon();
    }
}
