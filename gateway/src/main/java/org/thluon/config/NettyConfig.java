package org.thluon.config;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.resources.LoopResources;

@Configuration
public class NettyConfig {
    // Customize Netty To have only on thread (Simulating harsh condition)
    @Bean
    public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() {
        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();
        LoopResources loopResources = LoopResources.create("custom-loop", 1, true);
        factory.addServerCustomizers(httpServer -> httpServer.runOn(loopResources));
        return factory;
    }
}
