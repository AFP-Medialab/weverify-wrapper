package com.afp.medialab.weverify.social.dao.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.afp.medialab.weverify.social.dao.entity.Request;

public interface RequestInterface extends JpaRepository<Request, Integer> {

	Set<Request> findByKeywordListInAndMergeFalse(Set<String> keywords);
	Set<Request> findByKeywordListInAndKeywordAnyListInAndMergeFalse(Set<String> keywords, Set<String> keywordsAny);
	Set<Request> findByKeywordListInAndBannedWordsInAndMergeFalse(Set<String> keywords, Set<String> bannedWords);
	Set<Request> findByKeywordListInAndUserListInAndMergeFalse(Set<String> keywords, Set<String> userList);
	
	Set<Request> findByKeywordListInAndKeywordAnyListInAndBannedWordsInAndMergeFalse(Set<String> keywords, Set<String> keywordsAny, Set<String> bannedWords);
	Set<Request> findByKeywordListInAndKeywordAnyListInAndUserListInAndMergeFalse(Set<String> keywords, Set<String> keywordsAny, Set<String> userList);
	
	Set<Request> findByKeywordListInAndKeywordAnyListInAndBannedWordsInAndUserListInAndMergeFalse(
			Set<String> keywords,Set<String> keywordsAny,
			Set<String> bannedWords,Set<String> userList);
	
	Set<Request> findByKeywordAnyListInAndMergeFalse(Set<String> keywordsOr);
	Set<Request> findByKeywordAnyListInAndBannedWordsInAndMergeFalse(Set<String> keywordsAny, Set<String> bannedWords);
	Set<Request> findByKeywordAnyListInAndUserListInAndMergeFalse(Set<String> keywordsAny, Set<String> userList);
	Set<Request> findByKeywordAnyListInAndBannedWordsInAndUserListIn(
			Set<String> keywordsAny,
			Set<String> bannedWords,Set<String> userList);

	Set<Request> findByBannedWordsInAndMergeFalse(Set<String> bannedWords);

	Set<Request> findByUserListInAndMergeFalse(Set<String> userList);
	Set<Request> findByUserListInAndBannedWordsInAndMergeFalse(Set<String> userList, Set<String> bannedWords);

	/*@Query("select r from Request r where (:keywordList is null or r.keywordList in :keywordList) and "
			+ "(:keywordAnyList is null or r.keywordAnyList in :keywordAnyList) and "
			+ "(:bannedWords is null or r.bannedWords in :bannedWords) and "
			+ "(:userList is null or r.userList in :userList)")
	Set<Request> findByKeywordListInAndKeywordAnyListInAndBannedWordsInAndUserListIn(
			@Param("keywordList") Set<String> keywords, @Param("keywordAnyList") Set<String> keywordsAny,
			@Param("bannedWords")Set<String> bannedWords, @Param("userList")Set<String> userList);*/

	Set<Request> findByKeywordListInAndBannedWordsInAndUserListInAndMergeFalse(Set<String> keywords, Set<String> bannedWords,
			Set<String> userList);

	// @Modifying(clearAutomatically = true)
	// @Transactional

	@Query("select r from Request r where :my_keyword member of r.keywordList and size(r.keywordList) <= :my_length and r.merge = false")
	List<Request> myfindMatchingRequestByKeyword(@Param("my_keyword") String my_keyword,
			@Param("my_length") Integer my_length);

	// @Modifying(clearAutomatically = true)
	// @Transactional

	@Query("select r from Request r where :my_keyword member of r.bannedWords and size(r.bannedWords) <= :my_length or size(r.bannedWords) = 0 and r.merge = false")
	List<Request> my_findMatchingRequestByBannedWords(@Param("my_keyword") String my_keyword,
			@Param("my_length") Integer my_length);

	List<Request> findRequestByBannedWordsIsNull();

	// @Modifying(clearAutomatically = true)
	// @Transactional
	@Query("select r from Request r where :my_keyword member of r.userList and size(r.userList) >= :my_length or size(r.userList) = 0 and r.merge = false")
	List<Request> my_findMatchingRequestByUsers(@Param("my_keyword") String user,
			@Param("my_length") Integer my_length);

	List<Request> findRequestByUserListIsNull();

	// @Modifying(clearAutomatically = true)
	// @Transactional
	@Query("select r from Request r where :my_keyword member of r.keywordList and size(r.keywordList) >= :my_length and r.language = :lang")
	List<Request> my_findSmallerRequestByKeyword(@Param("my_keyword") String my_keyword,
			@Param("my_length") Integer my_length, @Param("lang") String lang);

	// @Modifying(clearAutomatically = true)
	// @Transactional
	@Query("select r from Request r where :my_keyword member of r.bannedWords and size(r.bannedWords) >= :my_length")
	List<Request> my_findSmallerRequestByBannedWords(@Param("my_keyword") String my_keyword,
			@Param("my_length") Integer my_length);

	// @Modifying(clearAutomatically = true)
	// @Transactional
	@Query("select r from Request r where :my_keyword member of r.userList")
	List<Request> my_findSmallerRequestByUsers(@Param("my_keyword") String user);

}
