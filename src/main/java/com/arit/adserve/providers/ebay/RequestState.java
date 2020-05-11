package com.arit.adserve.providers.ebay;

/**
 * States of a find request object
 * 
 * @author Alex Ryjoukhine
 * @since May 11, 2020
 * 
 */
public enum RequestState {
    /**
     * indicates that items still need to be retrieved 
     */
    RETRIEVE_ITEMS,
    /**enough items are retrieved so update the state of existing items
     * 
     */
    UPDATE_ITEMS,
    /**
     * nor enough items are retrieved with current search words so change the search words
     */
    CHANGE_SEARCH,    
    /**
     * no retrieval or update of items is required for ther moment
     */
    WAIT
}