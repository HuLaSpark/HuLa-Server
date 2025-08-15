package com.luohuo.flex.im.common.utils.discover;

import cn.hutool.core.util.StrUtil;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nyh
 */
public class CommonUrlDiscover extends AbstractUrlDiscover {
    @Nullable
    @Override
    public String getTitle(Document document) {
        return document.title();
    }

    @Nullable
    @Override
	public String getDescription(Document document) {
		// 获取元数据
		String description = document.head().select("meta[name=description]").attr("content");
		String keywords = document.head().select("meta[name=keywords]").attr("content");

		// 优先使用description，其次keywords
		String content = StrUtil.isNotBlank(description) ? description : keywords;
		if (StrUtil.isBlank(content)) {
			return null;
		}

		// 使用正则表达式匹配第一个句子终止符（支持中文。！？和英文.!?）
		Pattern pattern = Pattern.compile("[。！？.!?]");
		Matcher matcher = pattern.matcher(content);

		if (matcher.find()) {
			int endIndex = matcher.start();
			return content.substring(0, endIndex);
		}

		// 没有终止符时做长度截断（保留前120字符）
		int maxLength = Math.min(content.length(), 120);
		return content.substring(0, maxLength) + (content.length() > 120 ? "..." : "");
	}

    @Nullable
    @Override
    public String getImage(String url, Document document) {
        String image = document.select("link[type=image/x-icon]").attr("href");
        //如果没有去匹配含有icon属性的logo
        String href = StrUtil.isEmpty(image) ? document.select("link[rel$=icon]").attr("href") : image;
        //如果url已经包含了logo
        if (StrUtil.containsAny(url, "favicon")) {
            return url;
        }
        //如果icon可以直接访问或者包含了http
        if (isConnect(!StrUtil.startWith(href, "http") ? "http:" + href : href)) {
            return href;
        }

        return StrUtil.format("{}/{}", url, StrUtil.removePrefix(href, "/"));
    }


}
