package net.hypercubemc.seedfix;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SeedFix implements ModInitializer {
    public static final String MODID = "seedfix";
    public static final Logger logger = LogManager.getLogger(MODID);

    @Override
    public void onInitialize() {
        ModContainer seedfix = FabricLoader.getInstance().getModContainer(MODID)
                .orElseThrow(() -> new IllegalStateException("Couldn't find the mod container for seedfix"));
        String version = seedfix.getMetadata().getVersion().getFriendlyString();
        logger.info("Loaded seedfix v" + version + " by Justsnoopy30!");
    }
}
