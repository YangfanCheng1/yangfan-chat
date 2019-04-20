package com.yangfan.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600)
public class SessionConfig extends AbstractHttpSessionApplicationInitializer {
//    @Bean
//    @Primary
//    public RedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, ExpiringSession> template = new RedisTemplate<String, ExpiringSession>();
//
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setHashKeySerializer(new StringRedisSerializer());
//        template.setHashValueSerializer(new LdapFailAwareRedisObjectSerializer());
//
//        template.setConnectionFactory(connectionFactory);
//        return template;
//    }
}
