package com.xaut.server;

import com.xaut.server.single.SupremeMQServerManagerInitSingle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.jms.JMSException;

@ComponentScan
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@EnableScheduling
@Configuration
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class);
        try {
            SupremeMQServerManagerInitSingle.getSupremeMQServerManagerInitSingle()
                    .getSupremeMQServerManager().start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
