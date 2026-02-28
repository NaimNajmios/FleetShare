package com.najmi.fleetshare.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration class to enable asynchronous background processing.
 * This is crucial for tasks like sending emails without blocking the main
 * thread.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
