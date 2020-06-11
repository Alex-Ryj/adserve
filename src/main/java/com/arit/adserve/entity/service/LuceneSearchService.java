package com.arit.adserve.entity.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Alex Ryjoukhine
 * @since Jun 9, 2020
 */
@Service
@Slf4j
public class LuceneSearchService {
	
	   private Directory indexStore  = new RAMDirectory();	  
	   
	   /**
	     * 
	     * @param title
	     * @param body
	     */
	    public void indexDocument(Document doc, Analyzer analyzer) {

	        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
	        try {
	            IndexWriter writter = new IndexWriter(indexStore, indexWriterConfig);  
	            writter.addDocument(doc);
	            writter.close();
	        } catch (IOException e) {
	            log.error("index document", e);
	        }
	    }
	    
	    public void updateDocument(Document doc, Analyzer analyzer) {
	    	IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
	        try {
	            IndexWriter writter = new IndexWriter(indexStore, indexWriterConfig); 
	            Term term = new Term("id", doc.get("id"));
	            writter.updateDocument(term, doc);
	            writter.close();
	        } catch (IOException e) {
	            log.error("update document", e);
	        }
	    }

	    public List<Document> searchIndex(String inField, String queryString, Analyzer analyzer) {
	        try {
	            Query query = new QueryParser(inField, analyzer).parse(queryString);

	            IndexReader indexReader = DirectoryReader.open(indexStore);
	            IndexSearcher searcher = new IndexSearcher(indexReader);
	            TopDocs topDocs = searcher.search(query, 10);
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
	            IndexWriter writter = new IndexWriter(indexStore, indexWriterConfig);
	            writter.deleteDocuments(term);
	            writter.close();
	        } catch (IOException e) {
	        	log.error("delete document", e);
	        }
	    }

	    public List<Document> searchIndex(Query query, int numOfDocs) {
	        try {
	            IndexReader indexReader = DirectoryReader.open(indexStore);
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

	    public List<Document> searchIndex(Query query, Sort sort) {
	        try {
	            IndexReader indexReader = DirectoryReader.open(indexStore);
	            IndexSearcher searcher = new IndexSearcher(indexReader);
	            TopDocs topDocs = searcher.search(query, 10, sort);
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
