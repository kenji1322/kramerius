package cz.incad.kramerius.virtualcollections.impl.support;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import cz.incad.kramerius.utils.conf.KConfiguration;
import cz.incad.kramerius.virtualcollections.support.CDKCollectionsIndex;
import cz.incad.kramerius.virtualcollections.support.CDKCollectionsIndexException;
import cz.incad.kramerius.virtualcollections.support.CDKCollectionsIndex.Type;

public class CDKCollectionsIndexImpl implements CDKCollectionsIndex {
    
    
    public CDKCollectionsIndexImpl() {
    }
    
    public static String getSolrAddress() {
        return  KConfiguration.getInstance().getConfiguration().getString("cdk.solr.resources", "http://localhost:8983/solr/resources");
    }
    
    
    @Override
	public JSONObject getDataByPid(String pid) throws CDKCollectionsIndexException {
        try {
            JSONArray res = genericQuery(pidQuery(pid));
            if (res.length() > 0 ) {
            	 return res.getJSONObject(0);
            } else {
            	return null;
            }
        } catch (UnsupportedEncodingException e) {
            throw new CDKCollectionsIndexException(e);
        }
	}

	@Override
	public JSONArray getDataByParentAndType(String parentPid, Type type) throws CDKCollectionsIndexException {
        try {
            return genericQuery(parentPidAndTypeQuery(parentPid, type));
        } catch (UnsupportedEncodingException e) {
            throw new CDKCollectionsIndexException(e);
        }
	}

	@Override
    public JSONArray getDataByParent(String parentPid) throws CDKCollectionsIndexException {
        try {
            return genericQuery(parentPidQuery(parentPid));
        } catch (UnsupportedEncodingException e) {
            throw new CDKCollectionsIndexException(e);
        }
    }

    @Override
    public JSONArray getDataByType(Type type) throws CDKCollectionsIndexException {
        try {
            return genericQuery(typeQuery(type));
        } catch (UnsupportedEncodingException e) {
            throw new CDKCollectionsIndexException(e);
        }
    }

    private JSONArray genericQuery(String q) throws CDKCollectionsIndexException {
        JSONArray retValArray = new JSONArray();
        try {
        	int start = 0;
        	int rows = 10;
            int numberOfResults = -1;

        	do {
            	JSONObject jsonObject = solrRequest(q, start, rows);
                JSONArray jArray = jsonObject.getJSONObject("response").getJSONArray("docs");
                numberOfResults = jsonObject.getJSONObject("response").getInt("numFound");
                for (int i = 0,ll=jArray.length(); i < ll; i++) {
                    retValArray.put(jArray.getJSONObject(i));
                }
                start= start + rows;
        	} while(start < numberOfResults);
            
        } catch (UniformInterfaceException e) {
            throw new CDKCollectionsIndexException(e);
        } catch (ClientHandlerException e) {
            throw new CDKCollectionsIndexException(e);
        } catch (JSONException e) {
            throw new CDKCollectionsIndexException(e);
        }
        return retValArray;
    }

	private JSONObject solrRequest(String q, int start, int rows) {
		Client c = Client.create();
		WebResource r = c.resource(getSolrAddress()+"/select?indent=on&wt=json&q="+q+"&rows="+rows+"&start="+start);
		String t = r.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).get(String.class);
		JSONObject jsonObject = new JSONObject(t);
		return jsonObject;
	}

    private String parentPidAndTypeQuery(String parent, Type type) throws UnsupportedEncodingException {
    	return parentPidQuery(parent) + " AND "+typeQuery(type);
    }

    private String parentPidQuery(String parent) throws UnsupportedEncodingException {
        String param = URLEncoder.encode("parent:\""+parent+"\"", "UTF-8");
        return param;
    }

    private String typeQuery(Type type) throws UnsupportedEncodingException {
        String param = URLEncoder.encode("type:\""+type.name()+"\"", "UTF-8");
        return param;
    }

    private String pidQuery(String pid) throws UnsupportedEncodingException {
        String param = URLEncoder.encode("pid:\""+pid+"\"", "UTF-8");
        return param;
    }

    @Override
    public void index(Type type, JSONObject jsonObject) throws CDKCollectionsIndexException {
        try {
        	
        	if (!jsonObject.has("type")) {
        		jsonObject.put("type", type.name());
        	}
        	
            Client c = Client.create();
            String host = getSolrAddress()+"/update?commit=true";
            WebResource r = c.resource(host);

            JSONObject docObject = new JSONObject();
            docObject.put("doc", jsonObject);

            JSONObject addCommand = new JSONObject();
            addCommand.put("add", docObject);
            
            
            ClientResponse resp =  r.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).entity(addCommand.toString(),MediaType.APPLICATION_JSON).post(ClientResponse.class);
            int status = resp.getStatus();
            if (status != 200) {
                String entity = resp.getEntity(String.class);
                throw new CDKCollectionsIndexException("couldn't index data because of "+entity);
            }
            
        } catch (JSONException e) {
            throw new CDKCollectionsIndexException(e);
        } catch (UniformInterfaceException e) {
            throw new CDKCollectionsIndexException(e);
        } catch (ClientHandlerException e) {
            throw new CDKCollectionsIndexException(e);
        }

    }

    @Override
    public void updateField(String pid, String name, String value) throws CDKCollectionsIndexException {
        Client c = Client.create();
        String host = getSolrAddress()+"/update?commit=true";
        WebResource r = c.resource(host);

        JSONArray jsonArray = new JSONArray();
        
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("pid", pid);
        
        JSONObject setObject = new JSONObject();
        setObject.put("set", value);
        
        jsonObj.put(name, setObject);

        jsonArray.put(jsonObj);
        ClientResponse resp =  r.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).entity(jsonArray.toString(),MediaType.APPLICATION_JSON).post(ClientResponse.class);
        int status = resp.getStatus();
        if (status != 200) {
            String entity = resp.getEntity(String.class);
            throw new CDKCollectionsIndexException("couldn't index data because of "+entity);
        }

        
    }
}
