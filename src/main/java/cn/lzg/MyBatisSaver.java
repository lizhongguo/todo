package cn.lzg;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

import org.apache.ibatis.*;
import java.util.*;

public class MyBatisSaver extends Saver{
    private SqlSessionFactory sqlSessionFactory;
    public MyBatisSaver() throws Exception{
        String resource = "cn/lzg/HSQLDB_config.xml"; 
        InputStream inputStream = Resources.getResourceAsStream(resource);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        createTableIfNotExist();
    }

    private void createTableIfNotExist(){
        try(SqlSession sqlSession = sqlSessionFactory.openSession();){
            TaskMapper mapper = sqlSession.getMapper(TaskMapper.class);
            if(mapper.isTableExisted()<=0){
                mapper.createTaskTable();
            }
            sqlSession.commit();
        }
    }

    public Set<Task> get_unfinished_task(){
        try(SqlSession sqlSession = sqlSessionFactory.openSession();){
            TaskMapper mapper = sqlSession.getMapper(TaskMapper.class);
            Set<Task> tasks = mapper.getUnfinishedTask();
            sqlSession.commit();
            return tasks;
        }
    }

    public void update_task(String taskId){
        try(SqlSession sqlSession=sqlSessionFactory.openSession();){
            TaskMapper mapper = sqlSession.getMapper(TaskMapper.class);
            Task task = get_cached_task(taskId);
            mapper.updateTask(task);
            sqlSession.commit();
            changed.remove(taskId);
        }
    }

    public void delete_task(String taskId){
        try(SqlSession sqlSession=sqlSessionFactory.openSession();){
            TaskMapper mapper = sqlSession.getMapper(TaskMapper.class);
            Task task = get_cached_task(taskId);
            mapper.deleteTask(task);
            sqlSession.commit();
            deleted.remove(taskId);
        }
    }

    public void append_task(String taskId){
        try(SqlSession sqlSession=sqlSessionFactory.openSession();){
            TaskMapper mapper = sqlSession.getMapper(TaskMapper.class);
            Task task = get_cached_task(taskId);
            mapper.insertTask(task);
            sqlSession.commit();
            appended.remove(taskId);
        }
    }

    @Override
    public void save(){
        try( SqlSession sqlSession=sqlSessionFactory.openSession(ExecutorType.BATCH, false);){
            TaskMapper mapper = sqlSession.getMapper(TaskMapper.class);

            for(String taskId:deleted){
                Task task = get_cached_task(taskId);
                mapper.deleteTask(task);
            }
            sqlSession.commit();
            sqlSession.flushStatements();
            deleted.clear();

            for(String taskId:appended){
                Task task = get_cached_task(taskId);
                mapper.insertTask(task);
            }
            sqlSession.commit();
            sqlSession.flushStatements();
            appended.clear();

            for(String taskId:changed){
                Task task = get_cached_task(taskId);
                mapper.updateTask(task);                
            }
            sqlSession.commit();
            sqlSession.flushStatements();
            changed.clear();
        }
    }
}
