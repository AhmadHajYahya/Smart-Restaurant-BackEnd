package configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.smartRestaurant.boundaries.NotificationBoundary;

import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

@Configuration
public class NotificationsConfig {

    @Bean
    public Sinks.Many<NotificationBoundary> alertSink() {
    	return Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
    }
}
