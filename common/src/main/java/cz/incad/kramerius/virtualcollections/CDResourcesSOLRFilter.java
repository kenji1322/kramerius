package cz.incad.kramerius.virtualcollections;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.inject.Inject;

import cz.incad.kramerius.virtualcollections.impl.CDKProcessingIndexImpl;

public class CDResourcesSOLRFilter  {
    
    
    protected CDKProcessingIndex processingIndex;
    
    @Inject
    public CDResourcesSOLRFilter(CDKProcessingIndex processingIndex) {
        super();
        this.processingIndex = processingIndex;
    }

    
    public List<String> getResources() throws CDKProcessingIndexException {
        List<String> resources = new ArrayList<String>();
        JSONArray jArray = this.processingIndex.getDataByType(CDKProcessingIndex.Type.source);
        for (int i = 0,ll= jArray.length(); i < ll ; i++) {
            JSONObject doc = jArray.getJSONObject(i);
            String pid = doc.getString("pid");
            resources.add(pid);
        }
        return resources;
        
    }

    public static void main(String[] args) throws UnsupportedEncodingException, CDKProcessingIndexException {
        CDKProcessingIndex proci = new CDKProcessingIndexImpl();
        CDResourcesSOLRFilter resourceFilter = new CDResourcesSOLRFilter(proci);
        
        List<String> resources = resourceFilter.getResources();
        System.out.println(resources);
        
    }
    
}
