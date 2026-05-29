package moe.caramel.chat;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import me.decce.ixeris.api.IxerisApi;
import moe.caramel.chat.driver.IController;
import moe.caramel.chat.driver.arch.unknown.UnknownController;
import moe.caramel.chat.util.ModLogger;
import net.minecraft.client.gui.screens.Screen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.function.Supplier;

/**
 * caramelChat Main
 */
public final class Main {

    static { Main.instance = new Main(); }
    public static final boolean DEBUG = false;
    public static final boolean IXERIS_INSTALLED = PlatformProvider.getProvider().isModLoaded("ixeris");

    private static Main instance;
    private final IController controller;

    private Main() {
        this.controller = queryFromMainThread(IController::getController);

        if (controller instanceof UnknownController) {
            ModLogger.error("caramelChat can't find appropriate Controller in running OS");
        }
    }

    /**
     * Gets the Main instance.
     *
     * @return instance
     */
    public static Main getInstance() {
        return instance;
    }

    /**
     * Gets the current controller.
     *
     * @return controller
     */
    public static IController getController() {
        return Main.getInstance().controller;
    }

    /**
     * Sets the current screen.
     *
     * @param screen current screen
     */
    public static void setScreen(final Screen screen) {
        runOnMainThread(() -> Main.getController().changeFocusedScreen(screen));
    }

    /**
     * Runs the specified Runnable on the main thread
     * @param runnable The Runnable to run on the main thread
     */
    public static void runOnMainThread(final Runnable runnable) {
        if (IXERIS_INSTALLED) {
            IxerisApi.getInstance().runOnMainThread(runnable);
        }
        else {
            runnable.run();
        }
    }

    /**
     * Queries the result of the specified Supplier from the main thread
     * @param supplier The Supplier to query from the main thread
     */
    public static <T> T queryFromMainThread(final Supplier<T> supplier) {
        if (IXERIS_INSTALLED) {
            return IxerisApi.getInstance().query(supplier);
        }
        else {
            return supplier.get();
        }
    }

    /**
     * Runs the specified Runnable on the render thread
     * @param runnable The Runnable to run on the render thread
     */
    public static void runOnRenderThread(final Runnable runnable) {
        if (IXERIS_INSTALLED) {
            IxerisApi.getInstance().runLaterOnRenderThread(runnable);
        }
        else {
            runnable.run();
        }
    }

    /**
     * Copy the library to a temp directory.
     *
     * @param name library name
     * @return copied library path
     */
    public static String copyLibrary(final String name) {
        try {
            final URL url = Main.class.getClassLoader().getResource("native/" + name);
            if (url == null) {
                throw new IOException("Native library (" + name + ") not found.");
            }

            final File lib = File.createTempFile("caramelchat", Platform.isWindows() ? ".dll" : null, tempDir());
            try (
                final InputStream is = url.openStream();
                final FileOutputStream fos = new FileOutputStream(lib)
            ) {
                ModLogger.debug("Extracting library to {}", lib.getAbsolutePath());
                fos.write(is.readAllBytes());
                lib.deleteOnExit();
            }

            ModLogger.log("CocoaInput Driver has copied library to native directory.");

            return lib.getAbsolutePath();
        } catch (final Exception exception) {
            ModLogger.error("An error occurred while loading the library.");
            throw new RuntimeException(exception);
        }
    }

    private static File tempDir() throws IOException {
        try {
            final Method method = Native.class.getDeclaredMethod("getTempDir");
            method.setAccessible(true);
            return (File) method.invoke(null);
        } catch (final Exception exception) {
            return File.createTempFile("native", "temp");
        }
    }
}
