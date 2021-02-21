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
        //Saver saver = new HSQLDBSaver();
        try{
            taskTree = new TaskTree(new MyBatisSaver());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void dfs(String cur, Consumer<Task> cs){
        if(cur==null)cur = taskTree.getRootId();
        Task curTask = taskTree.getTask(cur);

        if(curTask!=null && !cur.equals(taskTree.getRootId()))cs.accept(curTask);

        for(String son:taskTree.getSons(cur)){
            dfs(son,cs);
        }
    }

    public void printTaskTree(){
        dfs(null, (Task task)->{System.out.println(task);});
    }

    public String getRootId(){
        return taskTree.getRootId();
    }

    public String getParent(String sonId){
        return taskTree.getParent(sonId);
    }

    public TreeSet<String> getSons(String parentId){
        return taskTree.getSons(parentId);
    }

    public String createTask(String desc, Date cT, Date dL, String parentId) throws IOException{
        String id = UUID.randomUUID().toString();
        Task task = new Task(id, desc, parentId, cT, dL);
            taskTree.addTask(task);
        taskTree.addTask(task);
        return id;
    }

    public String createTask(String desc, Date cT, Date dL) throws IOException{
        return createTask(desc, cT, dL,taskTree.getRootId());
    }

    public void removeTask(String taskId,boolean keep_son) throws
     IOException{
            taskTree.removeTask(taskId,keep_son);
    }

    public void setTaskFinished(String taskId) throws IOException{
        taskTree.setTaskFinished(taskId);
    }

    public void setTaskUnfinished(String taskId)throws IOException{
            taskTree.setTaskUnfinished(taskId);
    }

    public void setDescription(String taskId,String desc){
        taskTree.setDescription(taskId, desc);
    }

    public void setCreatetime(String taskId,Date date){
        taskTree.setCreatetime(taskId, date);
    }

    public void setDeadline(String taskId,Date date){
        taskTree.setDeadline(taskId, date);
    }

    public String getDescription(String taskId){
        return taskTree.getTask(taskId).getDescription();
    }

    public Date getCreatetime(String taskId){
        return taskTree.getTask(taskId).getCreatetime();
    }

    public Date getDeadline(String taskId){
        return taskTree.getTask(taskId).getDeadline();
    }

    public boolean isFinished(String taskId){
        return taskTree.getTask(taskId).getTaskState().equals(Task.State.FINISHED);
    }

    public void save(){
        taskTree.save();
    }
}
