package cn.itheima.dao;

import cn.itheima.po.Book;

import java.util.List;

/**
 * 图书dao接口
 */
public interface BookDao {

    /**
     * 查询全部图书数据列表
     */
    List<Book> findAllBooks();

}
