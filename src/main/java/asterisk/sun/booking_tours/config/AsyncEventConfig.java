package asterisk.sun.booking_tours.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executor;

/**
 * Configuration for Async Event Processing
 *
 * Enable @Async annotation để Domain Event Listeners
 * có thể chạy bất đồng bộ (không chặn main transaction)
 *
 * Ví dụ:
 * - Send email: chậm (1-2s) → nên async
 * - Clear cache: nhanh (< 100ms) → có thể sync
 * - Update search index: chậm (500ms) → nên async
 */
@Configuration
@EnableAsync
public class AsyncEventConfig {

    /**
     * Thread Pool cho async event processing
     *
     * Cấu hình:
     * - Core pool size: 5 threads (luôn sẵn sàng)
     * - Max pool size: 10 threads (tối đa khi busy)
     * - Queue capacity: 25 tasks (hàng đợi)
     */
    @Bean(name = "asyncEventExecutor")
    public Executor asyncEventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Core threads: Số threads luôn sẵn sàng
        executor.setCorePoolSize(5);

        // Max threads: Số threads tối đa khi có nhiều tasks
        executor.setMaxPoolSize(10);

        // Queue capacity: Số tasks có thể chờ trong queue
        executor.setQueueCapacity(25);

        // Thread name prefix (dễ debug)
        executor.setThreadNamePrefix("async-event-");

        // Wait for tasks to complete on shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();
        return executor;
    }
}
