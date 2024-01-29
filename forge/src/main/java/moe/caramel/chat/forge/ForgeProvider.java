package moe.caramel.chat.forge;

import moe.caramel.chat.PlatformProvider;

/**
 * Forge Provider
 */
public final class ForgeProvider extends PlatformProvider {

    private final String version;

    public ForgeProvider(final String version) {
        this.version = version;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getPlatformName() {
        return "Forge";
    }
}
