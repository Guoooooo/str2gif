package com.guooo.planb;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

/**
 * Created by Guo on 2016/11/25.
 */

public class GifMaker {
    public static void main(String[] args) throws Exception {
        getGif("野比你到底办了么");
    }

    //字体及颜色
    private final static String TTF_PATH = "str2gif/HYHeiLiZhiTiJ.ttf";
    private final static int[][] COLORS = {{74, 200, 171, 100}, {176, 117, 214, 100}, {95, 154, 221, 100},
            {254, 116, 59, 100}, {245, 207, 57, 100}, {136, 185, 111, 100},
            {217, 133, 138, 100}, {230, 93, 165, 100}, {112, 205, 241, 100}};

    //图片大小,字体大小
    private final static int FONT_SIZE = 100;
    private final static int IMAGE_WIDTH = 500;
    private final static int IMAGE_HEIGHT = 140;
    private final static int RANGE = FONT_SIZE / 3;//绘制每一帧的图片的像素间隔,目前设为字体大小的三分之一;
    private final static float RESIZE = 0.3f;//因图片绘制锯齿质量问题,估先绘制大图,再将图缩放至所需大小;
    private final static int DELAY = 80;//每一帧的时间差;

    //字体
    private static Font font;

    static {
        font = loadFont(TTF_PATH, (int) (FONT_SIZE / RESIZE));
    }

    public static void getGif(String str) throws Exception {
        for (int i = 0; i < COLORS.length; i++) {

            int imageWidth = (int) (IMAGE_WIDTH / RESIZE);
            int imageHeight = (int) (IMAGE_HEIGHT / RESIZE);
            int fontSize = (int) (FONT_SIZE / RESIZE);
            int range = (int) (RANGE / RESIZE);
            int distance = imageWidth + fontSize * str.length();//最长一个字最后距离图片最左端距离

            //准备字体和机色
            Color color = new Color(COLORS[i][0], COLORS[i][1], COLORS[i][2], 255);//透明度为100的话透明gif会有显示效果问题,最好将透明度改为255;

            ArrayList<BufferedImage> bufferedImages = new ArrayList<>();
            int imageCount = 0;
            while (distance > 0) {
                BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
                AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f);//透明
                graphics.setComposite(ac);
                graphics.fillRect(0, 0, imageWidth, imageHeight);
                ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);//不透明透明
                graphics.setComposite(ac);
                graphics.setColor(color);
                graphics.setFont(font);
                graphics.drawString(str, imageWidth - range * imageCount, imageHeight / 2 + fontSize / 2);
                graphics.dispose();
                bufferedImages.add(bufferedImage);
                distance = distance - range;
                imageCount++;
            }

            ImageOutputStream output = new FileImageOutputStream(new File("result" + i + "b.gif"));
            GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_4BYTE_ABGR, DELAY, true);

            for (BufferedImage bufferedImage : bufferedImages) {
//                bufferedImage = zoomImage(bufferedImage, RESIZE);
                writer.writeToSequence(bufferedImage);
            }
            writer.close();
            output.close();

        }
    }

    public static Font loadFont(String fontFileName, float fontSize) {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File(fontFileName));
            font = font.deriveFont(fontSize);
            return font;
        } catch (Exception e) {
            e.printStackTrace();
            return new java.awt.Font("宋体", Font.PLAIN, (int) fontSize);
        }
    }

    public static BufferedImage zoomImage(BufferedImage image, float resize) {
        int width = image.getWidth();
        int height = image.getHeight();
        int toWidth = (int) (width * resize);
        int toHeight = (int) (height * resize);
        BufferedImage result = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_4BYTE_ABGR);
        result.getGraphics().drawImage(image.getScaledInstance(toWidth, toHeight, Image.SCALE_SMOOTH), 0, 0, null);
        return result;
    }
}
