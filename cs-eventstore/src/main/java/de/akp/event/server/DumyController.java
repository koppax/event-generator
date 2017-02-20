package de.akp.event.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.akp.event.generate.FirstEventGenerator;

@RestController
public class DumyController {

	@Autowired
	FirstEventGenerator generator;

	@RequestMapping("/greeting")
	public String greeting(@RequestParam(value = "name", defaultValue = "World")
	String name) {
		return "ASDFAS " + name;
	}


	@RequestMapping("/start")
	public void start() {
		generator.start();

	}

	@RequestMapping("/stop")
	public void stop() {
		generator.stop();

	}

}
