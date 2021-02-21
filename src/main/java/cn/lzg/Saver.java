package cn.lzg;
import java.util.*;

public abstract class Saver{
    public abstract Set<Task> get_unfinished_task();

    private HashMap<String,Task> task_cache = new HashMap<>();
    protected TreeSet<String> changed = new TreeSet<>();
    protected TreeSet<String> deleted = new TreeSet<>();
    protected TreeSet<String> appended = new TreeSet<>();

    public Set<Task> get_task(){
        Set<Task> unfinished_task =  get_unfinished_task();
        for(Task task:unfinished_task){
            task_cache.put(task.getId(),task);
        }
        return unfinished_task;
    }

    public void save_task(Task task){
        if(changed.contains(task.getId())){
            return;
        }

        if(task_cache.containsKey(task.getId())){
            if(appended.contains(task.getId()))return;
            changed.add(task.getId());
            return;
        }
        appended.add(task.getId());
        task_cache.put(task.getId(), task);
    }

    public void remove_task(Task task){
        deleted.add(task.getId());
        changed.remove(task.getId());
        appended.remove(task.getId());
        task_cache.remove(task.getId());
    }

    //delete task and rel in deleted
    //for each key in changed, flush task into storage
    public abstract void update_task(String taskId);
    public abstract void delete_task(String taskId);
    public abstract void append_task(String taskId);

    public Task get_cached_task(String taskId){
        return task_cache.get(taskId);
    }

    public void save(){
        for(String taskId:deleted){
            delete_task(taskId);
        }

        for(String taskId:appended){
            append_task(taskId);
        }

        for(String taskId:changed){
            update_task(taskId);
        }
        deleted.clear();
        appended.clear();
        changed.clear();
    }

}
