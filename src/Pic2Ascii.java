import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;

public class Pic2Ascii {

    // The symbols to be used for the ascii picture, ordered from darker to
    // lighter
    private static char[] symbols = {'@', '#', '?', '*', '=', '^', '\''};

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println(
                    "Usage: Pic2Ascii <filename> <rectSizeX> <rectSizeY>"
            );
            return;
        }

        BufferedImage img;
        int rectSizeX, rectSizeY;

        try {
            File file = new File(args[0]);
            img = ImageIO.read(file);
        } catch (Exception e) {
            System.err.println("Error reading image. Please consult this " +
                    "beautiful stack trace:");
            e.printStackTrace();
            return;
        }

        if (img == null) {
            System.err.println("Error: not a valid picture.");
            return;
        }

        try {
            rectSizeX = Integer.parseInt(args[1]);
            rectSizeY = Integer.parseInt(args[2]);
        } catch (Exception e) {
            System.err.println(
                    "Error: the rectangle dimensions must be integers."
            );
            return;
        }

        printAscii(img, rectSizeX, rectSizeY);
    }

    /**
     * Prints an ascii picture representing the provided picture.
     * @param img the source image
     */
    public static void printAscii(BufferedImage img, int rectSizeX,
                                   int rectSizeY) {
        img = getGrayscaleImage(img);
        int width = (int) (img.getWidth() / rectSizeX);
        int height = (int) (img.getHeight() / rectSizeY);
        Raster raster = img.getData();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int avgLevel = getAverageLevel(raster,
                        j * rectSizeX, i * rectSizeY,
                        rectSizeX, rectSizeY);
                System.out.print(getSymbol(avgLevel));
            }
            System.out.println();
        }
    }

    /**
     * Returns a grayscale copy of an image.
     * @param srcImg the source image
     * @return a grayscale copy of the provided image.
     */
    private static BufferedImage getGrayscaleImage(BufferedImage srcImg) {
        BufferedImage grayscaleImg = new BufferedImage(srcImg.getWidth(),
                srcImg.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2 = grayscaleImg.createGraphics();
        g2.drawImage(srcImg, 0, 0, null);
        g2.dispose();
        return grayscaleImg;
    }

    /**
     * Returns the average level for a part of a raster image.
     * @param raster the raster image
     * @param startX the top-left x coordinate of the area
     * @param startY the top-left y coordinate of the area
     * @param sizeX the size of the area along the x-axis
     * @param sizeY the size of the area along the y-axis
     * @return the average level for the specified raster area.
     */
    private static int getAverageLevel(Raster raster, int startX, int startY,
                                       int sizeX, int sizeY) {
        int numPixels = sizeX * sizeY;
        int levelSum = 0;

        for (int i = startY, maxY = Math.min(raster.getHeight(),
                startY + sizeY); i < maxY; i++) {
            for (int j = startX, maxX = Math.min(raster.getWidth(),
                    startX + sizeX); j < maxX; j++) {
                levelSum += raster.getSample(j, i, 0);
            }
        }

        return levelSum / numPixels;
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
