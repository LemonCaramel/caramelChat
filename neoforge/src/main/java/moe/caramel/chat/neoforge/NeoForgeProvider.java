package moe.caramel.chat.neoforge;

import moe.caramel.chat.PlatformProvider;

/**
 * NeoForge Provider
 */
public final class NeoForgeProvider extends PlatformProvider {

    private final String version;

    public NeoForgeProvider(final String version) {
        this.version = version;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }
}
