package cn.luliangwei.rabbitmq.demo.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



/**
 * 
 * RabbitMQ相关的配置.
 * </p>
 * <pre>
 * 此配置类中可以配置:
 *      1、队列queue
 *      2、交换机exchange
 *      3、队列与交换机的绑定关系
 *      4、rabbitmq连接信息（也可以在配置文件中完成配置，然后注入即可）
 *      5、MessageConverter
 * </pre>
 * @author luliangwei
 * @since 0.0.1
 */
@Configuration
@EnableRabbit
public class RabbitConfig {

    public static final Logger LOG = LoggerFactory.getLogger(RabbitConfig.class);
    
    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    private MessageConverter jsonMessageConverter;
    
  //-----------------------------------公共配置----------------------------------------------    
    /**
     * 配置rabbit连接信息,如果你使用的是spring boot提供的starter,那么可以直接在配置文件</br>
     * <code> application.yml </code>中信息配置。如果你本地安装了rabbitmq服务，那么</br>
     * 即时你不在配置文件中配置连接信息，spring boot starter也会为我们自动将连接信息配置为</br>
     * 本地连接 <i>127.0.0.1:5672</i>。如果不是使用的<i>spring-boot-starter-amqp</i>,</br>
     * 那么需要按照此方法来设置连接信息，然后使用<code>@Bean</code>标注即可
     *
     * @return  连接信息对象
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory conn = new CachingConnectionFactory("10.10.9.153", 5672);
        conn.setUsername("test");
        conn.setPassword("123456a");
        return conn;
    }
    
    /**
     * 
     * 对注解@@Listener的支持.
     *
     * @return  
     */
    @Bean(name = "rabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory listenerContainerFactory(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        //json消息转换器,如果不设置会报错： org.springframework.amqp.AmqpException: No method found for class [B
        factory.setMessageConverter(jsonMessageConverter);
        //对消息消费后进行手动确认
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }
    /*
     * json消息转换器
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    /**
     * 
     * 设置rabbitmq队列管理员，默认使用连接信息connectionFactory中的用户作为管理员.</br>
     * 如果需要设置其他用户，可以在这里设置，方便声明队列。如果使用默认的，则忽略这个bean
     *
     * @return
     */
    @Bean
    public AmqpAdmin amqpAdmin(){
        return new RabbitAdmin(connectionFactory);
    }
    
    //-----------------------------------第一个队列配置信息----------------------------------------------    
    /**
     * 定义一个exchange并纳入spring容器管理
     * 类型为：DirectExchange</br>
     * 名称为：test-direct-exchange</br>
     * 是否持久化：true</br>
     * 是否自动删除：false</br>
     *
     * @return  Direct类型的交换机对象
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("test-direct-exchange1", true, false);
    }
    
    /**
     * 定义一个队列并纳入spring容器管理</br>
     * 队列名称：test-message-queue</br>
     * 是否持久化：true
     *
     * @return  一个queue实例
     */
    @Bean
    public Queue messageQueue() {
        return new Queue("test-message-queue1", true);
    }
    
    /**
     * 
     * 建立队列和exchange的绑定关系.</br>
     * 将队列test-message-queue与交换机test-direct-exchange绑定在一起</br>
     * 使用的路由key：队列名称，即test-message-queue。可以自定义其他的key
     * 
     *
     * @return
     */
    @Bean
    public Binding bind() {
        return BindingBuilder.bind(messageQueue()).to(directExchange()).with("test-message-queue1");
    }
    
    /**
     * 
     * 为交换机定义消息发送的模板.</br>
     * 交换机test-direct-exchange1对应的消息发送模板</br>
     * 路由key与绑定队列对应的key一致</br>
     * </p>
     * 也可以不配置此模板，直接在需要发送消息的时候注入AmqpTemplate，也可以发送消息
     *
     * @return
     */
    @Bean
    public RabbitTemplate queueOneTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setExchange("test-direct-exchange1");
        template.setRoutingKey("test-message-queue1");
        //json消息转换器,如果不设置会报错： org.springframework.amqp.AmqpException: No method found for class [B
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
    
  //-----------------------------------第二个队列配置信息----------------------------------------------    
    @Bean
    public Queue messageQueue2() {
        return new Queue("test-message-queue2", true);
    }
    
    @Bean
    public Binding bind2() {
        return BindingBuilder.bind(messageQueue2()).to(directExchange()).with("test-message-queue2");
    }
    
    @Bean
    public RabbitTemplate queueTwoTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setExchange("test-direct-exchange1");
        template.setRoutingKey("test-message-queue2");
        //json消息转换器,如果不设置会报错： org.springframework.amqp.AmqpException: No method found for class [B
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
    
  //-----------------------------------自定义部分，如果不需要，可以不用,暂时注释掉----------------------------------------------    
    /*
     * 解释:
     *     这部分主要是为了方便使用RabbitTemplate，同时也为了解决当多个队列存在的时候
     *     需要注入多个RabbitTemplate而引起的bean冲突
     */
    
    @Bean
    public QueueTemplate queueTemplate() {
        QueueTemplate template = new QueueTemplate();
        template.addQueueTemplate("test-message-queue1", queueOneTemplate());
        template.addQueueTemplate("test-message-queue2", queueTwoTemplate());
        return template;
    }
    
    /*
     * 自定义队列模板
     * 
     * 为了解决当注入多个RabbitTemplate时会出现bean冲突的情况
     * 同时也为了方便使用RabbitTemplate模板
     */
    public static class QueueTemplate {
        /*
         * 存放不同队列的RabbitTemplate
         */
        private Map<String,RabbitTemplate> queueTemplates = new HashMap<String,RabbitTemplate>();

        public QueueTemplate addQueueTemplate(String queueName,RabbitTemplate rabbitTemplate){
            if(queueName == null || rabbitTemplate == null){
                return this;
            }
            this.queueTemplates.put(queueName, rabbitTemplate);
            return this;
        }
        
        public RabbitTemplate getTemplate(String queueName){
            return this.queueTemplates.get(queueName);
        }

        public Map<String, RabbitTemplate> getQueueTemplates() {
            return queueTemplates;
        }
        
        public RabbitTemplate getQueueOneTemplate(){
            return this.queueTemplates.get("test-message-queue1");
        }
        
        public RabbitTemplate getQueueTwoTemplate(){
            return this.queueTemplates.get("test-message-queue2");
        }

        public void setQueueTemplates(Map<String, RabbitTemplate> queueTemplates) {
            this.queueTemplates = queueTemplates;
        }
        
    }
}
