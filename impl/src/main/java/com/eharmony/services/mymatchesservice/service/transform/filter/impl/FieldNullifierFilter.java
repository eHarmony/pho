package com.eharmony.services.mymatchesservice.service.transform.filter.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.eharmony.services.mymatchesservice.rest.SingleMatchRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.IMatchTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;

public class FieldNullifierFilter implements IMatchTransformer{

    private List<String> list;
    private String sectionName;

	private static final Logger logger = LoggerFactory.getLogger(FieldNullifierFilter.class);

    public FieldNullifierFilter(String sectionName, List<String> fields){
        Assert.hasText(sectionName, "sectionName parameter cannot be blank");
        Assert.notEmpty(fields, "list parameter cannot be empty");

        this.list = fields;
        this.sectionName = sectionName;
    }

	@Override
	public SingleMatchRequestContext processSingleMatch(
			SingleMatchRequestContext context) {

		Map<String, Map<String, Object>> match = context.getSingleMatch();
		Map<String, Object> matchSection = match.get(MatchFeedModel.SECTIONS.MATCH);
		
        if (matchSection == null) {
        	logger.warn("match info section={} is null for match={}",
                sectionName, context.getQueryContext().getMatchId());

            return context; // the whole section is not present, hence cleaning is success
        }
        
        for (String fieldName : list) {
        	Object value = matchSection.get(fieldName);
        	if(isZero(value)){
        		matchSection.put(fieldName, null);
        	}
        }

        logger.debug("nullified fields from MatchInfo for userId={}, section={}",
        				context.getQueryContext(), sectionName);

        return context;
	}

	private static boolean isZero(Object number) {
	    if (number instanceof BigDecimal) {
	        return BigDecimal.ZERO.equals(number);
	    } else if (number instanceof BigInteger) {
	        return BigInteger.ZERO.equals(number);
	    } else if (number instanceof Byte) {
	        return new Byte((byte) 0).equals(number);
	    } else if (number instanceof Double) {
	        return new Double(0).equals(number);
	    } else if (number instanceof Float) {
	        return new Float(0).equals(number);
	    } else if (number instanceof Integer) {
	        return new Integer(0).equals(number);
	    } else if (number instanceof Long) {
	        return new Long(0).equals(number);
	    } else if (number instanceof Short) {
	        return new Short((short) 0).equals(number);
	    }
	    return false;
	}
}
