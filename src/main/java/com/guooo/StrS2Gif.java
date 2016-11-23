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

public class StrS2Gif {
    public static void main(String[] args) throws IOException {
        getGif("一二三四五六七八九十", "十九八七六五", "三四五六七八九");
    }


    private final static int LINE_GAP = 5;//字体上下边距
    private final static int FIRST_LINE = 1;//决定哪一行先滚动到视野中,0-第一行,1-第二行,2-第三行;
    private final static int INDENT = 3;//每一行和前一行的缩进字符个数;
    private final static String TTF_PATH = "str2gif/text_font.ttf";
    private final static int[][] COLORS = {{74, 200, 171, 100}, {176, 117, 214, 100}, {95, 154, 221, 100},
            {254, 116, 59, 100}, {245, 207, 57, 100}, {136, 185, 111, 100},
            {217, 133, 138, 100}, {230, 93, 165, 100}, {112, 205, 241, 100}};

    private final static Float FONT_SIZE = 50f;
    private final static int IMAGE_WIDTH = 750;
    private final static int IMAGE_HEIGHT = 200;
    private final static float RESIZE = 0.2f;//因图片绘制质量问题,估先绘制大图,再将图缩放至所需大小;

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
            colors.add(new Color(COLORS[v][0], COLORS[v][1], COLORS[v][2], 255));//透明度为100的话透明gif会有显示效果问题,最好将透明度改为255;
        }
        //准备字体
        Font font = loadFont(TTF_PATH, FONT_SIZE);

        ArrayList<BufferedImage> bufferedImages = new ArrayList<>();
        int imageCount = 0;
        while (range > -FONT_SIZE) {
            BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
            graphics.setComposite(ac);
            graphics.fillRect(0, 0, imageWidth, imageHeight);
            ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
            graphics.setComposite(ac);
            //绘制字符串
            for (int i = 0; i < strings.size(); i++) {
                graphics.setColor(colors.get(i));
                graphics.setFont(font);
//                graphics.setStroke(new BasicStroke(2f));
//                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.drawString(strings.get(i), imageWidth + (indentAmounts.get(i) - imageCount) * FONT_SIZE, (imageHeight / strs.length * (i + 1)) - LINE_GAP);
            }
            graphics.dispose();
            ImageIO.write(bufferedImage, "png", new File("image" + (imageCount + 1) + ".png"));
            bufferedImages.add(bufferedImage);
            range = (int) (range - FONT_SIZE);
            imageCount++;
        }
        AnimatedGifEncoder e = new AnimatedGifEncoder();
        e.setTransparent(Color.WHITE);//设置图片上的白色为透明
        e.setRepeat(0);
        e.start("image" + (imageCount + 1) + ".gif");
        for (BufferedImage bufferedImage : bufferedImages) {
            e.setDelay(100);
            e.addFrame(bufferedImage);
        }
        e.finish();

        //压缩下图片看效果
        AnimatedGifEncoder f = new AnimatedGifEncoder();
        f.setTransparent(Color.WHITE);//设置图片上的白色为透明
        f.setRepeat(0);
        f.start("image" + (imageCount + 1) + "E.gif");
        for (BufferedImage bufferedImage : bufferedImages) {
            bufferedImage = zoomImage(bufferedImage, 0.5f);
            f.setDelay(100);
            f.addFrame(bufferedImage);
        }
        f.finish();
    }

    /**
     * 加载字体 Font
     *
     * @param fontFileName 文件路径
     * @param fontSize     文字大小
     * @return
     */
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

    /**
     * 缩放图片
     *
     * @param image  原图
     * @param resize 缩放倍数
     * @return
     */
    public static BufferedImage zoomImage(BufferedImage image, float resize) {
        /* 原始图像的宽度和高度 */
        int width = image.getWidth();
        int height = image.getHeight();

        /* 调整后的图片的宽度和高度 */
        int toWidth = (int) (width * resize);
        int toHeight = (int) (height * resize);

        /* 新生成结果图片 */
        BufferedImage result = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_ARGB);
        result.getGraphics().drawImage(image.getScaledInstance(toWidth, toHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);

        return result;

    }

}
