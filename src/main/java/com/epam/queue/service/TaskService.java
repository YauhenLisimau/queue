package com.epam.queue.service;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TaskService extends BaseMongoService {

    private static final String collectionName = "task";

    @Autowired
    public TaskService(MongoDatabase mongoDatabase) {
        super(mongoDatabase, collectionName);
    }

    public String getId(Document task) {
        return task.getString("id");
    }

    public void setId(Document task, String id) {
        task.put("id", id);
    }

    public String getUsername(Document task) {
        return task.getString("username");
    }

    public void setUsername(Document task, String username) {
        task.put("username", username);
    }

    public Date getEndDate(Document task) {
        return task.getDate("endDate");
    }

    public void setEndDate(Document task, Date endDate) {
        task.put("endDate", endDate);
    }
}
