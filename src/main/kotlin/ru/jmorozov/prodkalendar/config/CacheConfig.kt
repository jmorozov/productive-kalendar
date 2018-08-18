package ru.jmorozov.prodkalendar.config

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.jcache.JCacheCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.URISyntaxException
import javax.cache.Caching


@Configuration
@EnableCaching
class CacheConfig {
    @Bean
    @Throws(URISyntaxException::class)
    fun cacheManager(): CacheManager = JCacheCacheManager(
            Caching.getCachingProvider().getCacheManager(
                javaClass.getResource("/ehcache.xml").toURI(),
                javaClass.classLoader
            )
    )
}