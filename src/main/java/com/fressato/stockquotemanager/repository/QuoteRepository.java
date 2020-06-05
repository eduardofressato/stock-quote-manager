package com.fressato.stockquotemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fressato.stockquotemanager.model.Quote;


@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long>{

}
