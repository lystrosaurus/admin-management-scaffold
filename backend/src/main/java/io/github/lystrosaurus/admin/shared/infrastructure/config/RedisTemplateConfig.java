package io.github.lystrosaurus.admin.shared.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.lystrosaurus.admin.auth.domain.model.StateData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * RedisTemplate 配置。
 *
 * <p>SEC-002 将 OAuth state 改为 Redis 存储后，{@code RedisOAuthStateAdapter} 需要类型安全的
 * {@code RedisTemplate<String, StateData>}；{@code AuthApplicationService} / {@code TokenVersionInterceptor}
 * 需要 {@code RedisTemplate<String, Object>}。Spring Boot 自动配置仅提供
 * {@code RedisTemplate<Object, Object>}，泛型不匹配，故在此显式声明两个类型安全的模板。
 *
 * <p>两个 bean 均依赖 {@link RedisConnectionFactory}（由 Redisson / Spring Data Redis 自动配置提供），
 * 以方法参数形式注入，在 bean 实例化时由 Spring 解析，故无需额外的 {@code @ConditionalOnBean}。
 */
@Configuration
public class RedisTemplateConfig {

  @Bean
  public RedisTemplate<String, StateData> stateDataRedisTemplate(
      RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, StateData> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    StringRedisSerializer keySerializer = new StringRedisSerializer();
    template.setKeySerializer(keySerializer);
    template.setHashKeySerializer(keySerializer);
    // StateData.createdAt 为 LocalDateTime，默认 ObjectMapper 无法序列化 Java8 时间类型，
    // 需注册 JavaTimeModule（由 jackson-datatype-jsr310 提供）。
    ObjectMapper stateMapper = new ObjectMapper();
    stateMapper.registerModule(new JavaTimeModule());
    Jackson2JsonRedisSerializer<StateData> valueSerializer =
        new Jackson2JsonRedisSerializer<>(stateMapper, StateData.class);
    template.setValueSerializer(valueSerializer);
    template.setHashValueSerializer(valueSerializer);
    template.afterPropertiesSet();
    return template;
  }

  @Bean
  public RedisTemplate<String, Object> objectRedisTemplate(
      RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    StringRedisSerializer keySerializer = new StringRedisSerializer();
    template.setKeySerializer(keySerializer);
    template.setHashKeySerializer(keySerializer);
    GenericJackson2JsonRedisSerializer valueSerializer =
        new GenericJackson2JsonRedisSerializer();
    template.setValueSerializer(valueSerializer);
    template.setHashValueSerializer(valueSerializer);
    template.afterPropertiesSet();
    return template;
  }
}
