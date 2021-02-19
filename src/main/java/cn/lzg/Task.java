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
    private Date createTime;
    private Date finishedTime;
    private Date deadLine;

    //没必要区分WAITED和OVERTIME
    enum State{UNFINISHED,FINISHED};
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
        createTime = cT;
        deadLine = dL;   
        taskState = State.UNFINISHED;
        this.parent = parent;
        finishedTime = new Date();
    }

    public String get_id(){
        return id;
    }

    public String get_description(){
        return description;
    }

    public String get_parent(){
        return parent;
    }

    public void set_parent(String parent){
        this.parent = parent;
    }

    public void set_description(String desc){
        description=desc;
    }

    public void set_createTime(Date date){
        createTime = date;
    }

    public Date get_createTime(){
        return createTime;
    }

    public void set_deadline(Date date){
        deadLine = date;
    }

    public Date get_deadLine(){
        return deadLine;
    }

    public void set_finishedTime(Date date){
        finishedTime = date;
    }

    public Date get_finishedTime(){
        return finishedTime;
    }

    public void set_taskState(State s){
        if(s.equals(State.FINISHED))set_finishedTime(new Date());
        taskState = s;
    }

    public State get_taskState(){
        return taskState;
    }

    @Override
    public String toString(){
        return String.format("{type:Task,description:%s,id:%s,parentId:%s,createTime:%s,deadLine:%s,state:%s}", 
            description,id,parent,createTime.toString(),deadLine.toString(),taskState.toString());
    }
}
