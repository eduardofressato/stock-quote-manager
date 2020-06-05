package com.fressato.stockquotemanager.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fressato.stockquotemanager.exception.NotAllowedToCreateException;
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
	
	private String BASE_API = "http://localhost:8080/stock";
	
	/**
	 * Get All Stock Quotes
	 * @return
	 */
    @GetMapping("/stock-quotes")
    public List<StockQuote> getAllQuotes() {
        return stockQuoteRepository.findAll();
    }
    
    /**
     * Get a Single Stock Quote
     * @param stockQuoteId
     * @return
     */
    @GetMapping("/stock-quotes/{id}")
    public StockQuote getStockNoteById(@PathVariable(value = "id") String stockQuoteId) {
        return stockQuoteRepository.findById(stockQuoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock Quote", "id", stockQuoteId));
    }
    
    /**
     * Check if the id exists in the external API
     * @param id
     * @return
     */
    private Boolean checkStockQuoteExists(String id) {
    	RestTemplate restTemplate = new RestTemplate();
    	ResponseEntity<StockQuote[]> response = restTemplate.getForEntity(BASE_API, StockQuote[].class);
    	StockQuote[] stockQuotes = response.getBody();
    	    	
    	for (int i = 0; i < stockQuotes.length; i++) {
    		if (id.equals(stockQuotes[i].getId())) {
    			return true;
    		}
		}
    	
    	return false;
    }
    
    /**
     * Create a Stock Quote
     * @param body
     * @return
     */
    @PostMapping("/stock-quotes")
    public StockQuote createStockQuote(@RequestBody String body) {
    	
    	List<Quote> quotes = new ArrayList<Quote>();    	 
    	JsonObject jsonObject = new JsonParser().parse(body).getAsJsonObject();
    	Set<String> days = jsonObject.get("quotes").getAsJsonObject().keySet();
    	String id = jsonObject.get("id").getAsString();
    	    	
    	System.out.println("\n createStockQuote body:");
    	System.out.println(""+jsonObject);
    	
    	// check external API
    	if (checkStockQuoteExists(id) == false) {    		
    		throw new NotAllowedToCreateException();
    	}

    	for(Iterator<String> iter = days.iterator(); iter.hasNext();) {
    	    String day = iter.next();
    	    String value = jsonObject.get("quotes").getAsJsonObject().get(day).getAsString();

    	    Quote q = new Quote(day, value);
    	    Quote qSaved = quoteRepository.save(q);
    	    
    	    quotes.add(qSaved);
    	}

    	StockQuote sq = new StockQuote(id, id, quotes);
    	StockQuote sqCreated = stockQuoteRepository.save(sq);
    	
        return sqCreated;
    }
}
