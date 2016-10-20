package com.epam.queue.web;

import com.epam.queue.service.TaskService;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class QueueController {

    @Autowired
    private TaskService taskService;

    @Value("${requestUrl}")
    private String requestUrl;

    @RequestMapping("/")
    public String index(Principal user, Model model) {
        List<Document> tasks = taskService.find();
        tasks.forEach(it -> {
            it.put("creationDate", taskService.getCreationDate(it));
            it.put("requestUrl", requestUrl + taskService.getId(it));
        });
        model.addAttribute("tasks", tasks);
        if (user != null) {
            model.addAttribute("user", user.getName());
        }
        return "index";
    }

    @RequestMapping(value = "/task", method = POST)
    public String addTask(Principal user, @RequestParam String taskId) {
        if (user != null) {
            Document task = new Document();
            taskService.setId(task, taskId);
            taskService.setUsername(task, user.getName());
            taskService.insertOne(task);
        }
        return "redirect:/";
    }

    @RequestMapping("/task/done")
    public String taskDone(Principal user, @RequestParam String id) {
        Document task = taskService.findById(new ObjectId(id));
        if (task != null && taskService.getUsername(task).equals(user.getName())) {
            taskService.deleteOne(task);
        }
        return "redirect:/";
    }

    @RequestMapping("/task/cancel")
    public String taskCancel(Principal user, @RequestParam String id) {
        return taskDone(user, id);
    }
}
