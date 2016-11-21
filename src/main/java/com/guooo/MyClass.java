package com.guooo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class MyClass {
    public static void main(String[] args) throws IOException {
        getGif("你去死吧死吧死吧", "你个铺盖恒阿灿", "啊啊啊啊啊");
    }


    private final static int FONT_SIZE = 30;
    private final static int LINE_GAP = 5;//字体上下边距
    private final static int FIRST_LINE = 1;//决定哪一行先滚动到视野中,0-第一行,1-第二行,2-第三行;
    private final static int INDENT = 3;//每一行和前一行的缩进字符个数;

    public static void getGif(String... strs) throws IOException {

        if (strs.length == 0)
            return;

        ArrayList<String> strings = new ArrayList<>();
        ArrayList<Integer> indentCounts = new ArrayList<>();
        int maxLength = 0;
        int count = 0;
        for (String string : strs) {
            if (string == null || string.equals("")) {
                return;
            }

            int length = string.length();
            int indentCount = 0;
            if (count >= FIRST_LINE) {
                indentCount = (count - FIRST_LINE) * INDENT;
            } else {
                indentCount = (strs.length - (FIRST_LINE - count)) * INDENT;
            }

            length = indentCount + length;
            if (length > maxLength) {
                maxLength = length;
            }
            count++;

            strings.add(string);
            indentCounts.add(indentCount);
        }

        int imageWidth = maxLength * FONT_SIZE;//图片长度
        int imageHeight = strs.length * (FONT_SIZE + LINE_GAP * 2);//图片的高度
        int range = imageWidth * 2;//最长的字最后距离图片开始的距离

        ArrayList<BufferedImage> bufferedImages = new ArrayList<>();
        int imageCount = 0;
        while (range > -FONT_SIZE) {
            BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = bufferedImage.getGraphics();
            Graphics2D g2d = (Graphics2D) graphics;
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    1f);
            g2d.setComposite(ac);
            graphics.fillRect(0, 0, imageWidth, imageHeight);
            AlphaComposite ac2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    1f);
            g2d.setComposite(ac2);
            //绘制字符串
            for (int i = 0; i < strings.size(); i++) {
                graphics.setColor(Color.RED);
                graphics.setFont(new Font("宋体", Font.PLAIN, 30));
                graphics.drawString(strings.get(i), imageWidth + (indentCounts.get(i) - imageCount) * FONT_SIZE, (FONT_SIZE + 2 * LINE_GAP) * (i + 1) - LINE_GAP);
            }
            ImageIO.write(bufferedImage, "png", new File("haha" + (imageCount + 1) + ".png"));
            bufferedImages.add(bufferedImage);
            graphics.dispose();
            range = range - FONT_SIZE;
            imageCount++;
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
