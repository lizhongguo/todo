package cn.lzg;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.*;
public class TaskTreeManager {
    //更新task状态，任务完成后不删除任务，删除任务将导致任务再也无法找到
    //遍历task，定义优先级，定义过滤器
    //创建taskView，将Finished任务与Unfinished任务分离
    //对Unfinished任务的操作不会影响到Finished任务，需要额外保证
        //set_rel操作parent不能是Finished状态，son不能是Finished状态
        //将任务状态由Finished改变为Unfished时，其父任务的状态同时改变为Finished
        //加载任务时可只加载Unfinished任务，当涉及到Finished任务时，再从硬盘中加载
    private TaskTree taskTree;
    
    public TaskTreeManager() throws IOException{
        Saver saver = new HSQLDB_Saver();
        taskTree = new TaskTree(saver);
    }

    public void dfs(String cur, Consumer<Task> cs){
        if(cur==null)cur = taskTree.get_rootId();
        Task curTask = taskTree.get_task(cur);

        if(curTask!=null && !cur.equals(taskTree.get_rootId()))cs.accept(curTask);

        for(String son:taskTree.get_sons(cur)){
            dfs(son,cs);
        }
    }

    public void printTaskTree(){
        dfs(null, (Task task)->{System.out.println(task);});
    }

    public String get_rootId(){
        return taskTree.get_rootId();
    }

    public String get_parent(String sonId){
        return taskTree.get_parent(sonId);
    }

    public TreeSet<String> get_sons(String parentId){
        return taskTree.get_sons(parentId);
    }

    public String create_task(String desc, Date cT, Date dL, String parentId) throws IOException{
        String id = UUID.randomUUID().toString();
        Task task = new Task(id, desc, parentId, cT, dL);
            taskTree.add_task(task);
        taskTree.add_task(task);
        return id;
    }

    public String create_task(String desc, Date cT, Date dL) throws IOException{
        return create_task(desc, cT, dL,taskTree.get_rootId());
    }

    public void remove_task(String taskId,boolean keep_son) throws
     IOException{
            taskTree.remove_task(taskId,keep_son);
    }

    public void set_taskFinished(String taskId) throws IOException{
        taskTree.set_taskFinished(taskId);
    }

    public void set_taskUnfinished(String taskId)throws IOException{
            taskTree.set_taskUnfinished(taskId);
    }

    public void set_description(String taskId,String desc){
        taskTree.set_description(taskId, desc);
    }

    public void set_createTime(String taskId,Date date){
        taskTree.set_createTime(taskId, date);
    }

    public void set_deadline(String taskId,Date date){
        taskTree.set_deadline(taskId, date);
    }

    public String get_description(String taskId){
        return taskTree.get_task(taskId).get_description();
    }

    public Date get_createTime(String taskId){
        return taskTree.get_task(taskId).get_createTime();
    }

    public Date get_deadline(String taskId){
        return taskTree.get_task(taskId).get_deadLine();
    }

    public boolean is_finished(String taskId){
        return taskTree.get_task(taskId).get_taskState().equals(Task.State.FINISHED);
    }

    public void save(){
        taskTree.save();
    }
}
