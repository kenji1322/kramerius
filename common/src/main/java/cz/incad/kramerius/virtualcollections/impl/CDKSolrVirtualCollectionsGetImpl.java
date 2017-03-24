package cz.incad.kramerius.virtualcollections.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import cz.incad.kramerius.utils.conf.KConfiguration;
import cz.incad.kramerius.virtualcollections.CDKProcessingIndex;
import cz.incad.kramerius.virtualcollections.CDKProcessingIndexException;
import cz.incad.kramerius.virtualcollections.CDKVirtualCollectionsGet;
import cz.incad.kramerius.virtualcollections.Collection;

public class CDKSolrVirtualCollectionsGetImpl  implements CDKVirtualCollectionsGet {
    
    private CDKProcessingIndex procIndex;
    
    @Inject
    public CDKSolrVirtualCollectionsGetImpl(CDKProcessingIndex procIndex) {
        super();
        this.procIndex = procIndex;
    }


	private Collection fromProcessingIndex(JSONObject jsonObject) {
		String pid = jsonObject.getString("pid");
		String name = jsonObject.getString("name");
		boolean cleave = jsonObject.getBoolean("canLeave");
		Collection col = new Collection(pid,name, cleave);
		Iterator keys = jsonObject.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			if (key.startsWith("description_txt_")) {
				String langKey = key.substring("description_txt_".length());
				String value = jsonObject.getString(key);
				Collection.Description desc = new Collection.Description(langKey, "TEXT_"+ langKey , value);
				col.addDescription(desc);
			}
		}
		return col;
	}

    @Override
    public List<Collection> virtualCollectionsFromResource(String res) throws CDKProcessingIndexException {
    	List<Collection> list = new ArrayList<Collection>();
        JSONArray cols = this.procIndex.getDataByParentAndType(res, CDKProcessingIndex.Type.collection);
        for (int i = 0,ll=cols.length(); i < ll; i++) {
    		JSONObject jsonObject = cols.getJSONObject(i);
        	Collection col = fromProcessingIndex(jsonObject);
            list.add(col);
        }
        return list;
    }

    @Override
    public Collection virtualCollectionsFromResource(String vc, String res) throws CDKProcessingIndexException {
    	List<Collection> virtualCollectionsFromResource = virtualCollectionsFromResource(res);
    	for (int i = 0,ll=virtualCollectionsFromResource.size(); i < ll; i++) {
			Collection collection = virtualCollectionsFromResource.get(i);
			if (collection.getPid().equals(vc)) return collection;
		}
    	return null;
    }

    @Override
    public List<Collection> virtualCollections() throws CDKProcessingIndexException {
    	List<Collection> list = new ArrayList<Collection>();
    	JSONArray cols = this.procIndex.getDataByType(CDKProcessingIndex.Type.collection);
        for (int i = 0,ll=cols.length(); i < ll; i++) {
    		JSONObject jsonObject = cols.getJSONObject(i);
        	Collection col = fromProcessingIndex(jsonObject);
            list.add(col);
        }
        return list;
    }

    @Override
    public String getResource(String vcId) {
        // TODO Auto-generated method stub
        return null;
    }


    /*
    public static void main(String[] args) throws UnsupportedEncodingException, CDKProcessingIndexException {
    	CDKProcessingIndex procIndex = new CDKProcessingIndexImpl();
    	CDKSolrVirtualCollectionsGetImpl get = new CDKSolrVirtualCollectionsGetImpl(procIndex);
    	List<Collection> virtualCollections = get.getCollections();
    	System.out.println(virtualCollections.size());
    	//getCollections();
    	
        
    }*/
}
