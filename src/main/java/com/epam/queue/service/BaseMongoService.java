package com.epam.queue.service;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;

public abstract class BaseMongoService {

    @Autowired
    private MessageSource messageSource;

    protected MongoCollection<Document> mongoCollection;

    private static final Logger logger = LoggerFactory.getLogger(BaseMongoService.class);

    public BaseMongoService(MongoDatabase mongoDatabase, String mongoCollectionName) {
        mongoCollection = mongoDatabase.getCollection(mongoCollectionName);
        if (mongoCollection != null && mongoCollection.count() == 0) {
            initCollection();
        }
    }

    public void initCollection() {
    }

    public MongoCollection<Document> getCollection() {
        return mongoCollection;
    }

    public void drop() {
        logger.info("mass change collection {} drop (BaseMongoService.drop)", mongoCollection.getNamespace().getCollectionName());
        mongoCollection.drop();
    }

    public void insertOne(Document document) {
        logger.info("change collection {} insertOne document {} (BaseMongoService.insertOne)", mongoCollection.getNamespace().getCollectionName(), document);
        mongoCollection.insertOne(document);
    }

    public void insertMany(List<Document> documents) {
        logger.info("mass change collection {} insertMany, documentIds {} (BaseMongoService.insertMany)", mongoCollection.getNamespace().getCollectionName(), documents.stream().map(d -> d.get("_id")).collect(Collectors.toList()).toString());
        mongoCollection.insertMany(documents);
    }

    public void replaceOne(Document document) {
        logger.info("change collection {} replaceOne by _id document = {} upsert = true (BaseMongoService.replaceOne)", mongoCollection.getNamespace().getCollectionName(), document);
        mongoCollection.replaceOne(eq("_id", document.get("_id")), document, new UpdateOptions().upsert(true));
    }

    public void deleteOne(Document document) {
        logger.info("change collection {} deleteOne document {} (BaseMongoService.deleteOne)", mongoCollection.getNamespace().getCollectionName(), document);
        mongoCollection.deleteOne(document);
    }

    public void truncate() {
        logger.info("mass change collection {} deleteMany(new Document()) (BaseMongoService.truncate)", mongoCollection.getNamespace().getCollectionName());
        mongoCollection.deleteMany(new Document());
    }

    public Object getMongoId(Document doc) {
        return doc.get("_id");
    }

    public void deleteByIds(List<Object> ids) {
        logger.info("mass change collection {} deleteMany ids {} (BaseMongoService.deleteByIds)", mongoCollection.getNamespace().getCollectionName(), ids);
        mongoCollection.deleteMany(in("_id", ids));
    }

    public void deleteDocuments(List<Document> docs) {
        List<Object> id = docs.stream().map(doc -> doc.get("_id")).collect(Collectors.toList());
        logger.info("mass change collection {} deleteMany ids {} (BaseMongoService.deleteDocuments)", mongoCollection.getNamespace().getCollectionName(), id);
        mongoCollection.deleteMany(in("_id", id));
    }

    public void replaceDocuments(List<Document> docs) {
        List<WriteModel<Document>> writes = new ArrayList<>();
        List<Object> ids = new ArrayList<>(docs.size());
        for (Document doc : docs) {
            Object id = doc.get("_id");
            ids.add(id);
            writes.add(new ReplaceOneModel<>(new Document("_id", id), doc));
        }
        logger.info("mass change collection {} bulkReplaceOne ids {} (BaseMongoService.replaceDocuments)", mongoCollection.getNamespace().getCollectionName(), ids);
        bulkWrite(writes);
    }

    public void bulkWrite(List<WriteModel<Document>> writes) {
        mongoCollection.bulkWrite(writes);
    }

    public Document findById(Object id) {
        return mongoCollection.find(eq("_id", id)).first();
    }

    public Document findFirstByProperty(String key, Object value) {
        return mongoCollection.find(eq(key, value)).first();
    }

    public Document findFirstByProperties(Map<String, Object> values) {
        List<Bson> filter = new ArrayList<>();
        values.entrySet().forEach(entry -> filter.add(eq(entry.getKey(), entry.getValue())));
        return mongoCollection.find(and(filter)).first();
    }

    protected <T> List<T> consumeToList(FindIterable<T> input) {
        return consumeToList(input, new ArrayList<>());
    }

    protected <T> List<T> consumeToList(FindIterable<T> input, List<T> output) {
        Consumer<T> consumer = output::add;
        input.forEach(consumer);
        return output;
    }

    public List<Document> find() {
        List<Document> docs = new ArrayList<>();
        mongoCollection.find().forEach((Consumer<Document>) docs::add);
        return docs;
    }

    public long count() {
        return mongoCollection.count();
    }

    public Document findFirst() {
        return mongoCollection.find().first();
    }

    protected String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    protected String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    public void updateOneFieldById(String id, String fieldName, Object value) {
        logger.info("change collection {} updateOne id {} fieldName {} value {} (BaseMongoService.updateOneFieldById)", mongoCollection.getNamespace().getCollectionName(), id, fieldName, value);
        mongoCollection.updateOne(eq("_id", new ObjectId(id)), new Document("$set", new Document(fieldName, value)));
    }
}
