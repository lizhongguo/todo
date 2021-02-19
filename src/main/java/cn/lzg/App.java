package cn.lzg;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        java.util.Date date = new java.util.Date();
        java.sql.Date sdate = new java.sql.Date(date.getTime());
        System.out.println(new java.sql.Timestamp(date.getTime()));
        System.out.println( "Hello World!" );
    }
}
