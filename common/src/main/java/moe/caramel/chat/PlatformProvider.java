package moe.caramel.chat;

import moe.caramel.chat.util.ModLogger;

/**
 * Platform Provider Interface
 */
public abstract class PlatformProvider {

    /**
     * Mod id.
     */
    public static final String MOD_ID = "caramelchat";

    /**
     * Default Provider.
     */
    public static final PlatformProvider DEFAULT = new PlatformProvider() {
        @Override
        public String getVersion() {
            return "UNKNOWN";
        }

        @Override
        public String getPlatformName() {
            return "UNKNOWN";
        }

        @Override
        public boolean isModLoaded(String modid) {
            return false;
        }
    };

    // ================================

    private static PlatformProvider provider = PlatformProvider.DEFAULT;

    /**
     * Gets the Platform provider.
     *
     * @return provider
     */
    public static PlatformProvider getProvider() {
        return provider;
    }

    /**
     * Sets the Platform provider.
     *
     * @param provider provider
     */
    public static void setProvider(final PlatformProvider provider) {
        if (PlatformProvider.provider == PlatformProvider.DEFAULT) {
            PlatformProvider.provider = provider;
            ModLogger.log("The platform provider has been loaded: {}", provider);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    // ================================

    /**
     * Gets the current mod version.
     *
     * @return mod version
     */
    public abstract String getVersion();

    /**
     * Gets the current platform name.
     *
     * @return platform name
     */
    public abstract String getPlatformName();

    /**
     * Checks if a specific mod is installed
     *
     * @return true if the specified mod is loaded
     */
    public abstract boolean isModLoaded(String modid);

    @Override
    public String toString() {
        return "(" + getPlatformName() + " / " + getVersion() + ")";
    }
}
