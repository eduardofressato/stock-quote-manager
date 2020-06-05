package com.fressato.stockquotemanager.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fressato.stockquotemanager.exception.ResourceNotFoundException;
import com.fressato.stockquotemanager.model.Quote;
import com.fressato.stockquotemanager.model.StockQuote;
import com.fressato.stockquotemanager.repository.QuoteRepository;
import com.fressato.stockquotemanager.repository.StockQuoteRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class StockQuoteController {
	
	@Autowired
	QuoteRepository quoteRepository;
	@Autowired
	StockQuoteRepository stockQuoteRepository;
	
    // Get All Stock Quotes
    @GetMapping("/stock-quotes")
    public List<StockQuote> getAllQuotes() {
        return stockQuoteRepository.findAll();
    }
    
    // Get a Single Stock Quote
    @GetMapping("/stock-quotes/{id}")
    public StockQuote getStockNoteById(@PathVariable(value = "id") String stockQuoteId) {
        return stockQuoteRepository.findById(stockQuoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock Quote", "id", stockQuoteId));
    }
    
    // Create a Stock Quote
    @PostMapping("/stock-quotes")
    public StockQuote createStockQuote(@RequestBody String body) {
    	
    	List<Quote> quotes = new ArrayList<Quote>();    	 
    	JsonObject jsonObject = new JsonParser().parse(body).getAsJsonObject();
    	Set<String> days = jsonObject.get("quotes").getAsJsonObject().keySet();
    	String id = jsonObject.get("id").getAsString();
    	
    	System.out.println("\n createStockQuote body:");
    	System.out.println(""+jsonObject);
    	
    	for(Iterator<String> iter = days.iterator(); iter.hasNext();) {
    	    String day = iter.next();
    	    String value = jsonObject.get("quotes").getAsJsonObject().get(day).getAsString();

    	    System.out.println("day: "+ day+ "- value: "+value);
    	    Quote q = new Quote(day, value);
    	    Quote qSaved = quoteRepository.save(q);
    	    
    	    quotes.add(qSaved);
    	}

    	StockQuote sq = new StockQuote(id, id, quotes);
    	StockQuote sqCreated = stockQuoteRepository.save(sq);
    	
        return sqCreated;
    }
}