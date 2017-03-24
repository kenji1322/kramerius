package cz.incad.kramerius.virtualcollections;

import org.json.JSONArray;
import org.json.JSONObject;

public interface CDKProcessingIndex {
    
    public static enum Type { source, collection }
    
    public JSONArray getDataByType(Type type) throws CDKProcessingIndexException;

    public JSONArray getDataByParent(String parentPid) throws CDKProcessingIndexException;

    public JSONArray getDataByParentAndType(String parentPid, Type type) throws CDKProcessingIndexException;
    
    public JSONObject getDataByPid(String pid) throws CDKProcessingIndexException;

    
    public void index(Type type, JSONObject jsonObject)  throws CDKProcessingIndexException;

    public void updateField(String pid, String name, String value) throws CDKProcessingIndexException;
    
}
