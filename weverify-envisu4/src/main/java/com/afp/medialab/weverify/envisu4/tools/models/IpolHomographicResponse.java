package com.afp.medialab.weverify.envisu4.tools.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "key", "algo_info", "error", "status", "work_url", "messages" })
public class IpolHomographicResponse {

	@JsonProperty("work_url")
	private String workUrl;

	private String key;

	private String status;

	private String error;

	private ErrorCode errorCode;

	private String[] messages;

	private Results results;

	public Results getResults() {
		return results;
	}

	public void setResults(Results results) {
		this.results = results;
	}

	@JsonProperty("algo_info")
	private AlgoInfo algoInfo;

	public class AlgoInfo {
		@JsonProperty("error_message")
		private String errorMessage;

		@JsonProperty("run_time")
		private String runTime;

		public String getErrorMessage() {
			return errorMessage;
		}

		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}

		public String getRunTime() {
			return runTime;
		}

		public void setRunTime(String runTime) {
			this.runTime = runTime;
		}

	}

	public String getWorkUrl() {
		return workUrl;
	}

	public void setWorkUrl(String workUrl) {
		this.workUrl = workUrl;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String[] getMessages() {
		return messages;
	}

	public void setMessages(String[] messages) {
		this.messages = messages;
	}

	public AlgoInfo getAlgoInfo() {
		return algoInfo;
	}

	public void setAlgoInfo(AlgoInfo algoInfo) {
		this.algoInfo = algoInfo;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

}
