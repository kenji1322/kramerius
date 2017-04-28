package cz.incad.kramerius.virtualcollections.support;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Only support index which holds information about harvested collections, sources, timestamps
 * @author pstastny
 */
public interface CDKCollectionsIndex {
    
	/**
	 * Type of item enum
	 */
    public static enum Type { 
    	
    	/**
    	 * Source of cdk
    	 */
    	source, 

    	/**
    	 * Collection
    	 */
    	collection 
	}
    
    /**
     * Returns results accord with given type
     * @param type
     * @return
     * @throws CDKCollectionsIndexException
     */
    public JSONArray getDataByType(Type type) throws CDKCollectionsIndexException;

    /**
     * Returns results associated with given parent pid
     * @param parentPid
     * @return
     * @throws CDKCollectionsIndexException
     */
    public JSONArray getDataByParent(String parentPid) throws CDKCollectionsIndexException;

    /**
     * 
     * @param parentPid
     * @param type
     * @return
     * @throws CDKCollectionsIndexException
     */
    public JSONArray getDataByParentAndType(String parentPid, Type type) throws CDKCollectionsIndexException;
    
    
    public JSONObject getDataByPid(String pid) throws CDKCollectionsIndexException;
    
    public void index(Type type, JSONObject jsonObject)  throws CDKCollectionsIndexException;

    public void updateField(String pid, String name, String value) throws CDKCollectionsIndexException;

}
