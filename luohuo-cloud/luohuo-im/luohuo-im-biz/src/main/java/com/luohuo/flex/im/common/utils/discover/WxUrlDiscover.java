package com.luohuo.flex.im.common.utils.discover;

import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;

/**
 * 针对微信公众号文章的标题获取类
 * @author nyh
 */
public class WxUrlDiscover extends AbstractUrlDiscover {

    @Nullable
    @Override
    public String getTitle(Document document) {
        return document.getElementsByAttributeValue("property", "og:title").attr("content");
    }

    @Nullable
    @Override
    public String getDescription(Document document) {
        return document.getElementsByAttributeValue("property", "og:description").attr("content");
    }

    @Nullable
    @Override
    public String getImage(String url, Document document) {
        String href = document.getElementsByAttributeValue("property", "og:image").attr("content");
        return isConnect(href) ? href : null;
    }
}
