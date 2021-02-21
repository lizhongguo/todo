package cn.lzg;
import java.io.IOException;
import java.util.*;

/**
 * 将任务以树的形式组织起来，表示任务依赖关系
 * @author lizhongguo
 * @version 1.0
 */
public class TaskTree {
    protected HashMap<String, Task> id2task = new HashMap<>();

    private HashMap<String, TreeSet<String>> taskTree = new HashMap<>();  
    private String rootId;

    private Saver saver;

    public TaskTree(Saver saver) throws IOException{
        this.saver = saver;
        Set<Task> saved_task = saver.get_task();

        if(saved_task.isEmpty()){
            rootId = UUID.randomUUID().toString();
            Task rootTask = new Task(rootId, "root", rootId, new Date(), new Date());
            id2task.put(rootId, rootTask);
            saver.save_task(rootTask);
            return;    
        }

        for(Task task:saved_task){
            id2task.put(task.getId(), task);
        }
        buildTree();
    }

    protected void buildTree() throws IOException{
        Task task = null;

        for(Map.Entry<String,Task> e: id2task.entrySet()){
            task = e.getValue();
            if(task.getId().equals(task.getParent())){
                rootId = task.getId();
            }else{
                setRel(task.getId(), task.getParent());
            }
        }
    }

    public String getRootId(){
        return rootId;
    }

    public void addTask(Task task) throws IOException{
        //加入的任务将作为root子节点
        if(id2task.get(task.getId())==null)
            id2task.put(task.getId(), task);
        setRel(task.getId(), task.getParent());   
        saver.save_task(task);    
    }

    //task建立父子关系
    public void setRel(String son, String parent) throws IOException{
        if(son==null || parent==null)throw new IOException("Null UUID");
        if(son.equals(parent))throw new IOException("Invalid Relationship");

        //检查是否会形成环，son在树中不能是parent的祖先
        String grand = getParent(parent);
        while(true){
            if(son.equals(grand)){
                throw new IOException("Invalid Relationship");
            }
            if(grand.equals(getParent(grand)))break;
            grand = getParent(grand);
        }

        String oldParent = getParent(son);
        getSons(oldParent).remove(son);
        getTask(son).setParent(parent);
        getSons(parent).add(son);
        saver.save_task(getTask(son));
    }

    public TreeSet<String> getSons(String parent){
        TreeSet<String> ts = taskTree.get(parent);
        if(ts==null){
            ts = new TreeSet<>();
            taskTree.put(parent, ts);
        }
        return ts;
    }

    public String getParent(String son){
        return getTask(son).getParent();
    }

    public final Task getTask(String taskId){
        return id2task.get(taskId);
    }

    public void getDescendant(String anc, Set<String> descendant){
        for(String son:getSons(anc)){
            getDescendant(son, descendant);
        }
        descendant.add(anc);
    }

    public Set<String> getDescendant(String anc){
        HashSet<String> descendant = new HashSet<>();
        getDescendant(anc, descendant);
        return descendant;
    }

    public void removeTask (String taskId, boolean keep_son) throws IOException{
        if(taskId.equals(rootId))return;
        String parent = getParent(taskId);
        TreeSet<String> sons = getSons(taskId);
        if(!keep_son){
            //不能写成迭代式的
            for(String desc:getDescendant(taskId)){
                if(desc.equals(taskId))continue;
                saver.remove_task(getTask(desc));
                id2task.remove(desc);
                taskTree.remove(desc);
            }
        }else{
            for(String son:sons){
                setRel(son, parent);
            }
        }
        getSons(parent).remove(taskId);
        saver.remove_task(getTask(taskId));
        id2task.remove(taskId);
        taskTree.remove(taskId);
    }

    public void setTaskFinished(String taskId) throws IOException{
        if(taskId.equals(getRootId()))return;
        //check its sons are all finished
        for(String son:getSons(taskId)){
            if(getTask(son).getTaskState()!=Task.State.FINISHED){
            //子任务未完成，拒绝更改
                throw new IOException("Illegal State Uppdate");
            }
        }
        setTaskState(taskId,Task.State.FINISHED);
        setTaskFinished(getParent(taskId));

        saver.save_task(getTask(taskId));
    }

    //可能出现子任务均完成，但是父任务未完成的情况
    public void setTaskUnfinished(String taskId) throws IOException{
        //make its parent unfinished too
        TreeSet<String> sons = getSons(taskId);
        //如果任务不是叶子节点，则任务状态不改变
        if(!sons.isEmpty())throw new IOException("Illegal State Update");
        //所有祖先节点设置为未完成
        while(!taskId.equals(getRootId())){
            setTaskState(taskId,Task.State.UNFINISHED);
            saver.save_task(getTask(taskId));

            taskId = getParent(taskId);
        }

    }

    public void setDescription(String taskId,String desc){
        getTask(taskId).setDescription(desc);
        //写缓存
        saver.save_task(getTask(taskId));
    }

    public void setCreatetime(String taskId,Date date){
        getTask(taskId).setCreatetime(date);
        saver.save_task(getTask(taskId));
    }

    public void setDeadline(String taskId,Date date){
        getTask(taskId).setDeadline(date);
        saver.save_task(getTask(taskId));
    }

    public void setTaskState(String taskId,Task.State s){
        getTask(taskId).setTaskState(s);
        saver.save_task(getTask(taskId));
    }

    public void save(){
        saver.save();
    }
}
