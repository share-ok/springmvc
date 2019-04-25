package com.bk.dao.impl;

import com.bk.dao.BookDAO;
import com.bk.entity.BookEntity;
import com.ruanmou.vip.orm.core.handler.HandlerTemplate;
import com.ruanmou.vip.orm.core.handler.mysql.MySQLTemplateHandler;
import luna.vip.myspringmvc.annotation.MyRepository;

import java.util.List;

/**
 * @author luna
 * @date 2019-04-24
 */
@MyRepository
public class BookDAOImpl implements BookDAO {

    // 创建模板
    private HandlerTemplate template = new MySQLTemplateHandler();

    @Override
    public void addBook(BookEntity book) {
        template.save(book);
    }

    @Override
    public void deleteBook(Integer bookId) {
        BookEntity bookEntity = new BookEntity();
        bookEntity.setBookId(bookId);
        template.delete(bookEntity);
    }

    @Override
    public BookEntity getBookById(Integer bookId) {
        return template.queryForObject(BookEntity.class, bookId);
    }

    @Override
    public void updateBook(BookEntity book) {
        template.update(book);
    }

    @Override
    public List<BookEntity> getBooks() {
        return template.queryForList(BookEntity.class);
    }
}
