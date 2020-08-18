package configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class ReidsConfig {
	  @Bean
	  public RedisTemplate redisTemplate() {
		  return new RedisTemplate ();
	  }
}
