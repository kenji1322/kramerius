package cz.incad.kramerius.virtualcollections.impl.cdk;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.inject.Inject;

import cz.incad.kramerius.virtualcollections.CDKSource;
import cz.incad.kramerius.virtualcollections.CDKSourcesAware;
import cz.incad.kramerius.virtualcollections.Collection;
import cz.incad.kramerius.virtualcollections.CollectionException;
import cz.incad.kramerius.virtualcollections.CollectionsManager.SortType;
import cz.incad.kramerius.virtualcollections.impl.AbstractCollectionManager;
import cz.incad.kramerius.virtualcollections.impl.AbstractCollectionManager.CollectionComparator;
import cz.incad.kramerius.virtualcollections.support.CDKCollectionsIndex;
import cz.incad.kramerius.virtualcollections.support.CDKCollectionsIndexException;

public class CDKProcessingCollectionManagerImpl extends AbstractCollectionManager implements CDKSourcesAware{

    private CDKCollectionsIndex procIndex;
    
    @Inject
    public CDKProcessingCollectionManagerImpl(CDKCollectionsIndex procIndex) {
        super();
        this.procIndex = procIndex;
    }
	
	@Override
	public List<Collection> getCollections() throws CollectionException {
    	try {
			List<Collection> list = new ArrayList<Collection>();
			JSONArray cols = this.procIndex.getDataByType(CDKCollectionsIndex.Type.collection);
			for (int i = 0,ll=cols.length(); i < ll; i++) {
				JSONObject jsonObject = cols.getJSONObject(i);
				Collection col = fromProcessingIndex(jsonObject, Collection.class);
			    list.add(col);
			}
			return list;
		} catch (JSONException e) {
			throw new CollectionException(e);
		} catch (CDKCollectionsIndexException e) {
			throw new CollectionException(e);
		} catch (InstantiationException e) {
			throw new CollectionException(e);
		} catch (IllegalAccessException e) {
			throw new CollectionException(e);
		}
	}

		
	
	

	private <T extends Collection> T fromProcessingIndex(JSONObject jsonObject, Class<T> type) throws InstantiationException, IllegalAccessException {
		String pid = jsonObject.getString("pid");
		String name = jsonObject.getString("name");
		boolean cleave = true;
		if (jsonObject.has("canLeave")) {
			cleave = jsonObject.getBoolean("canLeave");
		}

		T col = type.newInstance();
		col.setPid(pid);
		col.setLabel(name);
		col.setCanLeaveFlag(cleave);
		
		
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
	public List<CDKSource> getSources() throws CollectionException {
    	try {
			List<CDKSource> list = new ArrayList<CDKSource>();
			JSONArray cols = this.procIndex.getDataByType(CDKCollectionsIndex.Type.source);
			for (int i = 0,ll=cols.length(); i < ll; i++) {
				JSONObject jsonObject = cols.getJSONObject(i);
				CDKSource col = fromProcessingIndex(jsonObject, CDKSource.class);
				
				String url = jsonObject.getString("url");
				col.setUrl(url);
				
				if (jsonObject.has("harvesting_timestamp")) {
					String timestamp = jsonObject.getString("harvesting_timestamp");
					//2014-06-26T23:25:21.179Z

					col.setHarvestingTimestamp(parseTimeStamp(timestamp));
				}

				if (jsonObject.has("publishing_timestamp")) {
					String timestamp = jsonObject.getString("publishing_timestamp");
					col.setHarvestingTimestamp(parseTimeStamp(timestamp));
				}

				list.add(col);
			}
			return list;
		} catch (JSONException e) {
			throw new CollectionException(e);
		} catch (CDKCollectionsIndexException e) {
			throw new CollectionException(e);
		} catch (InstantiationException e) {
			throw new CollectionException(e);
		} catch (IllegalAccessException e) {
			throw new CollectionException(e);
		}
	}

	
	
	
	private LocalDateTime parseTimeStamp(String timestamp) {
		DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_INSTANT;
		Instant dateInstant = Instant.from(isoFormatter.parse(timestamp));
		LocalDateTime date = LocalDateTime.ofInstant(dateInstant, ZoneId.of(ZoneOffset.UTC.getId()));
		return date;
	}

	@Override
	public List<CDKSource> getSortedSources(Locale locale, SortType type) throws CollectionException {
		List<CDKSource> sources = getSources();
		Collections.sort(sources, new CollectionComparator(locale, type));
        List<CDKSource> cols = new ArrayList<CDKSource>(sources);
        return cols;
	}

	@Override
	public CDKSource getSource(String pid) throws CollectionException {
		List<CDKSource> sources = getSources();
		for (CDKSource cdkSource : sources) {
			if (cdkSource.getPid().equals(pid)) return cdkSource;
		}
		return null;
	}

	@Override
	public List<Collection> getCollections(String sourcePid) throws CollectionException {
    	try {
			List<Collection> list = new ArrayList<Collection>();
			JSONArray cols = this.procIndex.getDataByParent(sourcePid);
			for (int i = 0,ll=cols.length(); i < ll; i++) {
				JSONObject jsonObject = cols.getJSONObject(i);
				Collection col = fromProcessingIndex(jsonObject, Collection.class);
			    list.add(col);
			}
			return list;
		} catch (JSONException e) {
			throw new CollectionException(e);
		} catch (CDKCollectionsIndexException e) {
			throw new CollectionException(e);
		} catch (InstantiationException e) {
			throw new CollectionException(e);
		} catch (IllegalAccessException e) {
			throw new CollectionException(e);
		}
	}


	@Override
	public List<Collection> getSortedCollections(String sourcePid, Locale locale, SortType type) throws CollectionException {
		List<Collection> cols = new ArrayList<Collection>(getCollections(sourcePid));
    	Collections.sort(cols, new CollectionComparator(locale, type));
    	return cols;
	}
	
}
