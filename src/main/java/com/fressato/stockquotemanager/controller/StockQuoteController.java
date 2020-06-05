package com.fressato.stockquotemanager.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
	
	private String BASE_API = "http://localhost:8080/";
	private String HOST = "localhost";
	private String PORT = "8081";
	
	private StockQuote[] cacheStock;
	
	public StockQuoteController() {
		this.registerNotification();
	}
	
	private void registerNotification() {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{\"host\": \""+ this.HOST +"\", \"port\": \"" + this.PORT + "\" }";
		
		System.out.println("\n REGISTER NOTIFICATION: " + requestJson);

		HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
		ResponseEntity<String> response = restTemplate.exchange(this.BASE_API + "notification", HttpMethod.POST, entity, String.class);

		System.out.println("\n REGISTER NOTIFICATION RESPONSE: " + response);
	}
	
    /**
     * Download stocks and cache
     * @return
     */
    private StockQuote[] downloadStocks() {
    	System.out.println("\n DOWNLOAD STOCKS");
    	
    	RestTemplate restTemplate = new RestTemplate();
    	ResponseEntity<StockQuote[]> response = restTemplate.getForEntity(this.BASE_API + "stock", StockQuote[].class);
    	StockQuote[] stockQuotes = response.getBody();
    	
    	return stockQuotes;
    }
    
    /**
     * Check if the id exists in cache Stocks
     * @param id
     * @return
     */
    private StockQuote checkStockQuoteExists(String id) {
    	if (cacheStock == null) {
    		cacheStock = downloadStocks();
    	}

    	for (int i = 0; i < cacheStock.length; i++) {
    		if (id.equals(cacheStock[i].getId())) {
    			return cacheStock[i];
    		}
		}
    	
    	return null;
    }
	
	/**
	 * Get All Stock Quotes
	 * @return
	 */
    @GetMapping("/stockquotes")
    public List<StockQuote> getAllQuotes() {
        return stockQuoteRepository.findAll();
    }
    
    /**
     * Get a Single Stock Quote
     * @param stockQuoteId
     * @return
     */
    @GetMapping("/stockquotes/{id}")
    public StockQuote getStockNoteById(@PathVariable(value = "id") String stockQuoteId) {
        return stockQuoteRepository.findById(stockQuoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock Quote", "id", stockQuoteId));
    } 
    
    /**
     * Create a Stock Quote
     * @param body
     * @return
     */
    @PostMapping("/stockquotes")
    public StockQuote createStockQuote(@RequestBody String body) {
    	
    	List<Quote> quotes = new ArrayList<Quote>();    	 
    	JsonObject jsonObject = new JsonParser().parse(body).getAsJsonObject();
    	Set<String> days = jsonObject.get("quotes").getAsJsonObject().keySet();
    	String id = jsonObject.get("id").getAsString();
    	    	
    	System.out.println("\n CREATE STOCK QUOTES: " + jsonObject);
    	
    	// check external API
    	StockQuote stockAPI = checkStockQuoteExists(id);
    	if (stockAPI == null) {    		
    		throw new NotAllowedToCreateException();
    	}

    	for(Iterator<String> iter = days.iterator(); iter.hasNext();) {
    	    String day = iter.next();
    	    String value = jsonObject.get("quotes").getAsJsonObject().get(day).getAsString();

    	    Quote q = new Quote(day, value);
    	    Quote qSaved = quoteRepository.save(q);
    	    
    	    quotes.add(qSaved);
    	}

    	StockQuote sq = new StockQuote(id, stockAPI.getDescription(), quotes);
    	StockQuote sqCreated = stockQuoteRepository.save(sq);
    	
        return sqCreated;
    }
    
	/**
	 * Delete stocks cache
	 * @return
	 */
    @DeleteMapping("/stockcache")
    public String deleteCache() {
    	System.out.println("\n DELETE CACHE");

    	cacheStock = null;   	
        return "Cache successfully deleted.";
    }
}
