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
    private Date createTime;
    private Date finishedTime;
    private Date deadLine;

    enum State{UNFINISHED,OVERTIME,FINISHED};
    private State taskState;

    public Task(){
    }

    public Task(String desc, String id, Date cT, Date dL){
        description = desc;
        this.id = id;
        createTime = cT;
        deadLine = dL;   
    }

    public String get_id(){
        return id;
    }

    public void set_id(String id){
        this.id = id;
    }

    public String get_description(){
        return description;
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

    public Date get_deadline(){
        return deadLine;
    }

    public void set_finishedTime(Date date){
        finishedTime = date;
    }

    public Date get_finishedTime(){
        return finishedTime;
    }

    public void set_taskState(State s){
        taskState = s;
    }

    public State get_taskState(){
        return taskState;
    }

    @Override
    public String toString(){
        return String.format("{type:Task,description:%s,id:%s,createTime:%s,deadLine:%s}", 
            description,id,createTime.toString(),deadLine.toString());
    }

}
