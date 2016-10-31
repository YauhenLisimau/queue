package com.epam.queue.web;

import com.epam.queue.service.TaskService;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

import static com.epam.queue.service.TaskService.JobStatus.merged;
import static com.epam.queue.service.TaskService.JobStatus.notMerged;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class QueueController {

    @Autowired
    private TaskService taskService;

    @RequestMapping("/")
    public String index(Principal user, Model model) {
        model.addAttribute("tasks", taskService.getActiveQueue());
        if (user != null) {
            model.addAttribute("user", user.getName());
        }
        return "index";
    }

    @RequestMapping(value = "/task", method = POST)
    public String addTask(Principal user, @RequestParam String url) {
        if (user != null) {
            Document task = new Document();
            taskService.setUsername(task, user.getName());
            Document job = new Document();
            taskService.setUrl(job, url);
            taskService.setName(job, getNameFromUrl(url));
            taskService.setStatus(job, notMerged.name());
            taskService.addJob(task, job);
            taskService.addToQueue(task);
        }
        return "redirect:/";
    }

    @RequestMapping("/task/done")
    public String taskDone(Principal user, @RequestParam String name) {
        Document task = taskService.findByName(name);
        if (task != null && taskService.getUsername(task).equals(user.getName())) {
            taskService.updateStatus(name, merged.name());
        }
        return "redirect:/";
    }

    @RequestMapping("/task/cancel")
    public String taskCancel(Principal user, @RequestParam String id) {
        return taskDone(user, id);
    }

    private String getNameFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }
}
