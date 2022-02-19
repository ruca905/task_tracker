package ru.innopolis.university.task_tracker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.innopolis.university.task_tracker.forms.TaskSubmitForm;
import ru.innopolis.university.task_tracker.models.Project;
import ru.innopolis.university.task_tracker.models.Task;
import ru.innopolis.university.task_tracker.repositories.ProjectsRepository;
import ru.innopolis.university.task_tracker.repositories.TasksRepository;

@Controller
public class TasksController {
    private final TasksRepository tasksRepository;
    private final ProjectsRepository projectsRepository;

    public TasksController(TasksRepository tasksRepository, ProjectsRepository projectsRepository) {
        this.tasksRepository = tasksRepository;
        this.projectsRepository = projectsRepository;
    }

    @GetMapping("/task_list/{task_id}")
    public String getTaskPage(@PathVariable("task_id") Long taskId, ModelMap modelMap) {
        try {
            modelMap.addAttribute("task",
                    tasksRepository.findById(taskId).orElseThrow(IndexOutOfBoundsException::new));
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            return "redirect:/error/no_such_task";
        }
        return "task_info";
    }

    @PostMapping("/task_list/{task_id}/submit_data")
    public String submitData(@PathVariable("task_id") Long taskId, TaskSubmitForm taskSubmitForm) {
        tasksRepository.save(Task.builder()
                .id(taskId)
                .name(taskSubmitForm.getName())
                .description(taskSubmitForm.getDescription())
                .status(taskSubmitForm.getStatus())
                .priority(taskSubmitForm.getPriority())
                .project(tasksRepository.getById(taskId).getProject())
                .build());
        return "redirect:/task_list/" + taskId;
    }

    @GetMapping("/projects_list/{project_id}/delete_task/{task_id}")
    public String deleteTask(@PathVariable("task_id") Long taskId, @PathVariable("project_id") Long projectId) {
        tasksRepository.deleteById(taskId);
        return "redirect:/projects_list/" + projectId;
    }

    @GetMapping("/projects_list/{project_id}/create_task")
    public String createTask(@PathVariable("project_id") Long projectId) {
        Task task = new Task();
        tasksRepository.save(task);
        Project project = projectsRepository.findById(projectId).orElse(new Project());
        task.setProject(project);
        project.getTaskSet().add(task);
        projectsRepository.save(project);
        return "redirect:/projects_list/" + projectId;
    }
}