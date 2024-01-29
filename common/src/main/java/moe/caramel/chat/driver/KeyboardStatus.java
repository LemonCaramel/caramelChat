package moe.caramel.chat.driver;

/**
 * Gets the current keyboard status.
 *
 * @param language current ime language
 * @param useNative whether to using the native language
 */
public record KeyboardStatus(Language language, boolean useNative) {

    @Override
    public Language language() {
        return useNative() ? language : Language.ENGLISH;
    }

    /**
     * Gets the display to use for language change notifications.
     *
     * @return display
     */
    public String display() {
        return language().display;
    }

    /**
     * Gets the indicator X offset.
     *
     * @return X offset
     */
    public float offset() {
        return language().offset;
    }

    /**
     * Display List
     */
    public enum Language {

        ENGLISH("ENG", 0.5f),
        KOREAN("한", 0.0f),
        JAPANESE("あ", 0.5f),
        CHINESE_SIMPLIFIED("中", 0.5f),
        CHINESE_TRADITIONAL("中", 0.5f),
        OTHER("Native", 0.5f);

        private final String display;
        private final float offset;

        Language(final String display, final float offset) {
            this.display = display;
            this.offset = offset;
        }
    }
}
