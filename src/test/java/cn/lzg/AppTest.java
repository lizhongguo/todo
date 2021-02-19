package cn.lzg;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import java.util.*;
import java.sql.*;
import java.util.Date;
/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void testHSQLDB(){
        try{
            Class.forName("org.hsqldb.jdbcDriver");
            Connection sql_connection = DriverManager.getConnection("jdbc:hsqldb:file:d:/task", "SA", "");

            DatabaseMetaData dbm = sql_connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "TASK", null);
            final String sql_create = "CREATE TABLE TASK (id VARCHAR(64) PRIMARY KEY,description VARCHAR(1048576),parent VARCHAR(64), createTime DATETIME, finishedTime DATETIME, deadLine DATETIME, taskState INTEGER)";
            if(!tables.next()){
                sql_connection.createStatement().executeUpdate(sql_create);
                sql_connection.commit();
            }
            final String sql_append = "INSERT INTO TASK VALUES(?,?,?,?,?,?,?)";
            PreparedStatement ps_append = sql_connection.prepareStatement(sql_append);
            ps_append.setString(1, "uuid");
            ps_append.setString(2, "test");
            ps_append.setString(3, "parent");
            ps_append.setTimestamp(4, new java.sql.Timestamp(new Date().getTime()));
            ps_append.setTimestamp(5, new java.sql.Timestamp(new Date().getTime()));
            ps_append.setTimestamp(6, new java.sql.Timestamp(new Date().getTime()));
            ps_append.setInt(7, 1);
            ps_append.addBatch();
            System.out.println(ps_append.executeBatch());
            sql_connection.commit();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testTaskTreeManager(){
        try{
            TaskTreeManager ttm = new TaskTreeManager();
            ttm.printTaskTree();
                       
            String id1 = ttm.create_task("Test job 1", new Date(), new Date(2021,12,1));
            String id11 = ttm.create_task("Test job 11", new Date(), new Date(2021,12,1), id1);
            String id12 = ttm.create_task("Test job 12", new Date(), new Date(2021,12,1), id1);
            String id121 = ttm.create_task("Test job 121", new Date(), new Date(2021,12,1), id12);

            System.out.println(id1);
            System.out.println(id11);
            System.out.println(id12);
            System.out.println(id121);

            assertTrue("get desc", ttm.get_description(id12).equals("Test job 12"));
            
            //测试set_taskFinished
            assertTrue("get state", !ttm.is_finished(id1));
            assertTrue("get state", !ttm.is_finished(id11));
        try{
            ttm.set_taskFinished(id121);
        }catch(IOException e){
            e.printStackTrace();
        }

        assertTrue("get state", ttm.is_finished(id121));
            assertTrue("get state", ttm.is_finished(id12));
            assertTrue("get state", !ttm.is_finished(id1));

            //测试set_taskUnfinished
        try{
            ttm.set_taskUnfinished(id121);
        }catch(IOException e){
            e.printStackTrace();
        }
            assertTrue("get state", !ttm.is_finished(id121));
            assertTrue("get state", !ttm.is_finished(id11));
            assertTrue("get state", !ttm.is_finished(id1));
            ttm.printTaskTree();
            ttm.remove_task(id12, true);
            assertTrue("sons of root", ttm.get_sons(ttm.get_rootId()).size()==1);
            ttm.printTaskTree();
            ttm.remove_task(id1, false);
            assertTrue("sons of root", ttm.get_sons(ttm.get_rootId()).size()==0);
            ttm.printTaskTree();
            ttm.save();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
