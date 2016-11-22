package com.guooo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class MyClass {
    public static void main(String[] args) throws IOException {
        getGif("一二三四五六七八九十", "一二三四五六", "一二三四五六七");
    }


    private final static int FONT_SIZE = 30;
    private final static int LINE_GAP = 5;//字体上下边距
    private final static int FIRST_LINE = 1;//决定哪一行先滚动到视野中,0-第一行,1-第二行,2-第三行;
    private final static int INDENT = 3;//每一行和前一行的缩进字符个数;

    public static void getGif(String... strs) throws IOException {

        if (strs.length == 0)
            return;

        ArrayList<String> strings = new ArrayList<>();
        ArrayList<Integer> indentAmounts = new ArrayList<>();
        int maxLength = 0;
        int strCount = 0;
        for (String string : strs) {
            if (string == null || string.equals("")) {
                return;
            }

            int length = string.length();
            int indentAmount;
            if (strCount >= FIRST_LINE) {
                indentAmount = (strCount - FIRST_LINE) * INDENT;
            } else {
                indentAmount = (strs.length - (FIRST_LINE - strCount)) * INDENT;
            }

            length = indentAmount + length;
            if (length > maxLength) {
                maxLength = length;
            }
            strCount++;

            strings.add(string);
            indentAmounts.add(indentAmount);
        }

        int imageWidth = maxLength * FONT_SIZE;//图片长度
        int imageHeight = strs.length * (FONT_SIZE + LINE_GAP * 2);//图片的高度
        int range = imageWidth * 2;//最长的字最后距离图片开始的距离

        ArrayList<BufferedImage> bufferedImages = new ArrayList<>();
        int imageCount = 0;
        while (range > -FONT_SIZE) {
            BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f);
            graphics.setComposite(ac);
            graphics.fillRect(0, 0, imageWidth, imageHeight);
            ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
            graphics.setComposite(ac);
            //绘制字符串
            for (int i = 0; i < strings.size(); i++) {
//                if (i == 0)
                graphics.setColor(Color.black);
//                if (i == 1)
//                    graphics.setColor(Color.GREEN);
//                if (i == 2)
//                    graphics.setColor(Color.YELLOW);
                graphics.setFont(new Font("宋体", Font.PLAIN, 30));
                graphics.drawString(strings.get(i), imageWidth + (indentAmounts.get(i) - imageCount) * FONT_SIZE, (imageHeight / strs.length * (i + 1)) - LINE_GAP);
            }
            graphics.dispose();
            ImageIO.write(bufferedImage, "png", new File("image" + (imageCount + 1) + ".png"));
            bufferedImages.add(bufferedImage);
            range = range - FONT_SIZE;
            imageCount++;
        }
        AnimatedGifEncoder e = new AnimatedGifEncoder();
        e.setTransparent(new Color(0, 255, 255, 255));
        e.setRepeat(0);
        e.start("gifImage.gif");
        for (BufferedImage bufferedImage : bufferedImages) {
            e.setDelay(100);
            e.addFrame(bufferedImage);
        }
        e.finish();
    }

}
