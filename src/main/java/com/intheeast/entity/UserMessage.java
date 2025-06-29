package com.intheeast.entity;

import java.util.Date;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "user_message")
public final class UserMessage {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
	private Long id;
	
	@NotNull
	@Length(min = 1, max = 80)
	private String name;
	
	@NotNull
	@Size(min = 1, max = 80)
	@Email
	private String email;
	
	@NotNull
	@Size(min = 1, max = 4000)
	private String text;
	
	private String ipAddr;
	private String acceptLanguage;
	private String referer;
	private String userAgent;
	private Date dateCreated; 
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	public Long getId() { return id; }
	
	@SuppressWarnings("unused")
	private void setId(Long id) { this.id = id; }
	
	@Column(name = "name")
	public String getName() { return name; }

	public void setName(String name) { this.name = name; }
	
	@Column(name = "email")
	public String getEmail() { return email; }

	public void setEmail(String email) { this.email = email; }

	@Column(name = "message_text")
	public String getText() { return text; }

	public void setText(String text) { this.text = text; }
	
	@Column(name = "ip_addr")
	public String getIpAddress() { return ipAddr; }
	
	public void setIpAddress(String ipAddr) { this.ipAddr = ipAddr; }
	
	@Column(name = "accept_language")
	public String getAcceptLanguage() { return acceptLanguage; }
	
	public void setAcceptLanguage(String acceptLanguage) {
		this.acceptLanguage = acceptLanguage;
	}
	
	@Column(name = "referer")
	public String getReferer() { return referer; }
	
	public void setReferer(String referer) { this.referer = referer; }
	
	@Column(name = "user_agent")
	public String getUserAgent() { return userAgent; }

	public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
	
	@Column(name = "date_created")
	public Date getDateCreated() { return dateCreated; }
	
	public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }

	public String toString() {
		return new ToStringBuilder(this)
			.append("name", name)
			.append("email", email)
			.append("text", text)
			.append("ipAddress", ipAddr)
			.append("acceptLanguage", acceptLanguage)
			.append("referer", referer)
			.append("userAgent", userAgent)
			.append("dateCreated", dateCreated)
			.toString();
	}
}