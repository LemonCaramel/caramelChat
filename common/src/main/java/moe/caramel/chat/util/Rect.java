package moe.caramel.chat.util;

/**
 * Rect Square Constructor
 *
 * @param x x-coordinate of rect square
 * @param y y-coordinate of rect squares
 * @param width width of rect squares
 * @param height height of rect squares
 */
public record Rect(float x, float y, float width, float height) {

    /**
     * Empty Rect
     */
    public static final Rect EMPTY = new Rect(0, 0, 0, 0);

    /**
     * Copy to the float array.
     *
     * @return float array
     */
    public float[] copy() {
        return new float[] { x, y, width, height };
    }
}
