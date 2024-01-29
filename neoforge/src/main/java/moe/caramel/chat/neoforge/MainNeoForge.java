package moe.caramel.chat.neoforge;

import static moe.caramel.chat.PlatformProvider.MOD_ID;
import moe.caramel.chat.PlatformProvider;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;

@Mod(MOD_ID)
public final class MainNeoForge {

    public MainNeoForge() {
        final String version = FMLLoader.getLoadingModList().getModFileById(MOD_ID).versionString();
        PlatformProvider.setProvider(new NeoForgeProvider(version));
    }
}
