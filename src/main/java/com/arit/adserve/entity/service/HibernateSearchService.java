package com.arit.adserve.entity.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arit.adserve.entity.Item;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Alex Ryjoukhine
 * @since May 20, 2020
 */
@Slf4j
@Service
public class HibernateSearchService {
	
	private final EntityManager entityManager;

    @Autowired
    public HibernateSearchService(final EntityManagerFactory entityManagerFactory) {
        super();
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @PostConstruct
    public void initializeHibernateSearch() {

        try {
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            log.error(null, e);
            Thread.currentThread().interrupt();
        }
    }
    
    @Transactional
    public List<Item> findItems(String searchTerm) {
    	 FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
         QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Item.class).get();
         Query luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(0).onFields("title")
                 .matching(searchTerm).createQuery();

         javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Item.class);

         List<Item> itemsList = null;
         try {
        	 itemsList = jpaQuery.getResultList();
         } catch (NoResultException nre) {
             log.warn("no search result");
         }
         return itemsList;
    }
}
