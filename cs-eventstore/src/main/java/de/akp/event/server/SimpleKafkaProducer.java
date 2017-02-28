package de.akp.event.server;

import java.util.Properties;
import java.util.stream.IntStream;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import de.akp.event.generate.EmitterFactoryBasic;
import de.akp.event.generate.IEventGenerator;

public class SimpleKafkaProducer {

	public static void main(String[] args) {
       
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.ByteArraySerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(configProperties);

        String topicName = "test";
        IEventGenerator<?> generator = new EmitterFactoryBasic().createEmitter();
        generator.start();
        IntStream.range(0,10)
        	.mapToObj(i->generator.nextEvent())
        	.forEach(e->producer
        			.send(new ProducerRecord<String, String>(topicName, e.toString())));
        
      
        producer.close();
	}

}
