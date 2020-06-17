package com.arit.adserve.entity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedNumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortedNumericSortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.RAMDirectory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LuceneSearchServiceTest {

	Analyzer analyzer;
	LuceneSearchService service;
	Document doc;		

	@BeforeEach
	public void setUp() {
		analyzer = new StandardAnalyzer();
		service = spy(new LuceneSearchService());
		when(service.getIndexStore()).thenReturn(new  RAMDirectory());
		doc = new Document();
		doc.add(new TextField("body", "Hello world", Field.Store.YES));
	}

	@Test
	void testIndexDocument() {		
		service.indexDocument(doc, analyzer);
		List<Document> documents = service.searchIndex("body", "world", analyzer, 10);
		assertEquals("Hello world", documents.get(0).get("body"));
	}

	@Test
	void testSearchIndexStringStringAnalyzer() {
		service.indexDocument(doc, analyzer);
		Term term = new Term("body", "world");
		Query query = new TermQuery(term);
		List<Document> documents = service.searchIndex(query, 10);
		assertEquals("Hello world", documents.get(0).get("body"));
	}

	@Test
	void testDeleteDocument() {
		Document doc1 = new Document();
		doc1.add(new StoredField("id", "1"));
		doc1.add(new TextField("body", "Hello world", Field.Store.YES));
		doc1.add(new TextField("field", "other stuff", Field.Store.YES));
		service.indexDocument(doc, analyzer);
		service.indexDocument(doc1, analyzer);
		Term term = new Term("body", "world");
		Query query = new TermQuery(term);
		List<Document> documents = service.searchIndex(query, 10);
		assertEquals(2, documents.size());
		Term term2 = new Term("field", "stuff");
		Query query2 = new TermQuery(term2);
		documents = service.searchIndex(query2, 10);
		assertEquals(1, documents.size());
		service.deleteDocument(term2, analyzer);
		documents = service.searchIndex(query, 10);
		assertEquals(1, documents.size());		
	}

	@Test
	void testSearchIndexQuery() {
		Document doc1 = new Document();
		doc1.add(new StoredField("id", "1"));
		doc1.add(new TextField("body", "Hello world", Field.Store.YES));
		doc1.add(new TextField("field", "other stuff", Field.Store.YES));
		doc1.add(new SortedNumericDocValuesField("int", 1));
		doc.add(new SortedNumericDocValuesField("int", 2));
		service.indexDocument(doc, analyzer);
		service.indexDocument(doc1, analyzer);
		Query query = new TermQuery(new Term("field", "stuff"));
		List<Document> documents = service.searchIndex(query, 10);
		assertEquals(1, documents.size());		
	}

	@Test
	void testSearchIndexQuerySort() {
		Document doc1 = new Document();
		doc1.add(new StoredField("id", "1"));
		doc1.add(new TextField("body", "Hello world", Field.Store.YES));
		doc1.add(new TextField("field", "other stuff", Field.Store.YES));
		doc1.add(new SortedNumericDocValuesField("int", 1));
		Document doc2 = new Document();
		doc2.add(new StoredField("id", "2"));
		doc2.add(new TextField("body", "Hello world", Field.Store.YES));
		doc2.add(new TextField("field", "other stuff", Field.Store.YES));
		doc2.add(new SortedNumericDocValuesField("int", 1));
		doc.add(new SortedNumericDocValuesField("int", 3));
		service.indexDocument(doc, analyzer);
		service.indexDocument(doc1, analyzer);
		service.indexDocument(doc2, analyzer);
		Query query = new TermQuery(new Term("body", "hello"));
		List<Document> documents = service.searchIndex(query, new Sort(new SortField[] {				
				new SortedNumericSortField("int", SortField.Type.INT) }), 10);
		assertTrue(StringUtils.isNotEmpty(documents.get(0).get("id")));
		assertTrue(StringUtils.isNotEmpty(documents.get(1).get("id")));
		assertTrue(StringUtils.isEmpty(documents.get(2).get("id")));
	}
	
	@Test
	void testSearchIndexQueryParser() throws Exception {
		Document doc1 = new Document();
		doc1.add(new StoredField("id", "1"));
		doc1.add(new TextField("body", "Hello world", Field.Store.YES));
		doc1.add(new TextField("field", "other stuff", Field.Store.YES));
		doc1.add(new SortedNumericDocValuesField("int", 1));
		Document doc2 = new Document();
		doc2.add(new StoredField("id", "2"));
		doc2.add(new TextField("body", "Hello green world ", Field.Store.YES));
		doc2.add(new TextField("field", "other stuff relveant", Field.Store.YES));
		doc2.add(new SortedNumericDocValuesField("int", 1));
		doc.add(new SortedNumericDocValuesField("int", 3));
		service.indexDocument(doc, analyzer);
		service.indexDocument(doc1, analyzer);
		service.indexDocument(doc2, analyzer);		
		List<Document> documents = service.searchWildcard("body", "field:stu* body:wor*", Sort.RELEVANCE, 10, analyzer);
		assertEquals(3, documents.size());
		documents = service.searchWildcard("field", "the* rel*", Sort.RELEVANCE, 10, analyzer);
		assertEquals(1, documents.size());
	}

}
