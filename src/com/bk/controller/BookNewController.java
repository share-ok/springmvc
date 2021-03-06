package com.bk.controller;

import com.bk.dao.BookDAO;
import com.bk.entity.BookEntity;
import luna.vip.myspringmvc.annotation.MyAutowired;
import luna.vip.myspringmvc.annotation.MyController;
import luna.vip.myspringmvc.annotation.MyRequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author luna
 * @date 2019-04-24
 */
@MyController
@MyRequestMapping("book")
public class BookNewController {

    @MyAutowired
    private BookDAO dao;

    @MyRequestMapping("listBook")
    public String list(HttpServletRequest request) {
        List<BookEntity> books = dao.getBooks();
        // 把books保存到request对象中(保证request对象存储的数据不丢失)
        request.setAttribute("books", books);

        // 转发到主页
        return "forward:/WEB-INF/book/list.jsp";
    }

    // 删除的方法
    public String delete(Integer bookId) {
        // 调用删除的方法
        dao.deleteBook(bookId);
        // 重定向初始化
        return  "redirect:/book/list";
    }

    // 初始化修改的方法
    public String initUpdate(HttpServletRequest request, Integer bookId){
        // 调用根据编号查询图书信息的方法
        BookEntity bookEntity = dao.getBookById(bookId);
        // 保存request对象中
        request.setAttribute("book", bookEntity);

        // 转发到修改页面
       return "/WEB-INF/book/update.jsp";
    }

    public String update(BookEntity bookEntity) {
        // 调用修改数据的方法
        dao.updateBook(bookEntity);

        return "redirect:/book/list";
    }

    // 初始化添加的方法
    public String initAdd() {

        // 转发到添加页面
        return "/WEB-INF/book/add.jsp";
    }

    public String add(BookEntity bookEntity) {
        // 调用修改数据的方法
        dao.addBook(bookEntity);
        return"redirect:/book/list";
    }
}
