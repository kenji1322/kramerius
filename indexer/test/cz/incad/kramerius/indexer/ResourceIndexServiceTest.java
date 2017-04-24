package cz.incad.kramerius.indexer;

import org.w3c.dom.Document;

import cz.incad.kramerius.resourceindex.IResourceIndex;
import cz.incad.kramerius.resourceindex.ResourceIndexService;
import cz.incad.kramerius.utils.XMLUtils;
import junit.framework.TestCase;

public class ResourceIndexServiceTest extends TestCase {

    public void testResource() throws Exception {
        IResourceIndex g = ResourceIndexService.getResourceIndexImpl();
//        org.w3c.dom.Document doc = g.getFedoraModels();
//        XMLUtils.print(doc, System.out);

        //offset=0&sort=&sort_dir=desc&rows=50&_=1487673633375
        Document fedoraObjectsFromModelExt = g.getFedoraObjectsFromModelExt("monograph",50,0,"","");
        XMLUtils.print(fedoraObjectsFromModelExt, System.out);
        
    }
}
