package com.sp.ordermanagement.repository;

import com.sp.ordermanagement.enitity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book,Long> {
}
