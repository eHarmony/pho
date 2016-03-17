package com.eharmony.services.mymatchesservice.service.transform.enrich.impl.nextcommaction;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stages of Communication
 * This is for V2 of the communication flow
 * 
 * @author aricheimer
 *
 */
public enum CommunicationStageEnum {
        
    UNKNOWN(0),
    NEW_MATCH(27),
    INITIALIZER_CHOOSES_CLOSED_ENDED(28),
    NON_INITIALIZER_READS_MATCH_DETAIL(29),
    NON_INITIALIZER_ANSWERS_CLOSED_ENDED_QUESTION(30),
    NON_INITIALIZER_CHOOSES_CLOSED_ENDED_QUESTION(31),
    INITIALIZER_READS_CLOSED_ENDED_ANSWERS(32),
    INITIALIZER_ANSWERS_CLOSED_ENDED_QUESTION(33),
    NON_INITIALIZER_READS_CLOSED_ENDED_ANSWERS(34),
    NON_INITIALIZER_CHOOSES_MUST_HAVE_CANT_STANDS(35),
    INITIALIZER_READS_MUST_HAVE_CANT_STANDS(36),
    INITIALIZER_CHOOSES_MUST_HAVE_CANT_STANDS(37),
    NON_INITIALIZER_READS_MUST_HAVE_CANT_STANDS(38),
    NON_INITIALIZER_CHOOSES_OPEN_ENDED_QUESTIONS(39),
    INITIALIZER_ANSWERS_OPEN_ENDED_QUESTIONS(40),
    INITIALIZER_CHOOSES_OPEN_ENDED_QUESTIONS(41),
    NON_INITIALIZER_READS_OPEN_ENDED_QUESTION_ANSWERS(42),
    NON_INITIALIZER_ANSWERS_OPEN_ENDED_QUESTIONS(43),
    INITIALIZER_READS_OPEN_ENDED_QUESTION_ANSWERS(44),
    INITIALIZER_READS_OPEN_COMM_PRIMER(45),
    INITIALIZER_WRITES_1ST_OPEN_COMM_MESSAGE(46),
    NON_INITIALIZER_READS_OPEN_COMM_PRIMER(47),
    NON_INITIALIZER_READS_1ST_OPEN_COMM_MESSAGE(48),
    NON_INITIALIZER_RESPONDS_TO_1ST_OPEN_COMM_MESSAGE(49),
    OPEN_COMM_REACHED(50);

    private int code;
    private static Map<Integer, CommunicationStageEnum> codeToEnumMap = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(CommunicationStageEnum.class);

    static {
        for(CommunicationStageEnum enumValue: values()){
            if(codeToEnumMap.containsKey(enumValue.getCode())){
                log.error("Enum value {} is invalid because code {} is already in use by {}", enumValue, enumValue.getCode(), codeToEnumMap.get(enumValue.getCode()));
                throw new Error("Multiple enum values have the same code ID.");
            }
            codeToEnumMap.put(enumValue.getCode(), enumValue);
        }
    }

    private CommunicationStageEnum(int code){

        this.code = code;

    }

    public static CommunicationStageEnum fromCode(int code){
        try {
            CommunicationStageEnum enumValue = codeToEnumMap.get(code);
            if(enumValue == null){
                return UNKNOWN;
            }
            return enumValue;
        } catch (Exception e) {
            log.error("Unknown communication stage code {}", code, e);
            return UNKNOWN;
        }
    }

    public int getCode() {

        return code;

    }

}
