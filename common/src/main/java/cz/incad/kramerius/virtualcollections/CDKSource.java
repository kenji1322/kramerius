package cz.incad.kramerius.virtualcollections;

import java.util.Date;

public class CDKSource extends Collection {
	
	private String url;
	private Date currentTimeStamp;
	
	public CDKSource(String pid, String label, boolean canLeaveFlag) {
		super(pid, label, canLeaveFlag);
	}

	
}
