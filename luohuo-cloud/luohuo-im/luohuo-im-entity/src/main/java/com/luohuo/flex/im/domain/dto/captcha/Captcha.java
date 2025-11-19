package com.luohuo.flex.im.domain.dto.captcha;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.QuadCurve2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

/**
 * 验证码抽象类
 *
 * Created by 乾乾 on 2024-07-27 上午 10:08.
 */
public abstract class Captcha extends Randoms {
    // 常用颜色
    public static final int[][] COLOR = {{0, 135, 255}, {51, 153, 51}, {255, 102, 102}, {255, 153, 0}, {153, 102, 0}, {153, 102, 153}, {51, 153, 153}, {102, 102, 255}, {0, 102, 204}, {204, 51, 51}, {0, 153, 204}, {0, 51, 102}};
    // 验证码文本类型
    public static final int TYPE_DEFAULT = 1;  // 字母数字混合
    public static final int TYPE_ONLY_NUMBER = 2;  // 纯数字
    public static final int TYPE_ONLY_CHAR = 3;  // 纯字母
    public static final int TYPE_ONLY_UPPER = 4;  // 纯大写字母
    public static final int TYPE_ONLY_LOWER = 5;  // 纯小写字母
    public static final int TYPE_NUM_AND_UPPER = 6;  // 数字大写字母
    // 内置字体
    public static final int FONT_1 = 0;
    public static final int FONT_2 = 1;
    public static final int FONT_3 = 2;
    public static final int FONT_4 = 3;
    public static final int FONT_5 = 4;
    public static final int FONT_6 = 5;
    public static final int FONT_7 = 6;
    public static final int FONT_8 = 7;
    public static final int FONT_9 = 8;
    public static final int FONT_10 = 9;
    private static final String[] FONT_NAMES = new String[]{"actionj.ttf", "epilog.ttf", "fresnel.ttf", "headache.ttf", "lexo.ttf", "prefix.ttf", "progbot.ttf", "ransom.ttf", "robot.ttf", "scandal.ttf"};
    protected int len = 5; // 验证码随机字符长度
    protected int width = 130; // 验证码显示宽度
    protected int height = 48; // 验证码显示高度
    protected int charType = TYPE_DEFAULT;  // 验证码类型
    protected String chars = null; // 当前验证码
    private Font font = null; // 验证码的字体

    /**
     * 生成随机验证码
     *
     * @return 验证码字符数组
     */
    protected char[] alphas() {
        char[] cs = new char[len];
        for (int i = 0; i < len; i++) {
            switch (charType) {
                case 2 -> cs[i] = alpha(numMaxIndex);
                case 3 -> cs[i] = alpha(charMinIndex, charMaxIndex);
                case 4 -> cs[i] = alpha(upperMinIndex, upperMaxIndex);
                case 5 -> cs[i] = alpha(lowerMinIndex, lowerMaxIndex);
                case 6 -> cs[i] = alpha(upperMaxIndex);
                default -> cs[i] = alpha();
            }
        }
        chars = new String(cs);
        return cs;
    }

    /**
     * 给定范围获得随机颜色
     *
     * @param fc 0-255
     * @param bc 0-255
     * @return 随机颜色
     */
    protected Color color(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + num(bc - fc);
        int g = fc + num(bc - fc);
        int b = fc + num(bc - fc);
        return new Color(r, g, b);
    }

    /**
     * 获取随机常用颜色
     *
     * @return 随机颜色
     */
    protected Color color() {
        int[] color = COLOR[num(COLOR.length)];
        return new Color(color[0], color[1], color[2]);
    }

    /**
     * 验证码输出,抽象方法，由子类实现
     *
     * @param os 输出流
     * @return 是否成功
     */
    public abstract boolean out(OutputStream os);

    /**
     * 输出base64编码
     *
     * @return base64编码字符串
     */
    public abstract String toBase64();


    /**
     * 获取图片类型
     *
     * @return 图片格式，MIME类型
     */
    public abstract String getContentType();

    /**
     * 输出base64编码
     *
     * @param type 编码头
     * @return base64编码字符串
     */
    public String toBase64(String type) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        out(outputStream);
        return type + Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    /**
     * 获取当前的验证码
     *
     * @return 字符串
     */
    public String text() {
        checkAlpha();
        return chars;
    }

    /**
     * 获取当前验证码的字符数组
     *
     * @return 字符数组
     */
    public char[] textChar() {
        checkAlpha();
        return chars.toCharArray();
    }

    /**
     * 检查验证码是否生成，没有则立即生成
     */
    public void checkAlpha() {
        if (chars == null) {
            alphas(); // 生成验证码
        }
    }

    /**
     * 随机画干扰线
     *
     * @param num 数量
     * @param g   Graphics2D
     */
    public void drawLine(int num, Graphics2D g) {
        drawLine(num, null, g);
    }

    /**
     * 随机画干扰线
     *
     * @param num   数量
     * @param color 颜色
     * @param g     Graphics2D
     */
    public void drawLine(int num, Color color, Graphics2D g) {
        for (int i = 0; i < num; i++) {
            g.setColor(color == null ? color() : color);
            int x1 = num(-10, width - 10);
            int y1 = num(5, height - 5);
            int x2 = num(10, width + 10);
            int y2 = num(2, height - 2);
            g.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * 随机画干扰圆
     *
     * @param num 数量
     * @param g   Graphics2D
     */
    public void drawOval(int num, Graphics2D g) {
        drawOval(num, null, g);
    }

    /**
     * 随机画干扰圆
     *
     * @param num   数量
     * @param color 颜色
     * @param g     Graphics2D
     */
    public void drawOval(int num, Color color, Graphics2D g) {
        for (int i = 0; i < num; i++) {
            g.setColor(color == null ? color() : color);
            int w = 5 + num(10);
            g.drawOval(num(width - 25), num(height - 15), w, w);
        }
    }

    /**
     * 随机画贝塞尔曲线
     *
     * @param num 数量
     * @param g   Graphics2D
     */
    public void drawBesselLine(int num, Graphics2D g) {
        drawBesselLine(num, null, g);
    }

    /**
     * 随机画贝塞尔曲线
     *
     * @param num   数量
     * @param color 颜色
     * @param g     Graphics2D
     */
    public void drawBesselLine(int num, Color color, Graphics2D g) {
        for (int i = 0; i < num; i++) {
            g.setColor(color == null ? color() : color);
            int x1 = 5, y1 = num(5, height / 2);
            int x2 = width - 5, y2 = num(height / 2, height - 5);
            int ctrlx = num(width / 4, width / 4 * 3), ctrly = num(5, height - 5);
            if (num(2) == 0) {
                int ty = y1;
                y1 = y2;
                y2 = ty;
            }
            if (num(2) == 0) {  // 二阶贝塞尔曲线
                QuadCurve2D shape = new QuadCurve2D.Double();
                shape.setCurve(x1, y1, ctrlx, ctrly, x2, y2);
                g.draw(shape);
            } else {  // 三阶贝塞尔曲线
                int ctrlx1 = num(width / 4, width / 4 * 3), ctrly1 = num(5, height - 5);
                CubicCurve2D shape = new CubicCurve2D.Double(x1, y1, ctrlx, ctrly, ctrlx1, ctrly1, x2, y2);
                g.draw(shape);
            }
        }
    }

    public Font getFont() {
        if (font == null) {
            try {
                setFont(FONT_1);
            } catch (Exception e) {
                setFont(new Font("Arial", Font.BOLD, 32));
            }
        }
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setFont(int font) throws IOException, FontFormatException {
        setFont(font, 32f);
    }

    public void setFont(int font, float size) throws IOException, FontFormatException {
        setFont(font, Font.BOLD, size);
    }

    public void setFont(int font, int style, float size) throws IOException, FontFormatException {
        this.font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/" + FONT_NAMES[font])).deriveFont(style, size);
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getCharType() {
        return charType;
    }

    public void setCharType(int charType) {
        this.charType = charType;
    }
}
