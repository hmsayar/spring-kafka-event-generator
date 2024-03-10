package com.example.EventGenerator.Task;

import com.example.EventGenerator.Service.DataGeneratorService;
import com.example.EventGenerator.Service.EventPublisherService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;

@Component
public class EventGeneratorTask {

    @Autowired
    private DataGeneratorService dataGeneratorService;
    @Autowired
    private EventPublisherService eventPublisherService;

    private TaskScheduler scheduler;
    private ScheduledFuture<?> futureTask;

    private final String topic = "mytopic";
    private final Random random = new Random();

    @Autowired
    private ObjectMapper objectMapper;

    private String extractKeyFromEvent(String eventJson) {
        try{
            JsonNode rootNode = objectMapper.readTree(eventJson);
            JsonNode userIdNode = rootNode.path("userId");
            if(!userIdNode.isMissingNode()){
                return userIdNode.asText();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }


    @PostConstruct
    private void init(){
        scheduler = new ThreadPoolTaskScheduler();
        ((ThreadPoolTaskScheduler) scheduler).initialize();
        scheduleNext();
    }

    private void scheduleNext(){
        //keep reference to future task further development.
        //that this:: syntax weird and didn't understand entirely. look at later.
        futureTask = scheduler.schedule(this::generateAndPublishRandomEvent, nextExecutionTime());
    }

    //apply strategy pattern here later
    private void generateAndPublishRandomEvent() {

        switch (random.nextInt(4)) {
            case 0:
                publishEvent(dataGeneratorService.generateProductViewedEvent());
                break;
            case 1:
                publishEvent(dataGeneratorService.generateProductAddedToCartEvent());
                break;
            case 2:
                publishEvent(dataGeneratorService.generateProductPurchasedEvent());
                break;
            case 3:
                publishEvent(dataGeneratorService.generateProductFavoritedEvent());
                break;
        }
        scheduleNext();
    }

    private void publishEvent(String event) {
        String key = extractKeyFromEvent(event);
        eventPublisherService.publishEvent(topic,key, event);
        //System.out.println("Publishing event: " + event);
    }

    private Date nextExecutionTime(){
        long delay = 1000L * (1+ random.nextInt(10));
        return new Date(System.currentTimeMillis() + delay);
    }

}
