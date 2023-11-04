package moe.caramel.chat;

import com.sun.jna.Native;
import com.sun.jna.Platform;
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

/**
 * caramelChat Main
 */
public final class Main {

    public static final String MOD_ID = "caramelchat";
    static { Main.instance = new Main(); }

    public static final boolean DEBUG = false;

    private static Main instance;
    private final IController controller;

    private Main() {
        this.controller = IController.getController();

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
        Main.getController().changeFocusedScreen(screen);
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
