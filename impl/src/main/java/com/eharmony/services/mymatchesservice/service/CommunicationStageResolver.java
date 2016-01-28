package com.eharmony.services.mymatchesservice.service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Builds communication stage objects map from comm work flow table during context start time and provides lookup method
 * to retrive the Communication Stage by given workflow Id
 * 
 * @author vvangapandu
 *
 */
@Component
public class CommunicationStageResolver {

    private static final Logger log = LoggerFactory.getLogger(CommunicationStageResolver.class);
    private static final int DEFAULT_WORKFLOW_ID = 27;
    private final ImmutableMap<Integer, CommunicationStage> commWorkFlow;
    private static final String COMM_WORKFLOW_FILE_PATH = "/mymatchesservice/comm_workflow.csv";
    // the fields to bind do in your JavaBean
    private static final String[] COMM_STAGE_COLUMNS = new String[] { "workFlowIdOld", "sectionId", "subSectionId",
            "turnOwener", "workflowId", "stageDescription", "requiresEmailOnCompletion", "legacyMatchStage", "version",
            "actionPath", "nonOwnerPath", "reviewPath", "versionSequence", "excemptionRule" };

    public CommunicationStageResolver() throws FileNotFoundException {
        InputStream is = getClass().getResourceAsStream(COMM_WORKFLOW_FILE_PATH);
        CSVReader reader = new CSVReader(new InputStreamReader(is));
        ColumnPositionMappingStrategy<CommunicationStage> strat = new ColumnPositionMappingStrategy<CommunicationStage>();
        strat.setType(CommunicationStage.class);
        strat.setColumnMapping(COMM_STAGE_COLUMNS);

        CsvToBean<CommunicationStage> csv = new CsvToBean<CommunicationStage>();
        List<CommunicationStage> list = csv.parse(strat, reader);
        Builder<Integer, CommunicationStage> commWorkFlowBuilder = ImmutableMap.<Integer, CommunicationStage> builder();
        list.forEach(a -> {
            commWorkFlowBuilder.put(a.getWorkflowId(), a);
        });
        commWorkFlow = commWorkFlowBuilder.build();

    }

    public CommunicationStage resolveCommStage(int workFlowId) {
        if (workFlowId == 0) {
            log.info("workflow id is 0 and returning default workflow");
            return commWorkFlow.get(DEFAULT_WORKFLOW_ID);
        }

        if(log.isDebugEnabled()){
        	log.debug("returning communication stage for workflowid {}", workFlowId);
        }
        return commWorkFlow.get(workFlowId);
    }

}
