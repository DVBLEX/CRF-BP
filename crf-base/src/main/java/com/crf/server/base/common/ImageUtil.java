package com.crf.server.base.common;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;

public class ImageUtil {

    private static final int    IMAGE_PREFERED_WIDTH      = 1024;
    private static final int    IMAGE_PREFERED_HEIGHT     = 768;
    private static final float  IMAGE_COMPRESSION_QUALITY = 0.6f;
    private static final String IMAGE_TYPE                = "jpg";

    public static byte[] compressImageAndSmartResize(byte[] imageData) throws Exception {

        InputStream inputStream = new ByteArrayInputStream(imageData);
        BufferedImage bufferedImage = ImageIO.read(inputStream);

        int newWidth = bufferedImage.getWidth();
        int newHeight = bufferedImage.getHeight();

        if (newWidth > IMAGE_PREFERED_WIDTH) {

            double heightDecreaseRate = ((newWidth - IMAGE_PREFERED_WIDTH) * 100) / newWidth;
            newHeight -= (newHeight * heightDecreaseRate) / 100;
            newWidth = IMAGE_PREFERED_WIDTH;
        }

        if (newHeight > IMAGE_PREFERED_HEIGHT) {

            double withDecreaseRate = ((newHeight - IMAGE_PREFERED_HEIGHT) * 100) / newHeight;
            newWidth -= (newWidth * withDecreaseRate) / 100;
            newHeight = IMAGE_PREFERED_HEIGHT;
        }

        byte[] newImageDate = compressImage(resizeImage(bufferedImage, newWidth, newHeight));

        inputStream.close();
        return newImageDate;
    }

    public static byte[] compressImageAndResize(byte[] imageData, int newWidth, int newHeight) throws Exception {

        InputStream inputStream = new ByteArrayInputStream(imageData);
        BufferedImage bufferedImage = ImageIO.read(inputStream);

        byte[] newImageDate = compressImage(resizeImage(bufferedImage, newWidth, newHeight));

        inputStream.close();
        return newImageDate;
    }

    private static byte[] compressImage(BufferedImage bufferedImage) throws Exception {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName(IMAGE_TYPE);

        ImageWriter imageWriter = imageWriters.next();
        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);
        imageWriter.setOutput(imageOutputStream);

        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();

        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionQuality(IMAGE_COMPRESSION_QUALITY);

        imageWriter.write(null, new IIOImage(bufferedImage, null, null), imageWriteParam);

        outputStream.close();
        imageOutputStream.close();
        imageWriter.dispose();

        return outputStream.toByteArray();
    }

    public static byte[] compressImage(byte[] imageData) throws Exception {

        InputStream inputStream = new ByteArrayInputStream(imageData);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedImage bufferedImage = ImageIO.read(inputStream);

        Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName(IMAGE_TYPE);

        ImageWriter imageWriter = imageWriters.next();
        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);
        imageWriter.setOutput(imageOutputStream);

        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();

        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionQuality(IMAGE_COMPRESSION_QUALITY);

        imageWriter.write(null, new IIOImage(bufferedImage, null, null), imageWriteParam);

        inputStream.close();
        outputStream.close();
        imageOutputStream.close();
        imageWriter.dispose();

        return outputStream.toByteArray();
    }

    public static BufferedImage resizeImage(BufferedImage bufferedImageToResize, int newWidth, int newHeight) {

        Image tmpImage = bufferedImageToResize.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);
        BufferedImage resizedBufferedImage = new BufferedImage(newWidth, newHeight, bufferedImageToResize.getType());

        Graphics2D graphics2dap = resizedBufferedImage.createGraphics();
        graphics2dap.drawImage(tmpImage, 0, 0, null);
        graphics2dap.dispose();

        return resizedBufferedImage;
    }
}
