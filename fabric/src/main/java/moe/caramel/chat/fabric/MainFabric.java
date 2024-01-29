package moe.caramel.chat.fabric;

import static moe.caramel.chat.PlatformProvider.MOD_ID;
import moe.caramel.chat.PlatformProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

/**
 * Fabric platform initializer
 */
public final class MainFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        final ModContainer container = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow();
        PlatformProvider.setProvider(new FabricProvider(container.getMetadata().getVersion()));
    }
}
