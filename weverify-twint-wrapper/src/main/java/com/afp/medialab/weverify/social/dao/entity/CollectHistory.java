package com.afp.medialab.weverify.social.dao.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.afp.medialab.weverify.social.model.Status;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "collectHistory")
public class CollectHistory implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "session")
    private String session;

    @OneToMany(mappedBy = "collectHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Request> requests = new LinkedList<Request>();

    @Column(name = "processStart", nullable = true)
    private Date processStart;
    @Column(name = "processEnd", nullable = true)
    private Date processEnd;
    @Column(name = "Status")
    private String status;
    @Column(name = "message")
    private String message;
    @Column(name = "count")
    private Integer count = 0;
    @Column(name = "finished_threads")
    private Integer finished_threads = 0;
    @Column(name = "total_threads")
    private Integer total_threads = 0;
    @Column(name = "successful_threads")
    private Integer successful_threads = 0;


    public CollectHistory() {
    }

    public CollectHistory(String session, List<Request> requests, Date processStart, Date processEnd, Status status, String message, Integer count, Integer finished_threads, Integer total_threads, Integer successful_threads) {
        this.session = session;
        this.requests = requests;
        this.processStart = processStart;
        this.processEnd = processEnd;
        this.status = status.toString();
        this.message = message;
        this.count = count;
        this.finished_threads = finished_threads;
        this.total_threads = total_threads;
        this.successful_threads = successful_threads;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public void addRequest(Request request) {
    	requests.add(request);
    	request.setCollectHistory(this);
    }
    
    public void removeRequest(Request request) {
    	requests.remove(request);
    	request.setCollectHistory(null);
    }
    
    public List<Request> getRequests() {
		return requests;
	}

	public Date getProcessStart() {
        return processStart;
    }

    public void setProcessStart(Date processStart) {
        this.processStart = processStart;
    }

    public Date getProcessEnd() {
        return processEnd;
    }

    public void setProcessEnd(Date processEnd) {
        this.processEnd = processEnd;
    }

    public Status getStatus() {
        return Status.valueOf(this.status);
    }

    public void setStatus(Status status) {
        this.status = status.toString();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getFinished_threads() {
        return finished_threads;
    }

    public void setFinished_threads(Integer finished_threads) {
        this.finished_threads = finished_threads;
    }

    public Integer getTotal_threads() {
        return total_threads;
    }

    public void setTotal_threads(Integer total_threads) {
        this.total_threads = total_threads;
    }

    public Integer getSuccessful_threads() {
        return successful_threads;
    }

    public void setSuccessful_threads(Integer successful_threads) {
        this.successful_threads = successful_threads;
    }
}
