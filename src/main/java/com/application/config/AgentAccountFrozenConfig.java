package com.application.config;

import java.util.HashMap;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * 代理商账户金额解冻延迟消费队列，无实际消费者，需在producer端配置
 * 代理商收到返现，7天后将冻结金额转换为可用余额
 */
@Configuration
public class AgentAccountFrozenConfig {
	
	/**
	 * 
	 * 超时交换机
	 */
	@Bean
	public DirectExchange agentAccountFrozenTtlExchange() {
		return new DirectExchange("ttl-exchange", true, false);
	}
	
	/**
	 * 
	 * 超时队列
	 */
	@Bean
    public Queue agentAccountFrozenTtlQueue() {
		HashMap<String,Object> args = new HashMap<>();
		//消息超时时间
		args.put("x-message-ttl", 60000);
		//绑定死信交换机
		args.put("x-dead-letter-exchange", "dlx-exchange"); 
		args.put("x-dead-letter-routing-key", "dlx-routing-key");
        return new Queue("ttl-queue",true,false,false,args);
    }
	
	@Bean
    public Binding bindingKey() {
        return BindingBuilder.bind(agentAccountFrozenTtlQueue()).to(agentAccountFrozenTtlExchange()).with("ttl-routing-key");
    }

}
