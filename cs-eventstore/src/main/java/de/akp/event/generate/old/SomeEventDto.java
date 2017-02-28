package de.akp.event.generate.old;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SomeEventDto {

	public String eventId;

	public Long nodeId;

	public Long parentId;

	public long prioity;


	public Boolean isCome;

	public String payload;

	public String getEventId() {
		return eventId;
	}


	public void setEventId(String eventId) {
		this.eventId = eventId;
	}


	public Long getNodeId() {
		return nodeId;
	}


	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}


	public Long getParentId() {
		return parentId;
	}


	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}


	public Boolean getIsCome() {
		return isCome;
	}


	public void setIsCome(Boolean isCome) {
		this.isCome = isCome;
	}


	public String getPayload() {
		return payload;
	}


	public void setPayload(String payload) {
		this.payload = payload;
	}

	public long getPrioity() {
		return prioity;
	}

	public void setPrioity(long prioity) {
		this.prioity = prioity;
	}

	@JsonCreator
	public SomeEventDto(@JsonProperty("eventId")
	String eventId, @JsonProperty("nodeId")
	Long nodeId, @JsonProperty("gateWayId")
	Long gateWayId,
			@JsonProperty("priority")
			Long priority, @JsonProperty("onCome")
	Boolean come,
			@JsonProperty("payload")
			String payload) {
		this.eventId = eventId;
		this.nodeId = nodeId;
		this.parentId = gateWayId;
		this.isCome = come;
		this.payload = payload;
	}

	public SomeEventDto() {

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ID=")
				.append(eventId).append(", come=").append(isCome)
				.append(", content: ").append(payload);
		return sb.toString();
	}

}
