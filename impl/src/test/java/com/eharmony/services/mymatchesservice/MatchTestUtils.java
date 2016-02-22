package com.eharmony.services.mymatchesservice;

import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.util.CustomObjectMapper;

import org.apache.commons.io.IOUtils;

import org.springframework.core.io.ClassPathResource;


public class MatchTestUtils {
    private static final CustomObjectMapper mapper = new CustomObjectMapper();

    public static LegacyMatchDataFeedDto getTestFeed(String fileName)
        throws Exception {
        LegacyMatchDataFeedDto dto = null;

        String json = loadFileAsString(fileName);
        dto = mapper.readValue(json, LegacyMatchDataFeedDto.class);

        return dto;
    }

    public static String loadFileAsString(String filename) {
        try {
            return IOUtils.toString(new ClassPathResource(filename).getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
