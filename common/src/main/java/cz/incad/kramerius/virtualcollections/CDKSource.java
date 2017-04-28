package cz.incad.kramerius.virtualcollections;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Not collection but source
 * @author pstastny
 *
 */
public class CDKSource extends Collection {
	
	private String url;
	
	private LocalDateTime harvestingTimeStamp;
	
	private LocalDateTime publishingTimeStamp;
	
	public CDKSource(String pid, String label, boolean canLeaveFlag) {
		super(pid, label, canLeaveFlag);
	}

	public CDKSource() {
		super();
	}
	
	/**
	 * Returns url of the source
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets url of the source
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Harvesting timestamp
	 * @return
	 */
	public LocalDateTime getHarvestingTimestamp() {
		return harvestingTimeStamp;
	}

	/**
	 * Sets harvesting timestamp
	 * @param currentTimeStamp
	 */
	public void setHarvestingTimestamp(LocalDateTime currentTimeStamp) {
		this.harvestingTimeStamp = currentTimeStamp;
	}
	
	public LocalDateTime getPublishingTimestamp() {
		return this.publishingTimeStamp;
	}
	
	public void setPublishingTimestamp(LocalDateTime timestamp) {
		this.publishingTimeStamp = timestamp;
	}
	
}
