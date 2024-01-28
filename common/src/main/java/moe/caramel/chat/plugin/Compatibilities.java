package moe.caramel.chat.plugin;

import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Compatibility data registry
 */
public final class Compatibilities {

    private static final Map<String, Data> REGISTRY = new HashMap<>();

    public static final Data EMI = register(
        "EMI", "dev.emi.emi.screen.widget.EmiSearchWidget",
        Set.of(
            "moe.caramel.chat.mixin.emi.MixinPluginEmiPort",
            "moe.caramel.chat.mixin.emi.MixinPluginEmiSearchWidget"
        )
    );

    public static final Data XAERO_MINIMAP = register(
        "Xaero's Minimap", "xaero.common.gui.GuiAddWaypoint",
        Set.of("moe.caramel.chat.mixin.xaeromap.MixinPluginXaeroMapWayPoint")
    );

    // ================================

    /**
     * Gets the compatibility data.
     *
     * @param mixinClass Mixin class name
     * @return Compatibility data
     */
    @Nullable
    public static Data getData(final String mixinClass) {
        return Compatibilities.REGISTRY.get(mixinClass);
    }

    /**
     * Compatibility data.
     *
     * @param name Mod name
     * @param targetClassName Detect target class name
     * @param classes Mixin classes
     */
    public record Data(String name, String targetClassName, Set<String> classes) {}

    /**
     * Register compatibility data.
     *
     * @param name Mod name
     * @param targetClassName Detect target class name
     * @param classes Mixin classes
     */
    private static Data register(final String name, final String targetClassName, final Set<String> classes) {
        final Data data = new Data(name, targetClassName, classes);
        for (final String clazz : classes) {
            Compatibilities.REGISTRY.put(clazz, data);
        }
        return data;
    }
}
