import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;

public class Pic2Ascii {

    // The symbols to be used for the ascii picture, ordered from darker to
    // lighter
    private static char[] symbols = {'@', '"', '#', '?', '*', '^', '=', '\''};

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: Pic2Ascii <filename> <maxSize>");
            return;
        }

        BufferedImage img;
        int maxSize;

        try {
            File file = new File(args[0]);
            img = ImageIO.read(file);
        } catch (Exception e) {
            System.err.println("Error while reading image. Please consult " +
                    "this beautiful stack trace:");
            e.printStackTrace();
            return;
        }

        if (img == null) {
            System.err.println("Error: not a valid picture.");
            return;
        }

        try {
            maxSize = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.err.println("Error: the maxSize argument must be an int.");
            return;
        }

        System.out.println("Original size: " +
            img.getWidth() + " x " + img.getHeight());

        double d = Math.max(img.getWidth(), img.getHeight()) / maxSize;
        int width = (int) (img.getWidth() / d);
        int height = (int) (img.getHeight() / (d * 2.5));
        img = getScaledImage(img, width, height);

        System.out.println("New size: " +
                img.getWidth() + " x " + img.getHeight());

        printAscii(img);
    }

    /**
     * Prints an ascii picture representing the provided picture. The picture
     * should be in grayscale and not *too* large.
     * @param img the source image
     */
    private static void printAscii(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        Raster raster = img.getData();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int sample = raster.getSample(j, i, 0);
                System.out.print(getSymbol(sample));
            }
            System.out.println();
        }
    }

    /**
     * Returns a scaled and _grayscaled_ copy of an image.
     * @param srcImg the source image
     * @param width the desired width
     * @param height the desired height
     * @return a scaled copy of the source image in grayscale.
     */
    private static BufferedImage getScaledImage(BufferedImage srcImg,
                                                int width, int height) {
        BufferedImage resizedImg = new BufferedImage(width, height,
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, width, height, null);
        g2.dispose();
        return resizedImg;
    }

    /**
     * Returns the corresponding symbol for a given level.
     * @param level the level in the range [0, 255]
     * @return the symbol corresponding to the level.
     */
    private static char getSymbol(int level) {
        int interval = 255 / symbols.length;
        int symbolIndex = level / interval;

        if (symbolIndex < 0) {
            symbolIndex = 0;
        } else if (symbolIndex >= symbols.length) {
            symbolIndex = symbols.length - 1;
        }

        return symbols[symbolIndex];
    }

}
