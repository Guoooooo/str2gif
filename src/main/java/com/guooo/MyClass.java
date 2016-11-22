package com.guooo;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import sun.awt.SunHints;

public class MyClass {
    public static void main(String[] args) throws IOException {
        getGif("一二三四五六七八九十", "一二三四五六", "一二三四五六七");
    }


    private final static Float FONT_SIZE = 20f;
    private final static int LINE_GAP = 5;//字体上下边距
    private final static int FIRST_LINE = 1;//决定哪一行先滚动到视野中,0-第一行,1-第二行,2-第三行;
    private final static int INDENT = 3;//每一行和前一行的缩进字符个数;
    private final static String TTF_PATH = "truetype.ttf";
    private final static int[][] COLORS = {{200, 170, 227, 225}, {140, 226, 1, 255}, {150, 32, 238, 255}, {180, 238, 26, 255}, {50, 238, 215, 255}};

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

        int imageWidth = (int) (maxLength * FONT_SIZE);//图片长度
        int imageHeight = (int) (strs.length * (FONT_SIZE + LINE_GAP * 2));//图片的高度
        int range = imageWidth * 2;//最长的字最后距离图片开始的距离
        //准备随机色
        ArrayList<Color> colors = new ArrayList<>();
        for (int i = 0; i < strings.size(); i++) {
            int v = (int) (Math.random() * (COLORS.length - 1));//随机色
            colors.add(new Color(COLORS[v][0], COLORS[v][1], COLORS[v][2], COLORS[v][3]));
        }
        //准备字体
        Font font = loadFont(TTF_PATH, FONT_SIZE);

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
                graphics.setColor(colors.get(i));
                graphics.setFont(font);
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.drawString(strings.get(i), imageWidth + (indentAmounts.get(i) - imageCount) * FONT_SIZE, (imageHeight / strs.length * (i + 1)) - LINE_GAP);
            }
            graphics.dispose();
            ImageIO.write(bufferedImage, "png", new File("image" + (imageCount + 1) + ".png"));
            bufferedImages.add(bufferedImage);
            range = (int) (range - FONT_SIZE);
            imageCount++;
        }
        AnimatedGifEncoder e = new AnimatedGifEncoder();
        e.setRepeat(0);
        e.start("image" + (imageCount + 1) + ".gif");
        for (BufferedImage bufferedImage : bufferedImages) {
            e.setDelay(100);
            e.addFrame(bufferedImage);
        }
        e.finish();
    }

    public static Font loadFont(String fontFileName, float fontSize) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontFileName));
            font = font.deriveFont(fontSize);
            return font;
        } catch (Exception e) {
            e.printStackTrace();
            return new java.awt.Font("宋体", Font.PLAIN, (int) fontSize);
        }
    }

}
