package moe.caramel.chat.forge;

import static moe.caramel.chat.PlatformProvider.MOD_ID;
import moe.caramel.chat.PlatformProvider;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(MOD_ID)
public final class MainForge {

    public MainForge() {
        final String version = FMLLoader.getLoadingModList().getModFileById(MOD_ID).versionString();
        PlatformProvider.setProvider(new ForgeProvider(version));
    }
}
