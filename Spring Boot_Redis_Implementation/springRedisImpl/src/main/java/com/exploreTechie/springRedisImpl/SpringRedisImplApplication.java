package com.exploreTechie.springRedisImpl;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.persistence.Id;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;


@SpringBootApplication
public class SpringRedisImplApplication {

	/*
	 * public static class Cat {}
	 * 
	 * @Bean
	 * 
	 * @ConditionalOnMissingBean(name = "redisTemplate") public
	 * RedisTemplate<String, Cat> redisTemplate( RedisConnectionFactory
	 * redisConnectionFactory){ RedisTemplate< String, Cat> redisTemplate = new
	 * RedisTemplate<>();
	 * 
	 * redisTemplate.setConnectionFactory(redisConnectionFactory);
	 * 
	 * RedisSerializer<Cat> values = new Jackson2JsonRedisSerializer<>(Cat.class);
	 * RedisSerializer keys = new StringRedisSerializer();
	 * redisTemplate.setKeySerializer(keys);
	 * redisTemplate.setValueSerializer(values);
	 * redisTemplate.setHashKeySerializer(keys);
	 * redisTemplate.setHashValueSerializer(values);
	 * 
	 * return redisTemplate; }
	 */

	private ApplicationRunner titledRunner(String title, ApplicationRunner ar) {
		return args -> {
			Log.info(title.toUpperCase() + ":");
		};
	}

	@Bean
	ApplicationRunner geography(RedisTemplate<String, String> rt) {
		return titledRunner("geography", args -> {

			GeoOperations<String, String> geoOps = rt.opsForGeo();
			geoOps.add("India", new Point(13.361389, 38.11555643), "cal");
			geoOps.add("India", new Point(15.08729, 37.502669), "cal1");
			geoOps.add("India", new Point(13.583333, 37.31667), "cal2");

			Circle circle = new Circle(new Point(13.361389, 38.11555643), new Distance(100,
					org.springframework.data.redis.connection.RedisGeoCommands.DistanceUnit.KILOMETERS));

			GeoResults<GeoLocation<String>> geoResults = geoOps.radius("cal", circle);
			geoResults.getContent().forEach(s -> Log.info(s.toString()));

		});
	}

	@Bean
	ApplicationRunner repositories(OrderRepository orderRepository, LineItemRepository lineItemRepository) {
		return titledRunner("repositories", args -> {

			long orderId = generateId();

			List<LineItem> itemList = Arrays.asList(new LineItem(orderId, generateId(), "book"),
					new LineItem(orderId, generateId(), "cup"), new LineItem(orderId, generateId(), "watch"));

			/*
			 * itemList .stream() .map(LineItemRepository::save) .forEach(li ->
			 * Log.info(li.toString()));
			 */
			itemList.stream().map(lineItemRepository::save).forEach(li -> Log.info(li.toString()));

			Order order = new Order(orderId, new Date(), itemList);

			orderRepository.save(order);

			Collection<Order> found = orderRepository.findByWhen(order.getWhen());
			found.forEach(fnd -> Log.info("order found : " + fnd.toString()));

		});
	}

	private final String TOPIC = "chat";

	@Bean
	ApplicationRunner pubSub(RedisTemplate<String, String> rt) {
		return titledRunner("publish/subscribe", ars -> {
			rt.convertAndSend(TOPIC, "Hello world @ " + Instant.now().toString());
		});
	}

	@Bean
	RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
		MessageListener ml = (message, pattern) -> {
			String str = new String(message.getBody());
			Log.info("message from " + TOPIC + ": " + str);
		};
		RedisMessageListenerContainer rmlc = new RedisMessageListenerContainer();
		rmlc.setConnectionFactory(connectionFactory);
		rmlc.addMessageListener(ml, new PatternTopic(this.TOPIC));
		return rmlc;
	}

	private long generateId() {
		long id = new Random().nextLong();
		return Math.max(id, id * -1);

	}

	interface OrderRepository extends JpaRepository<Order, Long> {

		Collection<Order> findByWhen(Date d);

	}

	interface LineItemRepository extends JpaRepository<LineItem, Long> {

	}

	public static void main(String[] args) {
		SpringApplication.run(SpringRedisImplApplication.class, args);
	}
}

@RedisHash("lineItems")
class LineItem implements Serializable {

	@Indexed
	private long orderId;

	@Id
	private long id;

	private String description;

	public LineItem(long orderId, long id, String description) {
		this.orderId = orderId;
		this.id = id;
		this.description = description;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}

@RedisHash("orders")
class Order implements Serializable {

	@Id
	private long id;

	@Indexed
	private Date when;

	private List<LineItem> lineItems;

	public Order(long id, Date when, List<LineItem> lineItems) {
		this.id = id;
		this.when = when;
		this.lineItems = lineItems;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getWhen() {
		return when;
	}

	public void setWhen(Date when) {
		this.when = when;
	}

	public List<LineItem> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<LineItem> lineItems) {
		this.lineItems = lineItems;
	}

}
