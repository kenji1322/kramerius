package cz.incad.kramerius.indexer;

import java.util.ArrayList;

import junit.framework.TestCase;

public class FedoraOperationsTest extends TestCase {
    
    public void testFedoraOperations() throws Exception {
        FedoraOperations fo = new FedoraOperations();
        ArrayList<String> pidPaths = fo.getPidPaths("uuid:e24312f0-f83a-4c0c-87be-6ae1cccb1208");
        System.out.println(pidPaths);
    }
}
