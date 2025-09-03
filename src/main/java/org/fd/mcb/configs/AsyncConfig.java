package org.fd.mcb.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@EnableAsync
@Configuration
@RequiredArgsConstructor
public class AsyncConfig {

    @Bean
    public TaskExecutor processExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("process-executor-");
        executor.initialize();

        return executor;
    }

    @Bean
    public TaskExecutor virtualThreadExecutor() {
        ThreadFactory virtualThreadFactory = Thread.ofVirtual().name("virtual-executor-", 0).factory();
        return new TaskExecutorAdapter(Executors.newThreadPerTaskExecutor(virtualThreadFactory));
    }

}