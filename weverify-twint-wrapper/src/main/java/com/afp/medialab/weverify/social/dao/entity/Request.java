package com.afp.medialab.weverify.social.dao.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.afp.medialab.weverify.social.model.CollectRequest;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity(name = "Request")
@Table(name = "request")
public class Request implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7171258623231757504L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "keywordList", nullable = true, updatable = true)
	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	private Set<String> keywordList;

	@Column(name = "keywordAnyList", nullable = true, updatable = true)
	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	private Set<String> keywordAnyList;

	@Column(name = "bannedWords", nullable = true, updatable = true)
	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	private Set<String> bannedWords;

	@Column(name = "language")
	private String language;

	@Column(name = "userList", nullable = true, updatable = true)
	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	private Set<String> userList;

	@Column(name = "since")
	private Date since;

	@Column(name = "until")
	private Date until;

	@Column(name = "media_type")
	private String media;

	@Column(name = "verified")
	private Boolean verified = false;

	@Column(name = "retweetsHandling")
	private String retweetsHandling;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference
	private CollectHistory collectHistory;

	@Column(name = "merge")
	private Boolean merge = false;

	public Request() {
	}

	public Request(Set<String> keywordList, Set<String> keywordAnyList, Set<String> bannedWords, String language,
			Set<String> userList, Date since, Date until, String media) {
		this.keywordList = keywordList;
		this.keywordAnyList = keywordAnyList;
		this.bannedWords = bannedWords;
		this.language = language;
		this.userList = userList;
		this.since = since;
		this.until = until;
		this.media = media;
	}

	public Request(CollectRequest collectRequest) {
		this(collectRequest.getKeywordList(), collectRequest.getKeywordAnyList(), collectRequest.getBannedWords(),
				collectRequest.getLang(), collectRequest.getUserList(), collectRequest.getFrom(),
				collectRequest.getUntil(), collectRequest.getMedia());
	}

	public void update(CollectRequest collectRequest) {
		this.keywordList = collectRequest.getKeywordList();
		this.keywordAnyList = collectRequest.getKeywordAnyList();
		this.bannedWords = collectRequest.getBannedWords();
		this.language = collectRequest.getLang();
		this.userList = collectRequest.getUserList();
		this.since = collectRequest.getFrom();
		this.until = collectRequest.getUntil();
		this.media = collectRequest.getMedia();

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Set<String> getKeywordList() {
		return keywordList;
	}

	public void setKeywordList(SortedSet<String> keywordList) {
		this.keywordList = keywordList;
	}

	public Set<String> getKeywordAnyList() {
		return keywordAnyList;
	}

	public void setKeywordAndList(SortedSet<String> keywordAnyList) {
		this.keywordAnyList = keywordAnyList;
	}

	public Set<String> getBannedWords() {
		return bannedWords;
	}

	public void setBannedWords(SortedSet<String> bannedWords) {
		this.bannedWords = bannedWords;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Set<String> getUserList() {
		return userList;
	}

	public void setUserList(SortedSet<String> userList) {
		this.userList = userList;
	}

	public Date getSince() {
		return since;
	}

	public void setSince(Date since) {
		this.since = since;
	}

	public Date getUntil() {
		return until;
	}

	public void setUntil(Date until) {
		this.until = until;
	}

	public String getMedia() {
		return media;
	}

	public void setMedia(String media) {
		this.media = media;
	}

	public Boolean getVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
	}

	public String getRetweetsHandling() {
		return retweetsHandling;
	}

	public void setRetweetsHandling(String retweetsHandling) {
		this.retweetsHandling = retweetsHandling;
	}

	public CollectHistory getCollectHistory() {
		return collectHistory;
	}

	public void setCollectHistory(CollectHistory collectHistory) {
		this.collectHistory = collectHistory;
	}

	public Boolean getMerge() {
		return merge;
	}

	public void setMerge(Boolean merge) {
		this.merge = merge;
	}

	@Override
	public int hashCode() {
		int hash = Objects.hash(bannedWords, keywordAnyList, keywordList, language, media, merge, retweetsHandling, since,
				until, userList, verified);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Request other = (Request) obj;
		return Objects.equals(bannedWords, other.bannedWords) && Objects.equals(keywordAnyList, other.keywordAnyList)
				&& Objects.equals(keywordList, other.keywordList) && Objects.equals(language, other.language)
				&& Objects.equals(media, other.media) && Objects.equals(merge, other.merge)
				&& Objects.equals(retweetsHandling, other.retweetsHandling) && Objects.equals(since, other.since)
				&& Objects.equals(until, other.until) && Objects.equals(userList, other.userList)
				&& Objects.equals(verified, other.verified);
	}

}
