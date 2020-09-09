package com.exploreTechie.springRedisImpl.Repository;

import java.util.Collection;
import java.util.Date;

import javax.persistence.criteria.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	Collection<Order> findByWhen(Date d);
	
}
