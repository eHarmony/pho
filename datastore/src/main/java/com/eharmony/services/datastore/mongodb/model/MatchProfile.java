package com.eharmony.services.datastore.mongodb.model;

import java.util.Date;

import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Field;

public class MatchProfile {

	@TextIndexed
	@Field("fname")
	private String firstName;
	
	@Field("bdate")
	private Date birthDate;
	
	@Field("gid")
	private Integer genderId;
	
	@TextIndexed
	@Field("city")
	private String city;
	
	@Field("state")
	private String state;
	
	@Field("cid")
	private Integer countryId;
	
	@Field("photo")
	private Boolean hasPhoto;
	
	@Field("locale")
	private String locale;
	
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public Date getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	public Integer getGenderId() {
		return genderId;
	}
	public void setGenderId(Integer genderId) {
		this.genderId = genderId;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Integer getCountryId() {
		return countryId;
	}
	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}
	public Boolean getHasPhoto() {
		return hasPhoto;
	}
	public void setHasPhoto(Boolean hasPhoto) {
		this.hasPhoto = hasPhoto;
	}
	
	
}
