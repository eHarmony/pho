package com.eharmony.services.mymatchesservice.service.transform;

import java.util.ArrayList;
import java.util.List;

import com.eharmony.services.mymatchesservice.rest.SingleMatchRequestContext;

public class SingleMatchTransformerChain {

	private List<IMatchTransformer> transformers = new ArrayList<IMatchTransformer>();
	
	public SingleMatchTransformerChain(final List<IMatchTransformer> tx){
		this.transformers = tx;
	}
	
	public SingleMatchRequestContext execute(SingleMatchRequestContext context){
		
		for(IMatchTransformer filter : transformers){
			
			filter.processSingleMatch(context);
			if(context.getSingleMatch() == null){
				break;
			}
		}
		
		return context;
	}
	
	public void addTransformer(IMatchTransformer transformer){
		transformers.add(transformer);
	}
}
