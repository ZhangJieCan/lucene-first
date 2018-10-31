package cn.itheima.dao.impl;

import cn.itheima.dao.BookDao;
import cn.itheima.po.Book;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * 图书dao实现类
 */

public class BookDaoImpl implements BookDao {

    /**
     * 查询全部图书列表数据
     * @return
     */
    public List<Book> findAllBooks() {
        //定义图书结果集合list
        List<Book> bookList = new ArrayList<Book>();
        Connection connection = null;
        PreparedStatement psmt = null;
        ResultSet rs = null;
        //加载驱动
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //创建数据库链接对象
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaee91_travel","root","root");
            //定义sql语句
            String sql = "select * from book";
            //创建Statement语句对象
            psmt = con.prepareStatement(sql);

            //执行查询
            rs = psmt.executeQuery();

            // 处理结果集
            while (rs.next()){
                // 创建图书对象
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setBookName(rs.getString("bookname"));
                book.setPrice(rs.getFloat("price"));
                book.setPic(rs.getString("pic"));
                book.setBookDesc(rs.getString("bookdesc"));
                bookList.add(book);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            // 释放资源
            try{
                if(rs != null) rs.close();
                if(psmt != null) psmt.close();
                if(connection != null) connection.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }


        return bookList;
    }
}
