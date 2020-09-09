package com.exloreTechie.Jms_RabbitMq_Implementation.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exloreTechie.Jms_RabbitMq_Implementation.Model.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

}
