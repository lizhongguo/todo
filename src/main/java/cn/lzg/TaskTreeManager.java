package cn.lzg;

public class TaskTreeManager {
    //更新task状态，任务完成后不删除任务，删除任务将导致任务再也无法找到
    //遍历task，定义优先级，定义过滤器
    //创建taskView，将Finished任务与Unfinished任务分离
    //对Unfinished任务的操作不会影响到Finished任务，需要额外保证
        //set_rel操作parent不能是Finished状态，son不能是Finished状态
        //将任务状态由Finished改变为Unfished时，其父任务的状态同时改变为Finished
        //加载任务时可只加载Unfinished任务，当涉及到Finished任务时，再从硬盘中加载
    TaskTree taskTree;
    public TaskTreeManager(){
        taskTree = new TaskTree();
    } 

    public void dfs(String cur){
        if(cur==null)cur = taskTree.get_rootId();
        System.out.println(taskTree.get_task(cur).get_description());        
        for(String son:taskTree.get_sons(cur)){
            dfs(son);
        }
    }

    public void getView(){
        
    }
}
