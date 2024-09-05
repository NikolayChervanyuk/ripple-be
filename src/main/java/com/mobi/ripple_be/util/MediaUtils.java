package com.mobi.ripple_be.util;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

@Slf4j
public class MediaUtils {

    public static class JPG {

        public static boolean compressImage(File src, File dest, int maxByteSize) {
            float quality = (float) maxByteSize / src.length();
            return compressImage(src, dest, quality);
        }

        public static boolean compressImage(File src, File dest, float quality) {
            try {
                BufferedImage inputImage = ImageIO.read(src);

                Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
                ImageWriter writer = writers.next();

                ImageOutputStream outputStream = ImageIO.createImageOutputStream(dest);
                writer.setOutput(outputStream);

                ImageWriteParam params = writer.getDefaultWriteParam();
                params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                params.setCompressionQuality(quality);

                writer.write(null, new IIOImage(inputImage, null, null), params);

                outputStream.close();
                writer.dispose();

                return true;
            } catch (IOException e) {
                log.error(e.getMessage());
                return false;
            }
        }

        public static boolean resizeImage(File src, File dest, int targetUniformSizePx) {
            try {
                final BufferedImage inputImage = ImageIO.read(src);
                final int width = inputImage.getWidth();
                final int height = inputImage.getHeight();

                final double aspectRatio = (double) width / (double) height;
                final int targetWidth = Long.valueOf(Math.round(targetUniformSizePx * aspectRatio)).intValue();
                final var targetHeight = Long.valueOf(Math.round(targetUniformSizePx / aspectRatio)).intValue();

                final Image resultingImage = inputImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                final BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
                outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);

                return ImageIO.write(outputImage, "jpg", dest);
            } catch (IOException e) {
                log.error(e.getMessage());
                return false;
            }
        }

        public static BufferedImage cropImage(
                File src,
                int width,
                int height,
                double centerOffsetX,
                double centerOffsetY
        ) {
            try {
                var img = ImageIO.read(src);

                var finalWidth = width;
                if (finalWidth > img.getWidth()) {
                    finalWidth = img.getWidth();
                }
                var finalHeight = height;
                if (finalHeight > img.getHeight()) {
                    finalHeight = img.getHeight();
                }
                var xStart = img.getWidth() * centerOffsetX;
                xStart -= (double) finalWidth / 2;
                if (xStart < 0) {
                    xStart = 0;
                }

                var yStart = img.getHeight() * centerOffsetY;
                yStart -= (double) finalHeight / 2;
                if (yStart < 0) {
                    yStart = 0;
                }

                var xStartInt = (int) Math.round(xStart);
                var yStartInt = (int) Math.round(yStart);

                return img.getSubimage(xStartInt, yStartInt, finalWidth, finalHeight);

            } catch (IOException e) {
                log.error(e.getMessage());
                return null;
            }
        }
    }
}
