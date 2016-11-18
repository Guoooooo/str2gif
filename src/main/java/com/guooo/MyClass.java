package com.guooo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class MyClass {
    public static void main(String[] args) throws IOException {
        getGif("你去死吧死吧死吧");
    }

    public static void getGif(String string) throws IOException {
        int imageWidth = 240;
        int imageHeight = 120;
        int length = string.length();
        int range = imageWidth + length * 30;
        int count = 0;
        ArrayList<BufferedImage> bufferedImages = new ArrayList<>();
        while (range > 0 - 30) {
            BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = bufferedImage.getGraphics();
            graphics.setColor(Color.BLUE);
            graphics.fillRect(0, 0, imageWidth, imageHeight);
            graphics.setColor(Color.RED);
            graphics.setFont(new Font("宋体", Font.PLAIN, 30));
            graphics.drawString(string, imageWidth - count * 30, 5 + 30);
            ImageIO.write(bufferedImage, "png", new File("haha" + (count + 1) + ".png"));
            bufferedImages.add(bufferedImage);
            graphics.dispose();
            range = range - 30;
            count++;
        }

        AnimatedGifEncoder e = new AnimatedGifEncoder();
        e.setRepeat(0);
        e.start("laoma.gif");
        for (BufferedImage bufferedImage : bufferedImages) {
            e.setDelay(100);
            e.addFrame(bufferedImage);
        }
        e.finish();
    }
}
