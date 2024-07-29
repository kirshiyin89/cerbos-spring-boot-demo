package org.cerbos.demo.config;

import dev.cerbos.sdk.CerbosBlockingClient;
import dev.cerbos.sdk.CerbosClientBuilder;
import io.grpc.LoadBalancerRegistry;
import io.grpc.NameResolverRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CerbosConfig {


    @Bean
    public CerbosBlockingClient cerbosClient() throws CerbosClientBuilder.InvalidClientConfigurationException {
        LoadBalancerRegistry.getDefaultRegistry().register(new io.grpc.internal.PickFirstLoadBalancerProvider());
        NameResolverRegistry.getDefaultRegistry().register(new io.grpc.internal.DnsNameResolverProvider());
        return new CerbosClientBuilder("localhost:3593").withPlaintext().buildBlockingClient();
    }
}
