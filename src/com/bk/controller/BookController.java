package com.bk.controller;

import com.bk.base.BaseServlet;
import com.bk.dao.BookDAO;
import com.bk.dao.impl.BookDAOImpl;
import com.bk.entity.BookEntity;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 软谋教育Java VIP课程
 * <pre>
 *    今日内容:过滤器和监听器
 * </pre>
 *
 * @author gerry
 * @date 2018-06-28
 */
@WebServlet(name = "BookController", urlPatterns = "/book1")
public class BookController extends BaseServlet {

    // 初始化图书信息方法
    public String list(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 创建DAO层实现
        BookDAO dao = new BookDAOImpl();
        List<BookEntity> books = dao.getBooks();
        // 把books保存到request对象中(保证request对象存储的数据不丢失)
        request.setAttribute("books", books);

        // 转发到主页
        return "/WEB-INF/book/list.jsp";
    }

    // 删除的方法
    public void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 创建DAO层实现
        BookDAO dao = new BookDAOImpl();
        // 获取到需要删除的编号
        String bookId = request.getParameter("bookId");
        // 调用删除的方法
        dao.deleteBook(Integer.valueOf(bookId));
        // 重定向初始化
        response.sendRedirect(request.getContextPath()+"/book?param=list");
    }

    // 初始化修改的方法
    public String initUpdate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 创建DAO层实现
        BookDAO dao = new BookDAOImpl();
        // 获取到要修改的bookId
        String bookId = request.getParameter("bookId");
        // 调用根据编号查询图书信息的方法
        BookEntity bookEntity = dao.getBookById(Integer.valueOf(bookId));
        // 保存request对象中
        request.setAttribute("book", bookEntity);

        // 转发到修改页面
       return "/WEB-INF/book/update.jsp";
    }

    public void update(HttpServletRequest request, HttpServletResponse response, BookEntity bookEntity) throws Exception {
        // 创建DAO层实现
        BookDAO dao = new BookDAOImpl();
        // 调用修改数据的方法
        dao.updateBook(bookEntity);

        response.sendRedirect(request.getContextPath()+"/book?param=list");
    }

    // 初始化添加的方法
    public String initAdd(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 转发到添加页面
        return "/WEB-INF/book/add.jsp";
    }



    public void add(HttpServletRequest request, HttpServletResponse response, BookEntity bookEntity) throws Exception {
        System.out.println(bookEntity);
        // 创建DAO层实现
        BookDAO dao = new BookDAOImpl();
        // 调用修改数据的方法
        dao.addBook(bookEntity);

        response.sendRedirect(request.getContextPath()+"/book?param=list");
    }

    /**
     * 把一个字符串转换为一个日期对象
     * @param dateString
     * @return
     */
    private Date convertStringToDate(String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            return simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
