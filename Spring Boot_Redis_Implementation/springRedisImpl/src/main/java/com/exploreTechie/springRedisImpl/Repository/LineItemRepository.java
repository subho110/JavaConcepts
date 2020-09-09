package com.exploreTechie.springRedisImpl.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exploreTechie.springRedisImpl.Model.LineItem;

@Repository
public interface LineItemRepository extends JpaRepository<LineItem, Long> {

}
