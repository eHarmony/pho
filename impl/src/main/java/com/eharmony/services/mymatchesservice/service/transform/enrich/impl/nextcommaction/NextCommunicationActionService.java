package com.eharmony.services.mymatchesservice.service.transform.enrich.impl.nextcommaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.eharmony.singles.common.communication.MatchWrapper;
import static com.eharmony.services.mymatchesservice.service.transform.enrich.impl.nextcommaction.CommunicationActionEnum.*;
import static com.eharmony.services.mymatchesservice.service.transform.enrich.impl.nextcommaction.CommunicationActionAreaEnum.*;
import static com.eharmony.services.mymatchesservice.service.transform.enrich.impl.nextcommaction.CommunicationStageEnum.*;

/**
 * Service to determine the next communication action for a user to take
 * 
 * @author aricheimer
 *
 */
@Component
public class NextCommunicationActionService {
    
    private static final Logger log = LoggerFactory.getLogger(NextCommunicationActionService.class);

    /**
     * Determine the next action a user needs to take to advance communication
     * for a given match
     * 
     * @param match
     *            the match the user is in
     * @return the next action a user should take to advance communication
     */
    public NextCommunicationAction determineNextCommunicationAction(
            MatchWrapper match) {

        NextCommunicationAction nextCommunicationAction = new NextCommunicationAction();

        Integer stage = match.getStage();
        CommunicationStageEnum communicationStage = CommunicationStageEnum
                .fromCode(stage);

        if (match.isOpen()) {

            /*
             * Messages for FastTrack (i.e. Skip to eH Mail)
             */
            if (match.isFastTrackRequestedByViewer()) {
                // Message 11
                // Waiting for {1} Response
                nextCommunicationAction.setAction(WAIT);
                nextCommunicationAction.setArea(FASTTRACK_DECISION);
                return nextCommunicationAction;
            }

            if (match.isFastTrackDeniedByMatch()) {
                // Message 12
                // Read {1} Message
                nextCommunicationAction.setAction(VIEW);
                nextCommunicationAction.setArea(FASTTRACK_DECISION);
                return nextCommunicationAction;
            }

            if (match.isFastTrackRequestedByMatch()) {
                // Message 13
                // Read {1} Message
                nextCommunicationAction.setAction(VIEW);
                nextCommunicationAction.setArea(FASTTRACK_REQUEST);
                return nextCommunicationAction;
            }

            /*
             * Messages for Open Communication, i.e. eH Mail
             */
            if (communicationStage == CommunicationStageEnum.OPEN_COMM_REACHED) {
                if (match.getNewMessageCount() > 0) {
                    // Message 6
                    // Respond to {1} Message
                    nextCommunicationAction.setAction(VIEW);
                    nextCommunicationAction.setArea(EH_MAIL);
                    return nextCommunicationAction;
                } else {
                    // Message 7
                    // Send {2} a Message
                    nextCommunicationAction.setAction(SEND);
                    nextCommunicationAction.setArea(EH_MAIL);
                    return nextCommunicationAction;
                }
            }
        }

        /*
         * Messages for closed matches
         */
        if (match.isCloseInitiatedByMatch()) {
            // Message 3
            // Close Match
            nextCommunicationAction.setAction(CLOSE);
            nextCommunicationAction.setArea(MATCH);
        }

        if (match.isClosedMatch()) {
            if (match.isReOpenAvailable()) {
                // Message 8
                // Re-Open Match
                nextCommunicationAction.setAction(REOPEN);
                nextCommunicationAction.setArea(MATCH);
                return nextCommunicationAction;
            } else {
                // Message 2
                // Match is closed
                nextCommunicationAction.setAction(NO_ACTION);
                nextCommunicationAction.setArea(MATCH);
            }
        }

        /*
         * Messages for guided communication
         */
        if (match.isOpenWithoutFasttrack()) {

            /*
             * Messages for very beginning of guided communication
             */
            if (communicationStage == null
                    || communicationStage == CommunicationStageEnum.UNKNOWN
                    || communicationStage == NEW_MATCH) {
                // Message 14
                // View {1} profile
                nextCommunicationAction.setAction(VIEW);
                nextCommunicationAction.setArea(MATCH);
                return nextCommunicationAction;
            }

            if (communicationStage == CommunicationStageEnum.INITIALIZER_CHOOSES_CLOSED_ENDED) {
                if (match.isViewedMatchProfile()) {
                    // Message 15
                    // Send {2} a Message
                    nextCommunicationAction.setAction(SEND);
                    nextCommunicationAction.setArea(QUICK_QUESTIONS_QUESTIONS);
                    return nextCommunicationAction;
                } else {
                    // Message 14
                    // View {1} profile
                    nextCommunicationAction.setAction(VIEW);
                    nextCommunicationAction.setArea(MATCH);
                    return nextCommunicationAction;
                }
            }

            /*
             * Messages for main part of guided communication
             */
            if (match.isViewerMatchInitializer()) {

                return determineNextCommunicationActionForInitializer(
                        communicationStage);

            } else {

                return determineNextCommunicationActionForNonInitializer(
                        communicationStage);

            }

        }

        return nextCommunicationAction;
    }

    private NextCommunicationAction determineNextCommunicationActionForInitializer(
            CommunicationStageEnum communicationStage) {

        NextCommunicationAction nextCommunicationAction = new NextCommunicationAction();

        if (communicationStage == NON_INITIALIZER_READS_MATCH_DETAIL) {
            // Message 17
            // Waiting for {1} Response
            nextCommunicationAction.setAction(WAIT);
            nextCommunicationAction.setArea(QUICK_QUESTIONS_ANSWERS);
            return nextCommunicationAction;
        }

        if (communicationStage == NON_INITIALIZER_ANSWERS_CLOSED_ENDED_QUESTION) {
            // Message 17
            // Waiting for {1} Response
            nextCommunicationAction.setAction(WAIT);
            nextCommunicationAction.setArea(QUICK_QUESTIONS_ANSWERS);
            return nextCommunicationAction;
        }

        if (communicationStage == CommunicationStageEnum.NON_INITIALIZER_CHOOSES_CLOSED_ENDED_QUESTION) {
            // Message 17
            // Waiting for {1} Response
            nextCommunicationAction.setAction(WAIT);
            nextCommunicationAction.setArea(QUICK_QUESTIONS_QUESTIONS);
            return nextCommunicationAction;
        }

        if (communicationStage == INITIALIZER_READS_CLOSED_ENDED_ANSWERS) {
            // Message 23
            // Read {1} Answers
            nextCommunicationAction.setAction(VIEW);
            nextCommunicationAction.setArea(QUICK_QUESTIONS_ANSWERS);
            return nextCommunicationAction;
        }

        if (communicationStage == CommunicationStageEnum.INITIALIZER_ANSWERS_CLOSED_ENDED_QUESTION) {
            // Message 25
            // Respond to {1} Questions
            nextCommunicationAction.setAction(SEND);
            nextCommunicationAction.setArea(QUICK_QUESTIONS_ANSWERS);
            return nextCommunicationAction;
        }

        if (communicationStage == CommunicationStageEnum.NON_INITIALIZER_READS_CLOSED_ENDED_ANSWERS) {
            // Message 27
            // Waiting for {1} Response
            nextCommunicationAction.setAction(WAIT);
            nextCommunicationAction.setArea(QUICK_QUESTIONS_ANSWERS);
            return nextCommunicationAction;
        }

        if (communicationStage == NON_INITIALIZER_CHOOSES_MUST_HAVE_CANT_STANDS) {
            // Message 29
            // Waiting for {1} Response
            nextCommunicationAction.setAction(WAIT);
            nextCommunicationAction.setArea(MUST_HAVE_CANT_STAND);
            return nextCommunicationAction;
        }

        if (communicationStage == INITIALIZER_READS_MUST_HAVE_CANT_STANDS) {
            // Message 26
            // Read {1} Message
            nextCommunicationAction.setAction(VIEW);
            nextCommunicationAction.setArea(MUST_HAVE_CANT_STAND);
            return nextCommunicationAction;
        }

        if (communicationStage == INITIALIZER_CHOOSES_MUST_HAVE_CANT_STANDS) {
            // Message 28
            // Send {2} a Message
            nextCommunicationAction.setAction(SEND);
            nextCommunicationAction.setArea(MUST_HAVE_CANT_STAND);
            return nextCommunicationAction;
        }

        if (communicationStage == CommunicationStageEnum.NON_INITIALIZER_READS_MUST_HAVE_CANT_STANDS) {
            // Message 5
            // Waiting for {1} Response
            nextCommunicationAction.setAction(WAIT);
            nextCommunicationAction.setArea(MUST_HAVE_CANT_STAND);
            return nextCommunicationAction;
        }

        if (communicationStage == NON_INITIALIZER_CHOOSES_OPEN_ENDED_QUESTIONS) {
            // Message 5
            // Waiting for {1} Response
            nextCommunicationAction.setAction(WAIT);
            nextCommunicationAction.setArea(DIG_DEEPER_QUESTIONS);
            return nextCommunicationAction;
        }

        if (communicationStage == CommunicationStageEnum.INITIALIZER_ANSWERS_OPEN_ENDED_QUESTIONS) {
            // Message 21
            // Respond to {1} Questions
            nextCommunicationAction.setAction(SEND);
            nextCommunicationAction.setArea(DIG_DEEPER_ANSWERS);
            return nextCommunicationAction;
        }

        if (communicationStage == CommunicationStageEnum.INITIALIZER_CHOOSES_OPEN_ENDED_QUESTIONS) {
            // Message 24
            // Send {2} your Questions
            nextCommunicationAction.setAction(SEND);
            nextCommunicationAction.setArea(DIG_DEEPER_QUESTIONS);
            return nextCommunicationAction;
        }

        if (communicationStage == NON_INITIALIZER_READS_OPEN_ENDED_QUESTION_ANSWERS) {
            // Message 17
            // Waiting for {1} Response
            nextCommunicationAction.setAction(WAIT);
            nextCommunicationAction.setArea(DIG_DEEPER_ANSWERS);
            return nextCommunicationAction;
        }

        if (communicationStage == NON_INITIALIZER_ANSWERS_OPEN_ENDED_QUESTIONS) {
            // Message 17
            // Waiting for {1} Response
            nextCommunicationAction.setAction(WAIT);
            nextCommunicationAction.setArea(DIG_DEEPER_ANSWERS);
            return nextCommunicationAction;
        }

        if (communicationStage == INITIALIZER_READS_OPEN_ENDED_QUESTION_ANSWERS) {
            // Message 23
            // Read {1} Answers
            nextCommunicationAction.setAction(VIEW);
            nextCommunicationAction.setArea(DIG_DEEPER_ANSWERS);
            return nextCommunicationAction;
        }

        if (communicationStage == INITIALIZER_READS_OPEN_COMM_PRIMER
                || communicationStage == CommunicationStageEnum.INITIALIZER_WRITES_1ST_OPEN_COMM_MESSAGE) {
            // Message 16
            // Send {2} a Message
            nextCommunicationAction.setAction(SEND);
            nextCommunicationAction.setArea(EH_MAIL);
            return nextCommunicationAction;
        }

        if (communicationStage == CommunicationStageEnum.NON_INITIALIZER_READS_OPEN_COMM_PRIMER) {
            // Message 10
            // Waiting for {1} Response
            nextCommunicationAction.setAction(WAIT);
            nextCommunicationAction.setArea(EH_MAIL);
            return nextCommunicationAction;
        }

        if (communicationStage == CommunicationStageEnum.NON_INITIALIZER_READS_1ST_OPEN_COMM_MESSAGE) {
            // Message 10
            // Waiting for {1} Response
            nextCommunicationAction.setAction(WAIT);
            nextCommunicationAction.setArea(EH_MAIL);
            return nextCommunicationAction;
        }

        if (communicationStage == CommunicationStageEnum.NON_INITIALIZER_RESPONDS_TO_1ST_OPEN_COMM_MESSAGE) {
            // Message 10
            // Waiting for {1} Response
            nextCommunicationAction.setAction(WAIT);
            nextCommunicationAction.setArea(EH_MAIL);
            return nextCommunicationAction;
        }

        log.warn("Unable to find communication action for communication initializer in stage {}", communicationStage);
        return nextCommunicationAction;
    }

    private NextCommunicationAction determineNextCommunicationActionForNonInitializer(
            CommunicationStageEnum communicationStage) {

        NextCommunicationAction nextCommunicationAction = new NextCommunicationAction();

        if (communicationStage == NON_INITIALIZER_READS_MATCH_DETAIL) {
            // Message 1
            // Respond to {1} Questions
            nextCommunicationAction.setAction(SEND);
            nextCommunicationAction.setArea(QUICK_QUESTIONS_ANSWERS);
            return nextCommunicationAction;
        }

        if (communicationStage == NON_INITIALIZER_ANSWERS_CLOSED_ENDED_QUESTION) {
            // Message 20
            // Respond to {1} Questions
            nextCommunicationAction.setAction(SEND);
            nextCommunicationAction.setArea(QUICK_QUESTIONS_ANSWERS);
            return nextCommunicationAction;
        }

        if (communicationStage == NON_INITIALIZER_CHOOSES_CLOSED_ENDED_QUESTION) {
            // Message 22
            // Send {2} a Message
            nextCommunicationAction.setAction(SEND);
            nextCommunicationAction.setArea(QUICK_QUESTIONS_QUESTIONS);
            return nextCommunicationAction;
        }

        if (communicationStage == INITIALIZER_READS_CLOSED_ENDED_ANSWERS) {
            // Message 17
            // Waiting for {1} Response
            nextCommunicationAction.setAction(WAIT);
            nextCommunicationAction.setArea(QUICK_QUESTIONS_ANSWERS);
            return nextCommunicationAction;
        }

        if (communicationStage == INITIALIZER_ANSWERS_CLOSED_ENDED_QUESTION) {
            // Message 17
            // Waiting for {1} Response
            nextCommunicationAction.setAction(WAIT);
            nextCommunicationAction.setArea(QUICK_QUESTIONS_ANSWERS);
            return nextCommunicationAction;
        }

        if (communicationStage == NON_INITIALIZER_READS_CLOSED_ENDED_ANSWERS) {
            // Message 23
            // Read {1} Answers
            nextCommunicationAction.setAction(VIEW);
            nextCommunicationAction.setArea(QUICK_QUESTIONS_ANSWERS);
            return nextCommunicationAction;
        }

        if (communicationStage == NON_INITIALIZER_CHOOSES_MUST_HAVE_CANT_STANDS) {
            // Message 28
            // Send {2} a Message
            nextCommunicationAction.setAction(SEND);
            nextCommunicationAction.setArea(MUST_HAVE_CANT_STAND);
            return nextCommunicationAction;
        }

        if (communicationStage == INITIALIZER_READS_MUST_HAVE_CANT_STANDS) {
            // Message 29
            // Waiting for {1} Response
            nextCommunicationAction.setAction(WAIT);
            nextCommunicationAction.setArea(MUST_HAVE_CANT_STAND);
            return nextCommunicationAction;
        }

        if (communicationStage == INITIALIZER_CHOOSES_MUST_HAVE_CANT_STANDS) {
            // Message 29
            // Waiting for {1} Response
            nextCommunicationAction.setAction(WAIT);
            nextCommunicationAction.setArea(MUST_HAVE_CANT_STAND);
            return nextCommunicationAction;
        }

        if (communicationStage == NON_INITIALIZER_READS_MUST_HAVE_CANT_STANDS) {
            // Message 26
            // Read {1} Message
            nextCommunicationAction.setAction(VIEW);
            nextCommunicationAction.setArea(MUST_HAVE_CANT_STAND);
            return nextCommunicationAction;
        }

        if (communicationStage == NON_INITIALIZER_CHOOSES_OPEN_ENDED_QUESTIONS) {
            // Message 24
            // Send {2} your Questions
            nextCommunicationAction.setAction(SEND);
            nextCommunicationAction.setArea(DIG_DEEPER_QUESTIONS);
            return nextCommunicationAction;
        }

        if (communicationStage == INITIALIZER_ANSWERS_OPEN_ENDED_QUESTIONS) {
            // Message 17
            // Waiting for {1} Response
            nextCommunicationAction.setAction(WAIT);
            nextCommunicationAction.setArea(DIG_DEEPER_ANSWERS);
            return nextCommunicationAction;
        }

        if (communicationStage == INITIALIZER_CHOOSES_OPEN_ENDED_QUESTIONS) {
            // Message 17
            // Waiting for {1} Response
            nextCommunicationAction.setAction(WAIT);
            nextCommunicationAction.setArea(DIG_DEEPER_QUESTIONS);
            return nextCommunicationAction;
        }

        if (communicationStage == NON_INITIALIZER_READS_OPEN_ENDED_QUESTION_ANSWERS) {
            // Message 23
            // Read {1} Answers
            nextCommunicationAction.setAction(VIEW);
            nextCommunicationAction.setArea(DIG_DEEPER_ANSWERS);
            return nextCommunicationAction;
        }

        if (communicationStage == NON_INITIALIZER_ANSWERS_OPEN_ENDED_QUESTIONS) {
            // Message 21
            // Respond to {1} Questions
            nextCommunicationAction.setAction(SEND);
            nextCommunicationAction.setArea(DIG_DEEPER_ANSWERS);
            return nextCommunicationAction;
        }

        if (communicationStage == INITIALIZER_READS_OPEN_ENDED_QUESTION_ANSWERS) {
            // Message 19
            // Waiting for {1} Response
            nextCommunicationAction.setAction(WAIT);
            nextCommunicationAction.setArea(DIG_DEEPER_ANSWERS);
            return nextCommunicationAction;
        }

        if (communicationStage == CommunicationStageEnum.INITIALIZER_READS_OPEN_COMM_PRIMER) {
            // Message 19
            // Waiting for {1} Response
            nextCommunicationAction.setAction(WAIT);
            nextCommunicationAction.setArea(EH_MAIL);
            return nextCommunicationAction;
        }

        if (communicationStage == INITIALIZER_WRITES_1ST_OPEN_COMM_MESSAGE) {
            // Message 19
            // Waiting for {1} Response
            nextCommunicationAction.setAction(WAIT);
            nextCommunicationAction.setArea(EH_MAIL);
            return nextCommunicationAction;
        }

        if (communicationStage == NON_INITIALIZER_READS_OPEN_COMM_PRIMER
                || communicationStage == CommunicationStageEnum.NON_INITIALIZER_READS_1ST_OPEN_COMM_MESSAGE) {
            // Message 9
            // Respond to {1} Message
            nextCommunicationAction.setAction(VIEW);
            nextCommunicationAction.setArea(EH_MAIL);
            return nextCommunicationAction;
        }

        if (communicationStage == NON_INITIALIZER_RESPONDS_TO_1ST_OPEN_COMM_MESSAGE) {
            // Message 4
            // Respond to {1} Message
            nextCommunicationAction.setAction(SEND);
            nextCommunicationAction.setArea(EH_MAIL);
            return nextCommunicationAction;
        }

        log.warn("Unable to find communication action for communication non-initializer in stage {}", communicationStage);
        return nextCommunicationAction;
    }
}
