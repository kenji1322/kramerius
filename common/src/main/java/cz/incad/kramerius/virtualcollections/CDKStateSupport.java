package cz.incad.kramerius.virtualcollections;

import java.util.List;

public interface CDKStateSupport {
	
	/** cdk state */
	public enum CDKState {

		/** Item is havested and prepared for the validation */
		HARVESTED,
		
		/** Item is validated and prepared for the publish */
		VALIDATED, 
		
		/** item is published; it could be removed */
		PUBLISHED;
	}
	
	public void insert(String pid) throws CDKStateSupportException;

	public void remove(String pid) throws CDKStateSupportException;
	
	public void changeState(String pid, CDKState state) throws CDKStateSupportException;
	
	public List<String> getPids(CDKState state) throws CDKStateSupportException;
	
	public CDKState getState(String pid) throws CDKStateSupportException;
}
