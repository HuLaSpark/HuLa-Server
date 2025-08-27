package com.luohuo.flex.im.common.utils.discover;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.luohuo.flex.im.domain.UrlInfo;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.util.Pair;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author nyh
 */
@Slf4j
public abstract class AbstractUrlDiscover implements UrlDiscover {
    //链接识别的正则
	private static final Pattern PATTERN = Pattern.compile(
			"((https?:\\/\\/|www\\.)[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()!@:%_\\+.~#?&\\/\\/=]*))", Pattern.CASE_INSENSITIVE
	);

	private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(
			Math.max(4, Runtime.getRuntime().availableProcessors() * 2),
			new ThreadFactory() {
				private final AtomicInteger counter = new AtomicInteger();
				@Override
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r, "UrlDiscover-Thread-" + counter.incrementAndGet());
					t.setContextClassLoader(AbstractUrlDiscover.class.getClassLoader());
					return t;
				}
			}
	);

	@Nullable
	@Override
	public Map<String, UrlInfo> getUrlContentMap(String content) {
		if (StrUtil.isBlank(content)) return Collections.emptyMap();

		List<String> matchList = ReUtil.findAll(PATTERN, content, 0);
		CompletionService<Pair<String, UrlInfo>> cs = new ExecutorCompletionService<>(EXECUTOR);
		List<Future<Pair<String, UrlInfo>>> futures = matchList.stream()
				.map(match -> cs.submit(() -> {
					UrlInfo info = getContent(match);
					return info != null ? Pair.of(match, info) : null;
				}))
				.collect(Collectors.toList());

		Map<String, UrlInfo> resultMap = new ConcurrentHashMap<>();
		int completed = 0;
		long timeout = 5; // 秒
		long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(timeout);

		while (completed < matchList.size()) {
			try {
				Future<Pair<String, UrlInfo>> future = cs.poll(
						deadline - System.nanoTime(), TimeUnit.NANOSECONDS
				);

				if (future != null) {
					Pair<String, UrlInfo> pair = future.get();
					if (pair != null) resultMap.put(pair.getFirst(), pair.getSecond());
					completed++;
				} else if (System.nanoTime() >= deadline) {
					log.warn("Timeout: {}/{} URLs processed", completed, matchList.size());
					break;
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				futures.forEach(f -> f.cancel(true)); // 取消所有任务
				break;
			} catch (ExecutionException e) {
				log.error("Processing error: {}", e.getCause().getMessage());
				completed++;
			}
		}
		return resultMap;
	}

    @Nullable
    @Override
    public UrlInfo getContent(String url) {
        Document document = getUrlDocument(assemble(url));
        if (Objects.isNull(document)) {
            return null;
        }

        return UrlInfo.builder()
                .title(getTitle(document))
                .description(getDescription(document))
                .image(getImage(assemble(url), document)).build();
    }


    private String assemble(String url) {
        if (!StrUtil.startWith(url, "http")) {
            return "http://" + url;
        }

        return url;
    }

    protected Document getUrlDocument(String matchUrl) {
        try {
            Connection connect = Jsoup.connect(matchUrl);
            connect.timeout(2000);
            return connect.get();
        } catch (Exception e) {
            log.error("find error:url:{}", matchUrl, e);
        }
        return null;
    }

    /**
     * 判断链接是否有效
     * 输入链接
     * 返回true或者false
     */
    public static boolean isConnect(String href) {
        //请求地址
        URL url;
        //请求状态码
        int state;
        //下载链接类型
        String fileType;
        try {
            url = new URL(href);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            state = httpURLConnection.getResponseCode();
            fileType = httpURLConnection.getHeaderField("Content-Disposition");
            //如果成功200，缓存304，移动302都算有效链接，并且不是下载链接
            if ((state == 200 || state == 302 || state == 304) && fileType == null) {
                return true;
            }
            httpURLConnection.disconnect();
        } catch (Exception e) {
            return false;
        }
        return false;
    }

	@PreDestroy
	public void shutdown() {
		EXECUTOR.shutdown();
		try {
			if (!EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
				EXECUTOR.shutdownNow();
			}
		} catch (InterruptedException e) {
			EXECUTOR.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
}
