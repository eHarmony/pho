package com.eharmony.services.mymatchesservice.service;

public class CommunicationStage {
    private int workFlowIdOld;
    private int sectionId;
    private int subSectionId;
    private int turnOwener;
    private int workflowId;
    private String stageDescription;
    private int requiresEmailOnCompletion;
    private float legacyMatchStage;
    private int version;
    private String actionPath;
    private String nonOwnerPath;
    private String reviewPath;
    private String versionSequence;
    private String excemptionRule;

    public CommunicationStage() {
    }

    public int getWorkFlowIdOld() {
        return workFlowIdOld;
    }

    public void setWorkFlowIdOld(int workFlowIdOld) {
        this.workFlowIdOld = workFlowIdOld;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public int getSubSectionId() {
        return subSectionId;
    }

    public void setSubSectionId(int subSectionId) {
        this.subSectionId = subSectionId;
    }

    public int getTurnOwener() {
        return turnOwener;
    }

    public void setTurnOwener(int turnOwener) {
        this.turnOwener = turnOwener;
    }

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

    public String getStageDescription() {
        return stageDescription;
    }

    public void setStageDescription(String stageDescription) {
        this.stageDescription = stageDescription;
    }

    public int getRequiresEmailOnCompletion() {
        return requiresEmailOnCompletion;
    }

    public void setRequiresEmailOnCompletion(int requiresEmailOnCompletion) {
        this.requiresEmailOnCompletion = requiresEmailOnCompletion;
    }

    public float getLegacyMatchStage() {
        return legacyMatchStage;
    }

    public void setLegacyMatchStage(float legacyMatchStage) {
        this.legacyMatchStage = legacyMatchStage;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getActionPath() {
        return actionPath;
    }

    public void setActionPath(String actionPath) {
        this.actionPath = actionPath;
    }

    public String getNonOwnerPath() {
        return nonOwnerPath;
    }

    public void setNonOwnerPath(String nonOwnerPath) {
        this.nonOwnerPath = nonOwnerPath;
    }

    public String getReviewPath() {
        return reviewPath;
    }

    public void setReviewPath(String reviewPath) {
        this.reviewPath = reviewPath;
    }

    public String getVersionSequence() {
        return versionSequence;
    }

    public void setVersionSequence(String versionSequence) {
        this.versionSequence = versionSequence;
    }

    public String getExcemptionRule() {
        return excemptionRule;
    }

    public void setExcemptionRule(String excemptionRule) {
        this.excemptionRule = excemptionRule;
    }

}
