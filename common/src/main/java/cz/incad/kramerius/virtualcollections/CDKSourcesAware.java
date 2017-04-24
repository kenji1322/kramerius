package cz.incad.kramerius.virtualcollections;

import java.util.List;
import java.util.Locale;

import cz.incad.kramerius.virtualcollections.CollectionsManager.SortType;

/**
 * CDK sources aware interface; ehnance managers by cdk sources informations
 * @author pstastny
 */
public interface CDKSourcesAware {

	/**
	 * Returns all cdk sources
	 * @return
	 * @throws CollectionException
	 */
	public List<CDKSource> getSources() throws CollectionException;

	/**
	 * return info about one source
	 * @param pid
	 * @return
	 * @throws CollectionException
	 */
	public CDKSource getSource(String pid) throws CollectionException;
	

	/**
	 * sorting capability
	 * @param locale
	 * @param type
	 * @return
	 * @throws CollectionException
	 */
    public List<CDKSource> getSortedSources(Locale locale, SortType type) throws CollectionException;

    
    /**
     * Returns collections from given source
     * @param sourcePid Source pid
     * @return
     * @throws CollectionException
     */
    public List<Collection> getCollections(String sourcePid) throws CollectionException;
    
    /**
     * REturns sorted collections
     * @param sourcePid Source pid
     * @param locale Locale for sort
     * @param type Type of sort
     * @return
     * @throws CollectionException
     */
    public List<Collection> getSortedCollections(String sourcePid,Locale locale, SortType type) throws CollectionException;
}
