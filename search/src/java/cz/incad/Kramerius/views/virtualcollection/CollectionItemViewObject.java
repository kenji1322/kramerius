package cz.incad.Kramerius.views.virtualcollection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.imaging.ImageStreams;
import cz.incad.kramerius.impl.fedora.FedoraStreamUtils;
import cz.incad.kramerius.virtualcollections.Collection;
import cz.incad.kramerius.virtualcollections.Collection.Description;

public class CollectionItemViewObject {
	
	public static final Logger LOGGER = Logger.getLogger(CollectionItemViewObject.class.getName());
	
    private Collection collection;
    private boolean thumbnailAvailable = false;
    private boolean fullAvailable = false;
    
    
    public CollectionItemViewObject(Collection collection, FedoraAccess fedoraAccess) throws IOException {
        super();
        this.collection = collection;
        try {
            this.thumbnailAvailable = fedoraAccess.isStreamAvailable(this.collection.getPid(), ImageStreams.IMG_THUMB.name());
            this.fullAvailable = fedoraAccess.isStreamAvailable(this.collection.getPid(), ImageStreams.IMG_FULL.name());
        } catch(IOException ex) {
        	LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public String getPid() {
        return collection.getPid();
    }

    public String getLabel() {
        return collection.getLabel();
    }
    /**
     * @return the canLeave
     */
    public boolean isCanLeave() {
        return collection.isCanLeaveFlag();
    }
    

    public boolean isThumbnailAvailable() {
        return thumbnailAvailable;
    }

    public boolean isFullAvailable() {
        return fullAvailable;
    }

    public List<Description> getDescriptions() {
        return collection.getDescriptions();
    }
    
    
    public Map<String, String> getDescriptionsMap(){
        Map map = new HashMap<String, String>();
        for(Description d : this.collection.getDescriptions()){
            map.put(d.getLangCode(), d.getText());
        }
        return map;
    }

    public Map<String, String> getLongDescriptionsMap(){
        Map map = new HashMap<String, String>();
        for(Description d : this.collection.getDescriptions()){
            map.put(d.getLangCode(), d.getLongText());
        }
        return map;
    }

	public static List<CollectionItemViewObject> onlyLocalizedDescriptions(Locale locale, List<Collection> rawCollection, FedoraAccess fedoraAccess) throws IOException {
	    List<Collection> ncols = new ArrayList<Collection>();
	    for (Collection rCol : rawCollection) {
	        Collection col = new Collection(rCol.getPid(),rCol.getLabel(),rCol.isCanLeaveFlag());
	        Description l = rCol.lookup(locale.getLanguage());
	        if (l != null) {
	            col.addDescription(l);;
	        }
	        ncols.add(col);
	    }
	    return wrap(ncols,fedoraAccess);
	}

	public static List<CollectionItemViewObject> wrap(List<Collection> collections, FedoraAccess fedoraAccess) throws IOException {
	    List<CollectionItemViewObject> retvals = new ArrayList<CollectionItemViewObject>();
	    for (Collection collection : collections) {
	        retvals.add(new CollectionItemViewObject(collection, fedoraAccess));
	    }
	    return retvals;
	}
    
}
