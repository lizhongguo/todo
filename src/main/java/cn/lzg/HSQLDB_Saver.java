package cn.lzg;
import java.util.*;
import java.sql.*;


public class HSQLDB_Saver extends Saver {
    private PreparedStatement ps_update,ps_delete,ps_append,ps_select;
    private final String sql_create = "CREATE TABLE TASK (id VARCHAR(64) PRIMARY KEY,description VARCHAR(1048576),parent VARCHAR(64), createTime DATETIME, finishedTime DATETIME, deadLine DATETIME, taskState INTEGER)";

    private final String sql_update = "UPDATE TASK SET description=?,parent=?,createTime=?,finishedTime=?,deadLine=?,taskState=? WHERE id = ?";
    private final String sql_append = "INSERT INTO TASK VALUES(?,?,?,?,?,?,?)";
    private final String sql_delete = "DELETE FROM TASK WHERE id = ?";
    private final String sql_select = "SELECT * FROM TASK WHERE taskState = ?";

    private static class sqlConnection{
        private static Connection sql_connection = null;
        public static Connection get(){
            try{
                Class.forName("org.hsqldb.jdbcDriver");
                sql_connection = DriverManager.getConnection("jdbc:hsqldb:file:d:/task", "SA", "");
            }catch(Exception e){
                e.printStackTrace();
            }
            return sql_connection;
        }
    }

    public HSQLDB_Saver(){
        try{
            Connection conn = sqlConnection.get();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "TASK", null);

            if(!tables.next()){
                Statement s_create = conn.createStatement();
                s_create.executeUpdate(sql_create);
                s_create.close();
            }
            /*
            while (tables.next()) {
                System.out.println("Table name: "+tables.getString("Table_NAME"));
                System.out.println("Table type: "+tables.getString("TABLE_TYPE"));
                System.out.println("Table schema: "+tables.getString("TABLE_SCHEM"));
                System.out.println("Table catalog: "+tables.getString("TABLE_CAT"));
                System.out.println(" ");
            }
            */

            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Set<Task> get_unfinished_task(){
        HashSet<Task> result = new HashSet<>();
        try{
            Connection conn = sqlConnection.get();
            ps_select = conn.prepareStatement(sql_select);
            ps_select.setInt(1, Task.State.UNFINISHED.ordinal());
            ResultSet rs = ps_select.executeQuery();
            while(rs.next()){
                String id = rs.getString(1),description = rs.getString(2),parent=rs.getString(3);
                java.util.Date createTime = rs.getDate(4), deadLine = rs.getDate(6),finishedTime = rs.getDate(5);
                Task.State taskState = Task.State.values()[rs.getInt(7)];
                Task task = new Task(id,description,parent,createTime,deadLine);
                result.add(task);
            }
            rs.close();
            ps_select.close();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally{
        }
        return result;
    }

    public void update_task(String taskId){
        try{
            Task task = get_cached_task(taskId);
            ps_update.setString(1, task.get_description());
            ps_update.setString(2, task.get_parent());
            ps_update.setTimestamp(3, new java.sql.Timestamp(task.get_createTime().getTime()));
            ps_update.setTimestamp(4, new java.sql.Timestamp(task.get_finishedTime().getTime()));
            ps_update.setTimestamp(5, new java.sql.Timestamp(task.get_deadLine().getTime()));
            ps_update.setInt(6, task.get_taskState().ordinal());
            ps_update.setString(7, taskId);
            ps_update.addBatch();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void delete_task(String taskId){
        try{
            ps_delete.setString(1, taskId);
            ps_delete.addBatch();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void append_task(String taskId){
        try{
            Task task = get_cached_task(taskId);
            ps_append.setString(1, taskId);
            ps_append.setString(2, task.get_description());
            ps_append.setString(3, task.get_parent());
            ps_append.setTimestamp(4, new java.sql.Timestamp(task.get_createTime().getTime()));
            ps_append.setTimestamp(5, new java.sql.Timestamp(task.get_finishedTime().getTime()));
            ps_append.setTimestamp(6, new java.sql.Timestamp(task.get_deadLine().getTime()));
            ps_append.setInt(7, task.get_taskState().ordinal());
            ps_append.addBatch();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void save(){
        Connection conn = sqlConnection.get();
        try{
            try{
                ps_append = conn.prepareStatement(sql_append);
                ps_update = conn.prepareStatement(sql_update);
                ps_delete = conn.prepareStatement(sql_delete);    
            }catch(Exception e){
                e.printStackTrace();
            }
    
            super.save();
            
            try{
                ps_delete.executeBatch();
                ps_delete.close();
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
            try{
                ps_update.executeBatch();
                ps_update.close();
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
            try{
                ps_append.executeBatch();
                ps_append.close();
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
            ps_append.close();
            ps_delete.close();
            ps_update.close();
            conn.close();    
        }catch(Exception e){
        }finally{
        }
    }

}
