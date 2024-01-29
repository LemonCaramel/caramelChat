package moe.caramel.chat.fabric;

import moe.caramel.chat.PlatformProvider;
import net.fabricmc.loader.api.Version;

/**
 * Fabric Provider
 */
public final class FabricProvider extends PlatformProvider {

    private final String version;

    public FabricProvider(final Version version) {
        this.version = version.getFriendlyString();
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getPlatformName() {
        return "Fabric";
    }
}
