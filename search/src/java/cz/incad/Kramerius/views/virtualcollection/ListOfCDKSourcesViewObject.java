/*
 * Copyright (C) 2011 Alberto Hernandez
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package cz.incad.Kramerius.views.virtualcollection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.security.User;
import cz.incad.kramerius.users.LoggedUsersSingleton;
import cz.incad.kramerius.utils.conf.KConfiguration;
import cz.incad.kramerius.virtualcollections.CDKSource;
import cz.incad.kramerius.virtualcollections.CDKSourcesAware;
import cz.incad.kramerius.virtualcollections.Collection;
import cz.incad.kramerius.virtualcollections.Collection.Description;
import cz.incad.kramerius.virtualcollections.CollectionException;
import cz.incad.kramerius.virtualcollections.CollectionsManager;
import cz.incad.kramerius.virtualcollections.CollectionsManager.SortType;

public class ListOfCDKSourcesViewObject {

public static Logger LOGGER = Logger.getLogger(ListOfCDKSourcesViewObject.class.getName());
    
    @Inject
    Provider<HttpServletRequest> requestProvider;
    
    @Inject
    Provider<Locale> localeProvider;


    @Inject
    CDKSourcesAware soureAware;

    @Inject
    KConfiguration kConfiguration;
    
    @Inject
	@Named("securedFedoraAccess")
	FedoraAccess fedoraAccess;

    @Inject
    LoggedUsersSingleton loggedUsersSingleton;
    
    @Inject
    Provider<User> userProvider;

    
    @Inject
    CDKSourcesAware sources;


    public List<CDKSourceItemViewObject> getSources() throws Exception {
        return wrap(this.sources.getSources());
    }
    
    
    private List<CDKSourceItemViewObject> wrap(List<CDKSource> collections) throws IOException, CollectionException {
        SortType selectedVal = sortType();
    	List<CDKSourceItemViewObject> retvals = new ArrayList<CDKSourceItemViewObject>();
        for (CDKSource source : collections) {
            if (selectedVal != null) {
            	retvals.add(new CDKSourceItemViewObject(source,this.sources.getSortedCollections(source.getPid(), this.localeProvider.get(), selectedVal), this.fedoraAccess, this.localeProvider.get()));
            } else {
            	retvals.add(new CDKSourceItemViewObject(source,this.sources.getCollections(source.getPid()), this.fedoraAccess, this.localeProvider.get()));
            }
        	
        }
        return retvals;
    }

    
    public List<CDKSourceItemViewObject> getSourcesLocale() throws Exception {
        SortType selectedVal = sortType();
        if (selectedVal != null) {
            return onlyLocalizedDescriptions(this.sources.getSortedSources(this.localeProvider.get(), selectedVal));
        } else {
            return onlyLocalizedDescriptions(this.sources.getSources());
        }
    }

    private SortType sortType() {
        String confString = KConfiguration.getInstance().getConfiguration().getString("sources.sort", "ASC");
        if (confString == null) return null;
        SortType selectedVal = null;
        for (SortType v : CollectionsManager.SortType.values()) {
            if (confString.equals(v.name())) {
                selectedVal = v;
                break;
            }
        }
        return selectedVal;
    }

    
    public boolean isThumbnailsVisible() {
        boolean thumbs = KConfiguration.getInstance().getConfiguration().getBoolean("search.collection.thumbs",false);
        return thumbs;
    }
    
    
    private List<CDKSourceItemViewObject> onlyLocalizedDescriptions(List<CDKSource> rawCollection) throws IOException, CollectionException {
        Locale locale = this.localeProvider.get();
        List<CDKSource> ncols = new ArrayList<CDKSource>();
        for (CDKSource rCol : rawCollection) {
        	CDKSource col = new CDKSource(rCol.getPid(),rCol.getLabel(),rCol.isCanLeaveFlag());
        	col.setUrl(rCol.getUrl());
        	if (rCol.getHarvestingTimestamp() != null) {
        		col.setHarvestingTimestamp(rCol.getHarvestingTimestamp());
        	}
        	if (rCol.getPublishingTimestamp() != null) {
        		col.setPublishingTimestamp(rCol.getPublishingTimestamp());
        	}
            Description l = rCol.lookup(locale.getLanguage());
            if (l != null) {
                col.addDescription(l);;
            }
            ncols.add(col);
        }
        return wrap(ncols);
    }
    
    public List<CDKSourceItemViewObject> getVirtualCollectionsFromFedoraLocale() throws Exception {
        SortType selectedVal = sortType();
        if (selectedVal != null) {
            return onlyLocalizedDescriptions(this.sources.getSortedSources(this.localeProvider.get(), selectedVal));
        } else {
            return onlyLocalizedDescriptions(this.sources.getSources());
        }
    }
    

    private String[] filterLogged(String[] tabs) {
        List<String> mustBeLoggedList = new ArrayList<String>(Arrays.asList(kConfiguration.getPropertyList("search.home.tabs.onlylogged")));
        List<String> alist = new ArrayList<String>();
        for (int i = 0; i < tabs.length; i++) {
            if (!mustBeLoggedList.contains(tabs[i])) {
                alist.add(tabs[i]);
            }
        }
        return (String[]) alist.toArray(new String[alist.size()]);
    }
    
    
    
    public CDKSourceItemViewObject getParameterCollection()  {
        try {
            HttpServletRequest request = this.requestProvider.get();
            String parameter = request.getParameter("collection");
            if (parameter != null) {
            	CDKSource source = this.sources.getSource(parameter);
            	List<Collection> cols = this.sources.getCollections(parameter);
                return new CDKSourceItemViewObject(source, cols, this.fedoraAccess, this.localeProvider.get());
            } else return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }

    
    public String getLocaleLang() {
        return this.localeProvider.get().getLanguage();
    }
    
}
