package com.luohuo.basic.cloud.config;


import com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Logger;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignLoggerFactory;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import com.luohuo.basic.cloud.http.InfoFeignLoggerFactory;
import com.luohuo.basic.cloud.http.RestTemplateHeaderInterceptor;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * RestTemplate 相关的配置
 *
 * @author 乾乾
 */
@ConditionalOnClass(okhttp3.OkHttpClient.class)
@AllArgsConstructor
@Slf4j
@AutoConfiguration(
        after = {org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration.class}
)
public class RestTemplateConfiguration {
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private final ObjectMapper objectMapper;

    @Bean
    @ConditionalOnMissingBean(FeignLoggerFactory.class)
    public FeignLoggerFactory getInfoFeignLoggerFactory() {
        return new InfoFeignLoggerFactory();
    }

    @Bean
    @Profile({"dev", "test"})
    Logger.Level devFeignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    @Profile({"docker", "uat", "prod"})
    Logger.Level prodFeignLoggerLevel() {
        return Logger.Level.BASIC;
    }

//    TODO java17
//    /**
//     * 配置OkHttpClient
//     *
//     * @param httpClientFactory    httpClient 工厂
//     * @param connectionPool       链接池配置
//     * @param httpClientProperties httpClient配置
//     * @return OkHttpClient
//     */
//    @Bean
//    @ConditionalOnMissingBean(okhttp3.OkHttpClient.class)
//    public okhttp3.OkHttpClient okHttp3Client(
//            OkHttpClientFactory httpClientFactory,
//            okhttp3.ConnectionPool connectionPool,
//            FeignClientProperties feignClientProperties,
//            FeignHttpClientProperties httpClientProperties) {
//        FeignClientProperties.FeignClientConfiguration defaultConfig = feignClientProperties.getConfig().get("default");
//        return httpClientFactory.createBuilder(httpClientProperties.isDisableSslValidation())
//                .followRedirects(httpClientProperties.isFollowRedirects())
//                .writeTimeout(defaultConfig.getReadTimeout(), TimeUnit.MILLISECONDS)
//                .readTimeout(defaultConfig.getReadTimeout(), TimeUnit.MILLISECONDS)
//                .connectTimeout(httpClientProperties.getConnectionTimeout(), TimeUnit.MILLISECONDS)
//                .connectionPool(connectionPool)
//                .build();
//    }
//
//    /**
//     * okhttp3 链接池配置
//     *
//     * @param connectionPoolFactory 链接池配置
//     * @param hcp                   httpClient配置
//     * @return okhttp3.ConnectionPool
//     */
//    @Bean
//    @ConditionalOnMissingBean(okhttp3.ConnectionPool.class)
//    public okhttp3.ConnectionPool okHttp3ConnectionPool(FeignHttpClientProperties hcp,
//                                                        OkHttpClientConnectionPoolFactory connectionPoolFactory) {
//        return connectionPoolFactory.create(hcp.getMaxConnections(), hcp.getTimeToLive(), hcp.getTimeToLiveUnit());
//    }

    @Bean
    @ConditionalOnMissingBean
    public okhttp3.OkHttpClient.Builder okHttpClientBuilder() {
        return new okhttp3.OkHttpClient.Builder();
    }

    @Bean
    @ConditionalOnMissingBean(ConnectionPool.class)
    public ConnectionPool httpClientConnectionPool(FeignHttpClientProperties httpClientProperties) {
        int maxTotalConnections = httpClientProperties.getMaxConnections();
        long timeToLive = httpClientProperties.getTimeToLive();
        TimeUnit ttlUnit = httpClientProperties.getTimeToLiveUnit();
        return new ConnectionPool(maxTotalConnections, timeToLive, ttlUnit);
    }

    @Bean
    public okhttp3.OkHttpClient okHttpClient(okhttp3.OkHttpClient.Builder builder, ConnectionPool connectionPool, FeignHttpClientProperties httpClientProperties) {
        boolean followRedirects = httpClientProperties.isFollowRedirects();
        int connectTimeout = httpClientProperties.getConnectionTimeout();
        boolean disableSslValidation = httpClientProperties.isDisableSslValidation();
        Duration readTimeout = httpClientProperties.getOkHttp().getReadTimeout();
        if (disableSslValidation) {
            disableSsl(builder);
        }
        return builder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .followRedirects(followRedirects).readTimeout(readTimeout).connectionPool(connectionPool).build();
    }

    private void disableSsl(okhttp3.OkHttpClient.Builder builder) {
        try {
            X509TrustManager disabledTrustManager = new RestTemplateConfiguration.DisableValidationTrustManager();
            TrustManager[] trustManagers = new TrustManager[1];
            trustManagers[0] = disabledTrustManager;
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());
            SSLSocketFactory disabledSslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(disabledSslSocketFactory, disabledTrustManager);
            builder.hostnameVerifier(new RestTemplateConfiguration.TrustAllHostnames());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.warn("Error setting SSLSocketFactory in OKHttpClient", e);
        }
    }

    /**
     * 解决 RestTemplate 传递Request header
     */
    @Bean
    public RestTemplateHeaderInterceptor requestHeaderInterceptor() {
        return new RestTemplateHeaderInterceptor();
    }

    /**
     * 支持负载均衡的 LbRestTemplate, 传递请求头，一般用于内部 http 调用
     *
     * @param interceptor RestTemplateHeaderInterceptor
     * @return LbRestTemplate
     */
    @Bean("lbRestTemplate")
    @LoadBalanced
    @SentinelRestTemplate
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestTemplate lbRestTemplate(okhttp3.OkHttpClient httpClient, RestTemplateHeaderInterceptor interceptor) {
        RestTemplate lbRestTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory(httpClient));
        lbRestTemplate.setInterceptors(Collections.singletonList(interceptor));
        this.configMessageConverters(lbRestTemplate.getMessageConverters());
        return lbRestTemplate;
    }

//    public RestTemplate lbRestTemplate(RestTemplateBuilder restTemplateBuilder, RestTemplateHeaderInterceptor interceptor) {
//        ResponseErrorHandler responseErrorHandler = (response) -> true;
//        RestTemplate restTemplate = restTemplateBuilder.errorHandler(responseErrorHandler).build();
//        restTemplate.setInterceptors(Collections.singletonList(interceptor));
//        this.configMessageConverters(restTemplate.getMessageConverters());
//        return restTemplate;
//    }

    /**
     * 普通的 RestTemplate，不透传请求头，一般只做外部 http 调用
     *
     * @return RestTemplate
     */
//    @Bean
//    @LoadBalanced
//    @SentinelRestTemplate
//    public RestTemplate getRestTemplate(RestTemplateBuilder restTemplateBuilder) {
//        ResponseErrorHandler responseErrorHandler = (response) -> true;
//        RestTemplate restTemplate = restTemplateBuilder.errorHandler(responseErrorHandler).build();
//        this.configMessageConverters(restTemplate.getMessageConverters());
//        return restTemplate;
//    }
    @Bean
    @SentinelRestTemplate
    public RestTemplate restTemplate(okhttp3.OkHttpClient httpClient) {
        RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory(httpClient));
        this.configMessageConverters(restTemplate.getMessageConverters());
        return restTemplate;
    }

    private void configMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(c -> c instanceof StringHttpMessageConverter || c instanceof MappingJackson2HttpMessageConverter);
        converters.add(new StringHttpMessageConverter(UTF_8));
        converters.add(new MappingJackson2HttpMessageConverter(this.objectMapper));
    }

    /**
     * A {@link X509TrustManager} that does not validate SSL certificates.
     */
    class DisableValidationTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

    }

    class TrustAllHostnames implements HostnameVerifier {

        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }

    }
}
