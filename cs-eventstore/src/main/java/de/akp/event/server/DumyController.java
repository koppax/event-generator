package de.akp.event.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.akp.event.generate.EmitterFactoryOpenClose;
import de.akp.event.generate.old.FirstEventGenerator;

@RestController
public class DumyController {

	@Autowired
	FirstEventGenerator generator;
	
	SimpleServer server;

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
	
	@RequestMapping("/startServer")
	public void startServer() {
		if(server == null) {
			server = new SimpleServer(8082, new EmitterFactoryOpenClose(), 100);
		}
		server.centralLoop();
		
	}
	
	@RequestMapping("/stopServer")
	public void stopServer() {
		server.shutDown();
		
	}

}
