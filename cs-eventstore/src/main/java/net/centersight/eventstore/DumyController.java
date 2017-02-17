package net.centersight.eventstore;

import net.centersight.eventstore.generate.EventGenerator;

import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.stream.IStreamMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DumyController {

	@Autowired
	EventGenerator generator;

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

	@RequestMapping("/startHZJ")
	public void startHz() {
		JetInstance jet = Jet.newJetInstance();
		Jet.newJetInstance();

		IStreamMap<Integer, String> source = jet.getMap("source");

		source.put(0, "It was the best of times, " +
				"it was the worst of times ");
		source.put(1, "There were a king with a large jaw and a " +
				"queen with a plain face, on the that things in " +
				"general were settled for ever.");
		source.put(2, "It was the year of Our Lord one thousand " +
				"seven hundred and seventy-five. Spiritual " +
				"revelations were conceded to England at that " +
				"favoured period");

		// IMap<String, Integer> counts =
		long count = source.stream().map(x -> x.getValue().toLowerCase()).count();
		// .map(e -> e.getValue().toLowerCase())
		// .flatMap(line -> Arrays.stream(PATTERN.split(line)))
		// .collect(IMap.toIMap(m -> m, m -> 1,
		// (left, right) -> left + right));

		System.out.println("Counts=" + count);
		// System.out.println("Counts=" + counts.entrySet());
		Jet.shutdownAll();
	}

}
