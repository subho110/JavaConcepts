package com.exloreTechie.Jms_RabbitMq_Implementation.Controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exloreTechie.Jms_RabbitMq_Implementation.Model.Course;

@RestController
@RequestMapping("/app")
public class CourseController {
	
	RabbitTemplate rabbitTemplate;

	@GetMapping("/test/{name}")
	public String getCourses() {
		Course course = new Course(1,"adam",20);
		rabbitTemplate.convertAndSend("MobileQueue",course);
		rabbitTemplate.convertAndSend("Direct-Exchange", "MobileQueue", course);
		//for fanout exchange
		rabbitTemplate.convertAndSend("Fanout-Exchange", "", course);
		rabbitTemplate.convertAndSend("Topica-Exchange", "tv.mobile.ac", course);
		return "success";
	}
	
}
