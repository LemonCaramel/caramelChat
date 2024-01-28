package moe.caramel.chat.plugin;

import joptsimple.internal.Strings;
import moe.caramel.chat.plugin.Compatibilities.Data;
import moe.caramel.chat.util.ModLogger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Mixin Plugin for compatibility with other mods
 */
public final class MixinPlugin implements IMixinConfigPlugin {

    private final Map<Data, Boolean> checkStatus = new HashMap<>();

    @Override
    public void onLoad(final String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(final String targetClassName, final String mixinClassName) {
        final Data data = Compatibilities.getData(mixinClassName);
        if (data == null) {
            return true;
        }

        return checkStatus.computeIfAbsent(data, target -> {
            try {
                MixinService.getService().getBytecodeProvider().getClassNode(target.targetClassName());
                return true;
            } catch (final Exception ignored) {
                return false;
            }
        });
    }

    @Override
    public void acceptTargets(final Set<String> myTargets, final Set<String> otherTargets) {
        // Print logs
        final List<String> mods = new ArrayList<>();
        for (final Entry<Data, Boolean> data : this.checkStatus.entrySet()) {
            if (data.getValue()) {
                mods.add(data.getKey().name());
            }
        }

        if (mods.isEmpty()) {
            ModLogger.log("Unable to find mods compatible with caramelChat.");
        } else {
            ModLogger.log("Found mods compatible with caramelChat: {}", Strings.join(mods, ", "));
        }
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) {
    }
}
