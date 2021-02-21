package cn.lzg;
import java.util.*;
import org.apache.ibatis.annotations.*;
public interface TaskMapper {
    @MapKey("id")
    public HashSet<Task> getUnfinishedTask(); 
    public void insertTask(Task task);
    public void updateTask(Task task);
    public void deleteTask(Task task);
    public void createTaskTable();
    public int isTableExisted();
}