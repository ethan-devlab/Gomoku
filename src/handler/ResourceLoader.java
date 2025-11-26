package handler;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Utility class for centralized resource loading.
 * Provides consistent icon and image loading across all UI components.
 */
public class ResourceLoader {

    private static final String IMAGE_PATH = "/resources/image/";

    // Standard icon names
    public static final String ICON_BLACK = "black.png";
    public static final String ICON_WHITE = "white.png";
    public static final String ICON_USER = "user.png";
    public static final String ICON_HOURGLASS = "hourglass.png";
    public static final String ICON_CLOCK = "clock.png";
    public static final String ICON_TROPHY = "trophy-star.png";
    public static final String ICON_APP = "icon.png";
    public static final String ICON_RED_FLAG = "redflag.png";
    public static final String ICON_RIGHT_ARROW = "right-arrow.png";

    private final Class<?> resourceClass;

    public ResourceLoader() {
        this.resourceClass = getClass();
    }

    public ResourceLoader(Class<?> resourceClass) {
        this.resourceClass = resourceClass;
    }

    /**
     * Get a URL for a resource image.
     */
    public URL getImageUrl(String imageName) {
        return resourceClass.getResource(IMAGE_PATH + imageName);
    }

    /**
     * Load an ImageIcon from resources with specified dimensions.
     */
    public ImageIcon loadIcon(String imageName, int width, int height) {
        URL url = getImageUrl(imageName);
        if (url == null) {
            System.err.println("Resource not found: " + IMAGE_PATH + imageName);
            return null;
        }
        ImageIcon icon = new ImageIcon(url);
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    /**
     * Load an ImageIcon from resources with original dimensions.
     */
    public ImageIcon loadIcon(String imageName) {
        URL url = getImageUrl(imageName);
        if (url == null) {
            System.err.println("Resource not found: " + IMAGE_PATH + imageName);
            return null;
        }
        return new ImageIcon(url);
    }

    /**
     * Load an Image from resources with specified dimensions.
     */
    public Image loadImage(String imageName, int width, int height) {
        ImageIcon icon = loadIcon(imageName, width, height);
        return icon != null ? icon.getImage() : null;
    }

    /**
     * Get the application icon image.
     * Uses fallback paths for different runtime configurations.
     */
    public Image getAppIcon() {
        // Try resource path first
        URL url = getImageUrl(ICON_APP);
        if (url != null) {
            return Toolkit.getDefaultToolkit().getImage(url);
        }

        // Fallback to file system paths
        Path currentRelativePath = Path.of("");
        String basePath = currentRelativePath.toAbsolutePath().toString();

        String[] fallbackPaths = {
                basePath + "\\src\\resources\\image\\" + ICON_APP,
                basePath + "\\resources\\image\\" + ICON_APP,
                basePath + "/src/resources/image/" + ICON_APP,
                basePath + "/resources/image/" + ICON_APP
        };

        for (String path : fallbackPaths) {
            File file = new File(path);
            if (file.exists()) {
                return Toolkit.getDefaultToolkit().getImage(path);
            }
        }

        return null;
    }

    /**
     * Load a required icon, throwing exception if not found.
     */
    public ImageIcon loadRequiredIcon(String imageName, int width, int height) {
        URL url = Objects.requireNonNull(getImageUrl(imageName),
                "Required resource not found: " + IMAGE_PATH + imageName);
        ImageIcon icon = new ImageIcon(url);
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}
