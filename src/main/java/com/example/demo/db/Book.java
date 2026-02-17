package com.example.demo.db;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity

public class Book {
	/**
	 * Google Books API id.
	 */
	@Id
	private String id;
	private String title;
	private String author;
	private Integer pageCount;

	public Book(String id, String title, String author) {
		this.id = id;
		this.title = title;
		this.author = author;
	}

	public Book() {
	}

	public Book(String id, String title, String author, Integer pageCount) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.pageCount = pageCount;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public Integer getPageCount() {
		return pageCount;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}

}
