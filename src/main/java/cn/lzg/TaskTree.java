package cn.lzg;
import java.util.Date;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.UUID;

//todo TaskTree UnfinishedView单例模式
//todo TaskTree UnfinishedView finalize 将修改同步到TaskTree
//todo TaskTree 状态更新
//todo TaskTree lazy load
//todo TaskTree 多用户支持
//todo TaskTree 用户同步
//todo todo用户界面
/**
 * 将任务以树的形式组织起来，表示任务依赖关系
 * @author lizhongguo
 * @version 1.0
 */
public class TaskTree {
    private HashMap<String, TreeSet<String>> taskTree;  
    private HashMap<String, String> taskParent;
    private String rootId;
    private HashMap<String, Task> id2task;

    public TaskTree(){
        rootId = UUID.randomUUID().toString();
    }

    //lazy load

    //串化
    public void parse(Path p){}

    public String get_rootId(){
        return rootId;
    }


    private void add_task(Task task, String parentId) {
        //加入的任务将作为root子节点
        if(id2task.get(task.get_id())==null)
            id2task.put(task.get_id(), task);
        set_rel(task.get_id(), parentId);        
    }

    private void add_task(Task task){
        add_task(task,rootId);
    }

    public void create_task(String desc, Date cT, Date dL, String parentId){
        String id = UUID.randomUUID().toString();
        Task task = new Task(desc, id, cT, dL);
        add_task(task,parentId);
    }

    public void create_task(String desc, Date cT, Date dL){
        create_task(desc, cT, dL,rootId);
    }

    //task建立父子关系
    public void set_rel(String son, String parent){
        //检查是否会形成环，son在树中不能是parent的祖先
        if(son==null || parent==null)return;

        String grand = get_parent(parent);
        while(grand!=null){
            if(son.equals(grand))return;
            grand = taskParent.get(grand);
        }

        //此处应该封装
        String oldParent = get_parent(son);
        TreeSet<String> ts = get_sons(parent);

        if(oldParent!=null){
            if(oldParent.equals(parent))return;
            get_sons(oldParent).remove(son);
        }

        taskParent.put(son, parent);
        ts.add(son);
    }

    public TreeSet<String> get_sons(String parent){
        TreeSet<String> ts = taskTree.get(parent);
        if(ts==null){
            taskTree.put(parent, ts);
        }
        return ts;
    }

    public String get_parent(String son){
        return taskParent.get(son);
    }

    public final Task get_task(String taskId){
        return id2task.get(taskId);
    }

    public void remove_task(String taskId, boolean keep_son){
        if(taskId==rootId)return;
        TreeSet<String> sons = get_sons(taskId);
        if(!keep_son){
            for(String son:sons){
                remove_task(son,false);
            }
        }else{
            String parent = taskParent.get(taskId);
            for(String son:sons){
                set_rel(son, parent);
            }
        }
        taskParent.remove(taskId);
        id2task.remove(taskId);
    }

    //更新task状态，任务完成后不删除任务，删除任务将导致任务再也无法找到
    //遍历task，定义优先级，定义过滤器
    //创建taskView，将Finished任务与Unfinished任务分离
    //对Unfinished任务的操作不会影响到Finished任务，需要额外保证
        //set_rel操作parent不能是Finished状态，son不能是Finished状态
        //将任务状态由Finished改变为Unfished时，其父任务的状态同时改变为Finished
        //加载任务时可只加载Unfinished任务，当涉及到Finished任务时，再从硬盘中加载

    //装饰器模式+单例模式
    /**
     * 该类缓存了TaskTree的任务依赖关系,与父类公用id2task，修改操作会同步到父类
     * 如果将Task修改为Finished，该任务将从View中移除
     */
    public class UnfinishedView extends TaskTree{
        private HashMap<String, TreeSet<String>> taskTree;  
        private HashMap<String, String> taskParent;
        private String rootId;

        public UnfinishedView(){
            rootId = TaskTree.this.get_rootId();

            LinkedList<String> queue = new LinkedList<>();
            queue.addLast(rootId);

            while(!queue.isEmpty()){
                String cur = queue.removeFirst();
                String parent = TaskTree.this.get_parent(cur);
                
                set_rel(cur, parent);

                for(String son:TaskTree.this.get_sons(cur)){
                    if(TaskTree.this.get_task(son).get_taskState()!=Task.State.FINISHED){
                        queue.addLast(son);
                    }
                }    
            }
        }
    }
}
