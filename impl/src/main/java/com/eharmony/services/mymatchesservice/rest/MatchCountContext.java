package com.eharmony.services.mymatchesservice.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MatchCountContext {
    
    private int newCount;
    private int archived;
    private int openComm;
    private int myTurn;
    private int closed;
    private int all;
    private int theirTurn;
    
    @JsonProperty("new")
    public int getNewCount() {
        return newCount;
    }
    public void setNewCount(int newCount) {
        this.newCount = newCount;
    }
    public int getArchived() {
        return archived;
    }
    public void setArchived(int archived) {
        this.archived = archived;
    }
    public int getOpenComm() {
        return openComm;
    }
    public void setOpenComm(int openComm) {
        this.openComm = openComm;
    }
    public int getMyTurn() {
        return myTurn;
    }
    public void setMyTurn(int myTurn) {
        this.myTurn = myTurn;
    }
    public int getClosed() {
        return closed;
    }
    public void setClosed(int closed) {
        this.closed = closed;
    }
    public int getAll() {
        return all;
    }
    public void setAll(int all) {
        this.all = all;
    }
    public int getTheirTurn() {
        return theirTurn;
    }
    public void setTheirTurn(int theirTurn) {
        this.theirTurn = theirTurn;
    }

}
