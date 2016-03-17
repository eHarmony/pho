package com.eharmony.services.mymatchesservice.service.transform.enrich.impl.nextcommaction;

/**
 * The area in which another user can advance communication
 * @author aricheimer
 *
 */
public enum CommunicationActionAreaEnum {
    /**
     * A request to skip to eH Mail
     */
    FASTTRACK_REQUEST,
    
    /**
     * A response to a request to skip to eH Mail
     */
    FASTTRACK_DECISION,
    
    /**
     * A match of two users
     */
    MATCH,
    
    /**
     * The closed ended questions
     */
    QUICK_QUESTIONS_QUESTIONS,
    
    /**
     * The answers to closed ended questions
     */
    QUICK_QUESTIONS_ANSWERS,
    
    /**
     * A list of attributes about the user other user that would "make" and another list of attributes that would "break" a relationship for this user
     */
    MUST_HAVE_CANT_STAND,
    
    /**
     * A list of open ended questions
     */
    DIG_DEEPER_QUESTIONS,
    
    /**
     * Answers to the open ended questions
     */
    DIG_DEEPER_ANSWERS,
    
    /**
     * Open communication
     */
    EH_MAIL;
}
