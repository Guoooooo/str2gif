package com.guooo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Str2Gif {
    public static void main(String[] args) throws IOException {
        getGif("你咋不上天?!");
    }

    private final static boolean DEBUG = true;

    //字体及颜色
    private final static String TTF_PATH = "str2gif/HYHeiLiZhiTiJ.ttf";
    private final static int[][] COLORS = {{74, 200, 171, 100}, {176, 117, 214, 100}, {95, 154, 221, 100},
            {254, 116, 59, 100}, {245, 207, 57, 100}, {136, 185, 111, 100},
            {217, 133, 138, 100}, {230, 93, 165, 100}, {112, 205, 241, 100}};

    //图片大小,字体大小
    private final static int FONT_SIZE = 150;
    private final static int IMAGE_WIDTH = 750;
    private final static int IMAGE_HEIGHT = 200;
    private final static int RANGE = FONT_SIZE / 3;//绘制每一帧的图片的像素间隔,目前设为字体大小的三分之一;
    private final static float RESIZE = 1f;//因图片绘制锯齿质量问题,估先绘制大图,再将图缩放至所需大小;
    private final static int DELAY = 100;//每一帧的时间差;

    public static byte[] getGif(String str) {
        if (str == null || str.equals(""))
            return null;
        str = str.trim();

        int imageWidth = (int) (IMAGE_WIDTH / RESIZE);
        int imageHeight = (int) (IMAGE_HEIGHT / RESIZE);
        int fontSize = (int) (FONT_SIZE / RESIZE);
        int range = (int) (RANGE / RESIZE);
        int distance = imageWidth + fontSize * str.length();//最长一个字最后距离图片最左端距离

        //准备字体和随机色
        Font font = loadFont(TTF_PATH, fontSize);
        int random = (int) (Math.random() * (COLORS.length - 1));
        Color color = new Color(COLORS[random][0], COLORS[random][1], COLORS[random][2], 255);//透明度为100的话透明gif会有显示效果问题,最好将透明度改为255;

        ArrayList<BufferedImage> bufferedImages = new ArrayList<>();
        int imageCount = 0;
        while (distance > 0) {
            BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);//北京不透明 为白色
            graphics.setComposite(ac);
            graphics.fillRect(0, 0, imageWidth, imageHeight);
            graphics.setColor(color);
            graphics.setFont(font);
            //抗锯齿,但有时候合成gif会出现问题,个别图片背景变不了透明,RESIZE基本解决效果问题
//            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.drawString(str, imageWidth - range * imageCount, imageHeight / 2 + fontSize / 2);
            graphics.dispose();
            bufferedImages.add(bufferedImage);
            distance = distance - range;
            imageCount++;

            if (DEBUG)//本地生成图片看效果
                try {
                    ImageIO.write(bufferedImage, "png", new File("image" + imageCount + ".png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.setTransparent(Color.WHITE);//将白色背景设置为透明
        encoder.setRepeat(0);//图片无限循环
        encoder.startForBytes();
        for (BufferedImage bufferedImage : bufferedImages) {
            bufferedImage = zoomImage(bufferedImage, RESIZE);
            encoder.setDelay(DELAY);
            encoder.addFrame(bufferedImage);
        }
        byte[] bytes = encoder.finishForBytes();

        //本地生成图片看效果
        if (DEBUG) {
            File file = new File("result.gif");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                fos.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bytes;
    }

    /**
     * 加载字体 Font
     *
     * @param fontFileName 文件路径
     * @param fontSize     文字大小
     * @return 若文件错误返回默认字体
     */
    public static Font loadFont(String fontFileName, int fontSize) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontFileName));
            font = font.deriveFont((float) fontSize);
            return font;
        } catch (Exception e) {
            e.printStackTrace();
            return new java.awt.Font("宋体", Font.PLAIN, fontSize);
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
        int width = image.getWidth();
        int height = image.getHeight();
        int toWidth = (int) (width * resize);
        int toHeight = (int) (height * resize);
        BufferedImage result = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_ARGB);
        result.getGraphics().drawImage(image.getScaledInstance(toWidth, toHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
        return result;
    }

}




