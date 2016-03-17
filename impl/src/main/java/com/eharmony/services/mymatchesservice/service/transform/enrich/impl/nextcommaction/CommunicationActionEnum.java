package com.eharmony.services.mymatchesservice.service.transform.enrich.impl.nextcommaction;

/**
 * The types of action the user can take to advance communication with another user
 * 
 * @author aricheimer
 *
 */
public enum CommunicationActionEnum {
    
    /**
     * The user can view the action area
     */
    VIEW,
    
    /**
     * The user can send a communication
     */
    SEND,
    
    /**
     * The user can wait for the other user
     */
    WAIT,
    
    /**
     * The user can close the match
     */
    CLOSE,
    
    /**
     * The user can re-open the match
     */
    REOPEN,
    
    /**
     * The user cannot do anything because the match is closed
     */
    NO_ACTION;
}
