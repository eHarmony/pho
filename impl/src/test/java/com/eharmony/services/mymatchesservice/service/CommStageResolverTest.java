package com.eharmony.services.mymatchesservice.service;

import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.Test;


public class CommStageResolverTest {
	
	@Test
	public void testWorkFlowIdTwentySeven() throws FileNotFoundException{
		CommunicationStageResolver communicationStageResolver=new CommunicationStageResolver();	
		CommunicationStage communicationStage=communicationStageResolver.resolveCommStage(27);
		Assert.assertEquals(0, communicationStage.getSectionId());
		Assert.assertEquals(0, communicationStage.getSubSectionId());

	}

	@Test
	public void testWorkFlowIdTwentyEight() throws FileNotFoundException{
		CommunicationStageResolver communicationStageResolver=new CommunicationStageResolver();	
		CommunicationStage communicationStage=communicationStageResolver.resolveCommStage(28);
		Assert.assertEquals(1, communicationStage.getSectionId());
		Assert.assertEquals(1, communicationStage.getSubSectionId());

	}
	
	
}
