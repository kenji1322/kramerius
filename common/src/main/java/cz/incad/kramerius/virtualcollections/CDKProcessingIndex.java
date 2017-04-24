package cz.incad.kramerius.virtualcollections;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Processing index; used for store collections and sources
 * @author pstastny
 *
 */
public interface CDKProcessingIndex {
    
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
     * @throws CDKProcessingIndexException
     */
    public JSONArray getDataByType(Type type) throws CDKProcessingIndexException;

    /**
     * Returns results associated with given parent pid
     * @param parentPid
     * @return
     * @throws CDKProcessingIndexException
     */
    public JSONArray getDataByParent(String parentPid) throws CDKProcessingIndexException;

    /**
     * 
     * @param parentPid
     * @param type
     * @return
     * @throws CDKProcessingIndexException
     */
    public JSONArray getDataByParentAndType(String parentPid, Type type) throws CDKProcessingIndexException;
    
    public JSONObject getDataByPid(String pid) throws CDKProcessingIndexException;
    
    public void index(Type type, JSONObject jsonObject)  throws CDKProcessingIndexException;

    public void updateField(String pid, String name, String value) throws CDKProcessingIndexException;

}
