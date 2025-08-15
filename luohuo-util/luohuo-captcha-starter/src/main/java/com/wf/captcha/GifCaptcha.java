package com.wf.captcha;

import com.wf.captcha.base.Captcha;
import com.wf.captcha.utils.GifEncoder;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Gif验证码类
 * Created by 王帆 on 2018-07-27 上午 10:08.
 */
public class GifCaptcha extends Captcha {

    public GifCaptcha() {
    }

    public GifCaptcha(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public GifCaptcha(int width, int height, int len) {
        this(width, height);
        setLen(len);
    }

    public GifCaptcha(int width, int height, int len, Font font) {
        this(width, height, len);
        setFont(font);
    }

    @Override
    public boolean out(OutputStream os) {
        try {
            char[] strs = textChar();  // 获取验证码数组
            // 随机生成每个文字的颜色
            Color[] fontColor = new Color[len];
            for (int i = 0; i < len; i++) {
                fontColor[i] = color();
            }
            // 随机生成贝塞尔曲线参数
            int x1 = 5, y1 = num(5, height / 2);
            int x2 = width - 5, y2 = num(height / 2, height - 5);
            int ctrlx = num(width / 4, width / 4 * 3), ctrly = num(5, height - 5);
            if (num(2) == 0) {
                int ty = y1;
                y1 = y2;
                y2 = ty;
            }
            int ctrlx1 = num(width / 4, width / 4 * 3), ctrly1 = num(5, height - 5);
            int[][] besselXY = new int[][]{{x1, y1}, {ctrlx, ctrly}, {ctrlx1, ctrly1}, {x2, y2}};
            // 开始画gif每一帧
            GifEncoder gifEncoder = new GifEncoder();
            gifEncoder.setQuality(180);
            gifEncoder.setDelay(100);
            gifEncoder.setRepeat(0);
            gifEncoder.start(os);
            for (int i = 0; i < len; i++) {
                BufferedImage frame = graphicsImage(fontColor, strs, i, besselXY);
                gifEncoder.addFrame(frame);
                frame.flush();
            }
            gifEncoder.finish();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public String toBase64() {
        return toBase64("data:image/gif;base64,");
    }

    @Override
    public String getContentType() {
        return "image/gif";
    }


    /**
     * 画随机码图
     *
     * @param fontColor 随机字体颜色
     * @param strs      字符数组
     * @param flag      透明度
     * @param besselXY  干扰线参数
     * @return BufferedImage
     */
    private BufferedImage graphicsImage(Color[] fontColor, char[] strs, int flag, int[][] besselXY) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        // 填充背景颜色
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        // 抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 画干扰圆圈
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f * num(10)));  // 设置透明度
        drawOval(2, g2d);
        // 画干扰线
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));  // 设置透明度
        g2d.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        g2d.setColor(fontColor[0]);
        CubicCurve2D shape = new CubicCurve2D.Double(besselXY[0][0], besselXY[0][1], besselXY[1][0], besselXY[1][1], besselXY[2][0], besselXY[2][1], besselXY[3][0], besselXY[3][1]);
        g2d.draw(shape);
        // 画验证码
        g2d.setFont(getFont());
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int fW = width / strs.length;  // 每一个字符所占的宽度
        int fSp = (fW - (int) fontMetrics.getStringBounds("W", g2d).getWidth()) / 2;  // 字符的左右边距
        for (int i = 0; i < strs.length; i++) {
            // 设置透明度
            AlphaComposite ac3 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha(flag, i));
            g2d.setComposite(ac3);
            g2d.setColor(fontColor[i]);
            int fY = height - ((height - (int) fontMetrics.getStringBounds(String.valueOf(strs[i]), g2d).getHeight()) >> 1);  // 文字的纵坐标
            g2d.drawString(String.valueOf(strs[i]), i * fW + fSp + 3, fY - 3);
        }
        g2d.dispose();
        return image;
    }

    /**
     * 获取透明度,从0到1,自动计算步长
     *
     * @param i
     * @param j
     * @return 透明度
     */
    private float getAlpha(int i, int j) {
        int num = i + j;
        float r = (float) 1 / (len - 1);
        float s = len * r;
        return num >= len ? (num * r - s) : num * r;
    }

}
