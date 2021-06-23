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

	private String[] messages;
	
	private Results results = new Results();
	

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
	
	/**
	 * @author bertrand
	 *
	 */
	public class Results {
		private String output0, output1, pano, stdout;

		public String getOutput0() {
			return output0;
		}

		public void setOutput0(String output0) {
			this.output0 = output0;
		}

		public String getOutput1() {
			return output1;
		}

		public void setOutput1(String output1) {
			this.output1 = output1;
		}

		public String getPano() {
			return pano;
		}

		public void setPano(String pano) {
			this.pano = pano;
		}

		public String getStdout() {
			return stdout;
		}

		public void setStdout(String stdout) {
			this.stdout = stdout;
		}
		
	}

}
