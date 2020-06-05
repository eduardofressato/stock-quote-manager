package com.fressato.stockquotemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fressato.stockquotemanager.model.StockQuote;

@Repository
public interface StockQuoteRepository extends JpaRepository<StockQuote, String> {

}
