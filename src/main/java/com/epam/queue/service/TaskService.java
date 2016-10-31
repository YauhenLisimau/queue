package com.epam.queue.service;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Filters.gt;

@Service
public class TaskService extends BaseMongoService {

    private static final String collectionName = "task";
    private enum JobStatus {
        merged
    }

    @Autowired
    public TaskService(MongoDatabase mongoDatabase) {
        super(mongoDatabase, collectionName);
    }

    public List<Document> getActiveQueue() {
        return consumeToList(mongoCollection.find(exists("numberInQueue")).sort(Sorts.ascending("numberInQueue")));
    }

    public Integer getActiveQueueCount() {
        return Long.valueOf(mongoCollection.count(exists("numberInQueue"))).intValue();
    }

    public void addToQueue(Document task) {
        setNumberInQueue(task, getActiveQueueCount() + 1);
        insertOne(task);
    }

    public void removeFromQueue(Document task) {
        consumeToList(mongoCollection.find(gt("numberInQueue", getNumberInQueue(task))))
                .forEach(it -> updateOneFieldById(getObjectId(it), "numberInQueue", getNumberInQueue(it) - 1));
        task.remove("numberInQueue");
        replaceOne(task);
    }

    public void moveUp(Document task) {
        Integer numberInQueue = getNumberInQueue(task);
        Document prev = findFirstByProperty("numberInQueue", numberInQueue - 1);
        updateOneFieldById(getObjectId(prev), "numberInQueue", numberInQueue + 1);
        updateOneFieldById(getObjectId(task), "numberInQueue", numberInQueue - 1);
    }

    public void moveDown(Document task) {
        Integer numberInQueue = getNumberInQueue(task);
        Document next = findFirstByProperty("numberInQueue", numberInQueue + 1);
        updateOneFieldById(getObjectId(next), "numberInQueue", numberInQueue - 1);
        updateOneFieldById(getObjectId(task), "numberInQueue", numberInQueue + 1);
    }

    public void updateStatus(String jobName, String status) {
        Document task = findFirstByProperty("jobs.name", jobName);
        if (task != null) {
            for (Document job : getJobs(task)) {
                if (jobName.equals(getName(job))) {
                    setStatus(job, status);
                }
            }
            replaceOne(task);
            if (getJobs(task).stream().filter(it -> !JobStatus.merged.name().equals(getStatus(it))).count() == 0) {
                removeFromQueue(task);
            }
        }
    }

    //------------------------- Fields -------------------------------------------//

    public String getUsername(Document task) {
        return task.getString("username");
    }

    public void setUsername(Document task, String username) {
        task.put("username", username);
    }

    public Integer getNumberInQueue(Document task) {
        return task.getInteger("numberInQueue");
    }

    public void setNumberInQueue(Document task, Integer numberInQueue) {
        task.put("numberInQueue", numberInQueue);
    }

    @SuppressWarnings("unchecked")
    public List<Document> getJobs(Document task) {
        return task.get("jobs", List.class);
    }

    public void setJobs(Document task, List<Document> jobs) {
        task.put("jobs", jobs);
    }

    public String getName(Document job) {
        return job.getString("name");
    }

    public void setName(Document job, String name) {
        job.put("name", name);
    }

    public String getStatus(Document job) {
        return job.getString("status");
    }

    public void setStatus(Document job, String status) {
        job.put("status", status);
    }
}
