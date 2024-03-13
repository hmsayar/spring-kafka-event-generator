package com.example.EventGenerator.Config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class KafkaStreamsConfig {

    @Value("${spring.kafka.bootstrap.servers}")
    private  String bootstrapServer;

    @Autowired
    private ObjectMapper objectMapper;

    private String extractProductId(String eventJson){
        try{
            JsonNode rootNode = objectMapper.readTree(eventJson);
            JsonNode productIdNode = rootNode.path("productId");
            if(!productIdNode.isMissingNode()){
                return productIdNode.asText();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    public KafkaStreams kafkaStreams(){
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "event-aggregator-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        //configs about deserialization, serialization. relevant for both source topic and stream topics
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());


        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> sourceStream = builder.stream("mytopic");


        KTable<String, Long> productViewedCounts = sourceStream
                .filter((key, value) -> value.contains("\"eventType\":\"ProductViewed\""))
                .selectKey((key, value) -> extractProductId(value))
                .groupByKey()
                .count(Materialized.as("ProductViewedCounts"));

        KTable<String, Long> productPurchasedCounts = sourceStream
                .filter((key, value) -> value.contains("\"eventType\":\"ProductPurchased\""))
                .selectKey((key, value) -> extractProductId(value))
                .groupByKey()
                .count(Materialized.as("ProductPurchasedCounts"));


        productViewedCounts.toStream()
                .mapValues(value -> Long.toString(value))
                .to("ProductViewedCountsTopic");

        productPurchasedCounts.toStream()
                .mapValues(value -> Long.toString(value))
                .to("ProductPurchasedCountsTopic");


        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        return streams;

    }
}

//$ kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic ProductPurchasedCountsTopic --from-beginning --property print.key=true --proper
//ty print.partition=true