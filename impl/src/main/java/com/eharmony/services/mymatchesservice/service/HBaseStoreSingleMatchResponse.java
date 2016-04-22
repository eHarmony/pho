package com.eharmony.services.mymatchesservice.service;

import com.eharmony.datastore.model.MatchDataFeedItemDto;

public class HBaseStoreSingleMatchResponse extends AbstractStoreFeedResponse {

	private MatchDataFeedItemDto hbaseStoreFeedItem;

	public MatchDataFeedItemDto getHbaseStoreFeedItem() {
		return hbaseStoreFeedItem;
	}

	public void setHbaseStoreFeedItem(MatchDataFeedItemDto hbaseStoreFeedItem) {
		this.hbaseStoreFeedItem = hbaseStoreFeedItem;
	}
}
