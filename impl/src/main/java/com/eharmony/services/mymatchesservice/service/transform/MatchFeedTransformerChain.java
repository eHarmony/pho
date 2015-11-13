package com.eharmony.services.mymatchesservice.service.transform;

import java.util.ArrayList;
import java.util.List;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;

public class MatchFeedTransformerChain {

	private List<IMatchFeedTransformer> transformers = new ArrayList<IMatchFeedTransformer>();
	
	public MatchFeedTransformerChain(final List<IMatchFeedTransformer> tx){
		this.transformers = tx;
	}
	
	public MatchFeedRequestContext execute(MatchFeedRequestContext context){
		
		for(IMatchFeedTransformer filter : transformers){
			
			filter.processMatchFeed(context);
		}
		
		return context;
	}
	
	public void addTransformer(IMatchFeedTransformer transformer){
		transformers.add(transformer);
	}
	
}
