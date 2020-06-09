package com.arit.adserve.entity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class LuceneSearchServiceTest {

	static Analyzer analyzer;
	static LuceneSearchService service;

	@BeforeAll
	public static void setUp() {
		analyzer = new StandardAnalyzer();
		service = new LuceneSearchService();
	}

	@Test
	void testIndexDocument() {
		service.indexDocument("Hello world", "Some hello world ", analyzer);
		List<Document> documents = service.searchIndex("body", "world", analyzer);
		assertEquals("Hello world", documents.get(0).get("title"));
	}

	@Test
	void testSearchIndexStringStringAnalyzer() {
		service.indexDocument("Hello world", "Some hello world ", analyzer);
		Term term = new Term("body", "world");
		Query query = new TermQuery(term);
		List<Document> documents = service.searchIndex(query);
		assertEquals("Hello world", documents.get(0).get("title"));
	}

	@Test
	void testDeleteDocument() {
		fail("Not yet implemented");
	}

	@Test
	void testSearchIndexQuery() {
		fail("Not yet implemented");
	}

	@Test
	void testSearchIndexQuerySort() {
		fail("Not yet implemented");
	}

}
