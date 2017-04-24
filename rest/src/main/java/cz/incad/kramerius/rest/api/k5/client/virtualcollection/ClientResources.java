package cz.incad.kramerius.rest.api.k5.client.virtualcollection;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.xpath.XPathExpressionException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.rest.api.exceptions.GenericApplicationException;
import cz.incad.kramerius.rest.api.replication.exceptions.ObjectNotFound;
import cz.incad.kramerius.virtualcollections.CDKSource;
import cz.incad.kramerius.virtualcollections.CDKSourcesAware;
import cz.incad.kramerius.virtualcollections.Collection.Description;
import cz.incad.kramerius.virtualcollections.CollectionsManager;

@Path("/v5.0/sources")
public class ClientResources {

    public static Logger LOGGER = Logger.getLogger(ClientResources.class.getName());

    @Inject
    @Named("cdk")
    CollectionsManager colManager;
    
    @Inject
    CDKSourcesAware sourceAwareManager;

    @Inject
    @Named("securedFedoraAccess")
    FedoraAccess fedoraAccess;
    
    
    @GET
    @Path("{pid}")
    @Consumes
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response oneVirtualCollection(@PathParam("pid") String pid) {
        try {
            CDKSource vc = this.sourceAwareManager.getSource(pid);
            return Response
                    .ok()
                    .entity(resourceTOJSON(this.fedoraAccess, vc)).build();
        } catch (ObjectNotFound e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new GenericApplicationException(e.getMessage());
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response get() {
        try {
            JSONArray jsonArr = new JSONArray();
        	List<CDKSource> sources = this.sourceAwareManager.getSources();
        	for (CDKSource cdkSource : sources) {
                jsonArr.put(resourceTOJSON(this.fedoraAccess, cdkSource));
				
			}
            return Response.ok().entity(jsonArr.toString()).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new GenericApplicationException(e.getMessage());
        }
    }
    
    public static JSONObject resourceTOJSON(FedoraAccess fa, CDKSource vc) throws XPathExpressionException, IOException {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("pid", vc.getPid());
        jsonObj.put("label", vc.getLabel());


        JSONObject jsonMap = new JSONObject();
        List<Description> descriptions = vc.getDescriptions();
        for (Description description : descriptions) {
            jsonMap.put(description.getLangCode(), description.getText());
        }
        jsonObj.put("descs", jsonMap);
        
        jsonObj.put("timestamp",vc.getHarvestingTimestamp());
        jsonObj.put("url",vc.getUrl());
        
        return jsonObj;
    }

}
