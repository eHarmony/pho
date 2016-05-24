package com.eharmony.services.mymatchesservice.rest;

public class MatchFeedSearchAndFilterCriteriaBuilder {
    private String name;
    private String city;
    private String anyTextField;
    private Integer age;
    private Boolean hasPhotos;
    private Integer distance;
    
    public static MatchFeedSearchAndFilterCriteriaBuilder newInstance() {
        return new MatchFeedSearchAndFilterCriteriaBuilder();
    }

    public MatchFeedSearchAndFilterCriteriaBuilder setName(String name) {
        this.name = name;

        return this;
    }

    public MatchFeedSearchAndFilterCriteriaBuilder setCity(String city) {
        this.city = city;

        return this;
    }

    public MatchFeedSearchAndFilterCriteriaBuilder setAnyTextField(
        String anyTextField) {
        this.anyTextField = anyTextField;

        return this;
    }

    public MatchFeedSearchAndFilterCriteriaBuilder setAge(int age) {
        this.age = age;

        return this;
    }

    public MatchFeedSearchAndFilterCriteriaBuilder setHasPhotos(
        boolean hasPhotos) {
        this.hasPhotos = hasPhotos;

        return this;
    }

    public MatchFeedSearchAndFilterCriteriaBuilder setDistance(int distance) {
        this.distance = distance;

        return this;
    }
    
    public MatchFeedSearchAndFilterCriteria build(){
    	return new MatchFeedSearchAndFilterCriteriaImpl(name,city,anyTextField, age, hasPhotos, distance);
    }

    private class MatchFeedSearchAndFilterCriteriaImpl
        implements MatchFeedSearchAndFilterCriteria {
        private String name;
        private String city;
        private String anyTextField;
        private Integer age;
        private Boolean hasPhotos;
        private Integer distance;

        public MatchFeedSearchAndFilterCriteriaImpl(String name, String city,
            String anyTextField, Integer age, Boolean hasPhotos, Integer distance) {
            this.name = name;
            this.city = city;
            this.anyTextField = anyTextField;
            this.age = age;
            this.hasPhotos = hasPhotos;
            this.distance = distance;
        }

        public String getName() {
            return name;
        }

        public String getCity() {
            return city;
        }

        public String getAnyTextField() {
            return anyTextField;
        }

        public Integer getAge() {
            return age;
        }

        public Boolean isHasPhotos() {
            return hasPhotos;
        }

        public Integer getDistance() {
            return distance;
        }
    }
}
