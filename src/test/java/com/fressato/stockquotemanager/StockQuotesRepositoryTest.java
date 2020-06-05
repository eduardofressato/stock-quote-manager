package com.fressato.stockquotemanager;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fressato.stockquotemanager.model.Quote;
import com.fressato.stockquotemanager.model.StockQuote;
import com.fressato.stockquotemanager.repository.QuoteRepository;
import com.fressato.stockquotemanager.repository.StockQuoteRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StockQuotesRepositoryTest {

	@Autowired
	QuoteRepository quoteRepository;
	@Autowired
	StockQuoteRepository stockQuoteRepository;
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void createStockQuotesPersistData() {
		
		List<Quote> quotes = new ArrayList<Quote>();    
		
	    Quote q = new Quote("2019-01-01", "10");
	    Quote qSaved = this.quoteRepository.save(q);
	    quotes.add(q);
	    
	    Quote q1 = new Quote("2019-01-02", "11");
	    Quote qSaved2 = this.quoteRepository.save(q1);
	    quotes.add(q1);
	    
	    Quote q3 = new Quote("2019-01-03", "122");
	    Quote qSaved3 = this.quoteRepository.save(q3);
	    quotes.add(q3);
	    
    	StockQuote sq = new StockQuote("petr7", "test petr", quotes);
    	StockQuote sqCreated = stockQuoteRepository.save(sq);

		Assertions.assertThat(sqCreated.getId()).isEqualTo("petr7");
		Assertions.assertThat(sqCreated.getDescription()).isEqualTo("test petr");
		Assertions.assertThat(sqCreated.getQuotes()).asList();
		Assertions.assertThat(sqCreated.getQuotes()).isEqualTo(quotes);
	}
	
}
