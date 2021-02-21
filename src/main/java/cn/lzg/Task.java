package cn.lzg;

import java.util.Date;

/**
 * 描述一个todo任务的基本属性
 * @author lizhongguo
 * @version 1.0
 */
public class Task {
    private String description;
    private String id;
    private String parent;
    private Date createtime;
    private Date finishedtime;
    private Date deadline;

    //没必要区分WAITED和OVERTIME
    public static enum State{UNFINISHED(0),FINISHED(1);
        private int value;
        private State(int i){
            value = i;
        }

        public void setValue(int v){
            this.value = v;
        }
        public int getValue(){
            return this.value;
        }
    };
    private State taskState;

    /*
    public Task(){
        description = "Empty";
        id = null;
        createTime = new Date();
        finishedTime = new Date();
        deadLine = new Date();
        parent = null;
        taskState = State.UNFINISHED;
    }
    */

    public Task(String id, String desc, String parent,  Date cT, Date dL){
        description = desc;
        this.id = id;
        createtime = cT;
        deadline = dL;   
        taskState = State.UNFINISHED;
        this.parent = parent;
        finishedtime = new Date();
    }

    public String getId(){
        return id;
    }

    public String getDescription(){
        return description;
    }

    public String getParent(){
        return parent;
    }

    public void setParent(String parent){
        this.parent = parent;
    }

    public void setDescription(String desc){
        description=desc;
    }

    public void setCreatetime(Date date){
        createtime = date;
    }

    public Date getCreatetime(){
        return createtime;
    }

    public void setDeadline(Date date){
        deadline = date;
    }

    public Date getDeadline(){
        return deadline;
    }

    public void setFinishedtime(Date date){
        finishedtime = date;
    }

    public Date getFinishedtime(){
        return finishedtime;
    }

    public void setTaskState(State s){
        if(s.equals(State.FINISHED))setFinishedtime(new Date());
        taskState = s;
    }

    public State getTaskState(){
        return taskState;
    }

    @Override
    public String toString(){
        return String.format("{type:Task,description:%s,id:%s,parentId:%s,createTime:%s,deadLine:%s,state:%s}", 
            description,id,parent,createtime.toString(),deadline.toString(),taskState.toString());
    }
}
