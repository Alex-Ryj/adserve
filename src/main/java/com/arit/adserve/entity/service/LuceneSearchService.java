package com.arit.adserve.entity.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Alex Ryjoukhine
 * @since Jun 9, 2020
 */
@Service
@Slf4j
public class LuceneSearchService {

	@Value("${lucene.index.dir}")
	private String indexDir;

	private Directory indexStore;

	@PostConstruct
	private void setUp() throws IOException {
		indexStore = FSDirectory.open(Paths.get(indexDir));
	}
	
	/**
	 * this is to allow mocking the directory for tests
	 * @return Directory
	 */
	public Directory getIndexStore() {
		return indexStore;
	}

	/**
	 * 
	 * @param title
	 * @param body
	 */
	public void indexDocument(Document doc, Analyzer analyzer) {

   		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		try {
			IndexWriter writter = new IndexWriter(getIndexStore(), indexWriterConfig);
			writter.addDocument(doc);
			writter.close();
		} catch (IOException e) {
			log.error("index document", e);
		}
	}

	public void updateDocument(Document doc, Analyzer analyzer) {
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		try {
			IndexWriter writter = new IndexWriter(getIndexStore(), indexWriterConfig);
			Term term = new Term("id", doc.get("id"));
			writter.updateDocument(term, doc);
			writter.close();
		} catch (IOException e) {
			log.error("update document", e);
		}
	}

	public List<Document> searchIndex(String inField, String queryString, Analyzer analyzer, int numOfDocs) {
		try {
			Query query = new QueryParser(inField, analyzer).parse(queryString);
			IndexReader indexReader = DirectoryReader.open(getIndexStore());
			IndexSearcher searcher = new IndexSearcher(indexReader);
			TopDocs topDocs = searcher.search(query, numOfDocs);
			List<Document> documents = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				documents.add(searcher.doc(scoreDoc.doc));
			}
			return documents;
		} catch (IOException | ParseException e) {
			log.error("search index", e);
		}
		return Collections.emptyList();
	}

	public void deleteDocument(Term term, Analyzer analyzer) {
		try {
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			IndexWriter writter = new IndexWriter(getIndexStore(), indexWriterConfig);
			writter.deleteDocuments(term);
			writter.close();
		} catch (IOException e) {
			log.error("delete document", e);
		}
	}

	public List<Document> searchIndex(Query query, int numOfDocs) {
		try {
			IndexReader indexReader = DirectoryReader.open(getIndexStore());
			IndexSearcher searcher = new IndexSearcher(indexReader);
			TopDocs topDocs = searcher.search(query, numOfDocs);
			List<Document> documents = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				documents.add(searcher.doc(scoreDoc.doc));
			}

			return documents;
		} catch (IOException e) {
			log.error("search index", e);
		}
		return Collections.emptyList();
	}

	public List<Document> searchIndex(Query query, Sort sort, int numOfDocs) {
		try {
			IndexReader indexReader = DirectoryReader.open(getIndexStore());
			IndexSearcher searcher = new IndexSearcher(indexReader);
			TopDocs topDocs = searcher.search(query, numOfDocs, sort);
			List<Document> documents = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				documents.add(searcher.doc(scoreDoc.doc));
			}
			return documents;
		} catch (IOException e) {
			log.error("search index", e);
		}
		return Collections.emptyList();
	}
	
	public List<Document> searchWildcard(String field, String queryStr, Sort sort, int numOfDocs, Analyzer analyser) throws ParseException {
		try {
			IndexReader indexReader = DirectoryReader.open(getIndexStore());
			IndexSearcher searcher = new IndexSearcher(indexReader);			
			QueryParser qParser = new QueryParser(field, analyser);
			TopDocs topDocs = searcher.search(qParser.parse(queryStr), numOfDocs, sort);
			List<Document> documents = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				documents.add(searcher.doc(scoreDoc.doc));
			}
			return documents;
		} catch (IOException e) {
			log.error("search index", e);
		}
		return Collections.emptyList();
	}

}
