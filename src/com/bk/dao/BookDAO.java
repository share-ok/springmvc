package com.bk.dao;

import com.bk.entity.BookEntity;

import java.util.List;

/**
 * @author luna
 * @date 2019-04-24
 */
public interface BookDAO {
    void addBook(BookEntity book); // 新增图书
    void deleteBook(Integer bookId); // 删除图书
    BookEntity getBookById(Integer bookId); // 根据编号获取图书对象
    void updateBook(BookEntity book); // 修改图书信息
    List<BookEntity> getBooks(); // 获取所有的图书新方法
}
