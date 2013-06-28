package com.redhat.brmsworkshop.model;

public class Customer {

	private Integer id;
	private Integer age;
	private String cpf;
	private Boolean married;
	private Integer scpcScore;
	private Integer monthlyIncome;
	
	private Integer creditValue = 0;
	private Boolean approved;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public Boolean getMarried() {
		return married;
	}
	public void setMarried(Boolean married) {
		this.married = married;
	}
	public Integer getMonthlyIncome() {
		return monthlyIncome;
	}
	public void setMonthlyIncome(Integer monthlyIncome) {
		this.monthlyIncome = monthlyIncome;
	}
	public Integer getCreditValue() {
		return creditValue;
	}
	public void setCreditValue(Integer creditValue) {
		this.creditValue = creditValue;
	}
	public Boolean getApproved() {
		return approved;
	}
	public void setApproved(Boolean approved) {
		this.approved = approved;
	}
	public Integer getScpcScore() {
		return scpcScore;
	}
	public void setScpcScore(Integer scpcScore) {
		this.scpcScore = scpcScore;
	}
}
