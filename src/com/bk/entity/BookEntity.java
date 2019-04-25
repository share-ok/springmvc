package com.bk.entity;

import com.ruanmou.vip.orm.annotation.Column;
import com.ruanmou.vip.orm.annotation.PK;
import com.ruanmou.vip.orm.annotation.Table;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author luna
 * @date 2019-04-24
 */
@Table("t_book")
public class BookEntity {

    @PK
    @Column("book_id")
    private Integer bookId;

    @Column("book_name")
    private String bookName;

    @Column("book_author")
    private String bookAuthor;

    @Column("book_price")
    private BigDecimal bookPrice;

    @Column("book_date")
    private Date bookDate;

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public BigDecimal getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(BigDecimal bookPrice) {
        this.bookPrice = bookPrice;
    }

    public Date getBookDate() {
        return bookDate;
    }

    public void setBookDate(Date bookDate) {
        this.bookDate = bookDate;
    }

    public BookEntity(String bookName, String bookAuthor) {
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
    }

    public BookEntity() {
    }
}
