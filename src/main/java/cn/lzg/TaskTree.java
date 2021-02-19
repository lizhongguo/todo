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
            id2task.put(task.get_id(), task);
        }
        buildTree();
    }

    protected void buildTree() throws IOException{
        Task task = null;

        for(Map.Entry<String,Task> e: id2task.entrySet()){
            task = e.getValue();
            if(task.get_id().equals(task.get_parent())){
                rootId = task.get_id();
            }else{
                set_rel(task.get_id(), task.get_parent());
            }
        }
    }

    public String get_rootId(){
        return rootId;
    }

    public void add_task(Task task) throws IOException{
        //加入的任务将作为root子节点
        if(id2task.get(task.get_id())==null)
            id2task.put(task.get_id(), task);
        set_rel(task.get_id(), task.get_parent());   
        saver.save_task(task);    
    }

    //task建立父子关系
    public void set_rel(String son, String parent) throws IOException{
        if(son==null || parent==null)throw new IOException("Null UUID");
        if(son.equals(parent))throw new IOException("Invalid Relationship");

        //检查是否会形成环，son在树中不能是parent的祖先
        String grand = get_parent(parent);
        while(true){
            if(son.equals(grand)){
                throw new IOException("Invalid Relationship");
            }
            if(grand.equals(get_parent(grand)))break;
            grand = get_parent(grand);
        }

        String oldParent = get_parent(son);
        get_sons(oldParent).remove(son);
        get_task(son).set_parent(parent);
        get_sons(parent).add(son);
        saver.save_task(get_task(son));
    }

    public TreeSet<String> get_sons(String parent){
        TreeSet<String> ts = taskTree.get(parent);
        if(ts==null){
            ts = new TreeSet<>();
            taskTree.put(parent, ts);
        }
        return ts;
    }

    public String get_parent(String son){
        return get_task(son).get_parent();
    }

    public final Task get_task(String taskId){
        return id2task.get(taskId);
    }

    public void get_descendant(String anc, Set<String> descendant){
        for(String son:get_sons(anc)){
            get_descendant(son, descendant);
        }
        descendant.add(anc);
    }

    public Set<String> get_descendant(String anc){
        HashSet<String> descendant = new HashSet<>();
        get_descendant(anc, descendant);
        return descendant;
    }

    public void remove_task (String taskId, boolean keep_son) throws IOException{
        if(taskId.equals(rootId))return;
        String parent = get_parent(taskId);
        TreeSet<String> sons = get_sons(taskId);
        if(!keep_son){
            //不能写成迭代式的
            for(String desc:get_descendant(taskId)){
                if(desc.equals(taskId))continue;
                saver.remove_task(get_task(desc));
                id2task.remove(desc);
                taskTree.remove(desc);
            }
        }else{
            for(String son:sons){
                set_rel(son, parent);
            }
        }
        get_sons(parent).remove(taskId);
        saver.remove_task(get_task(taskId));
        id2task.remove(taskId);
        taskTree.remove(taskId);
    }

    public void set_taskFinished(String taskId) throws IOException{
        if(taskId.equals(get_rootId()))return;
        //check its sons are all finished
        for(String son:get_sons(taskId)){
            if(get_task(son).get_taskState()!=Task.State.FINISHED){
            //子任务未完成，拒绝更改
                throw new IOException("Illegal State Uppdate");
            }
        }
        set_taskState(taskId,Task.State.FINISHED);
        set_taskFinished(get_parent(taskId));

        saver.save_task(get_task(taskId));
    }

    //可能出现子任务均完成，但是父任务未完成的情况
    public void set_taskUnfinished(String taskId) throws IOException{
        //make its parent unfinished too
        TreeSet<String> sons = get_sons(taskId);
        //如果任务不是叶子节点，则任务状态不改变
        if(!sons.isEmpty())throw new IOException("Illegal State Update");
        //所有祖先节点设置为未完成
        while(!taskId.equals(get_rootId())){
            set_taskState(taskId,Task.State.UNFINISHED);
            saver.save_task(get_task(taskId));

            taskId = get_parent(taskId);
        }

    }

    public void set_description(String taskId,String desc){
        get_task(taskId).set_description(desc);
        //写缓存
        saver.save_task(get_task(taskId));
    }

    public void set_createTime(String taskId,Date date){
        get_task(taskId).set_createTime(date);
        saver.save_task(get_task(taskId));
    }

    public void set_deadline(String taskId,Date date){
        get_task(taskId).set_deadline(date);
        saver.save_task(get_task(taskId));
    }

    public void set_taskState(String taskId,Task.State s){
        get_task(taskId).set_taskState(s);
        saver.save_task(get_task(taskId));
    }

    public void save(){
        saver.save();
    }
}
