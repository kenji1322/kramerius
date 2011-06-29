package cz.incad.kramerius.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.fedora.api.RelationshipTuple;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.impl.FedoraAccessImpl;
import cz.incad.kramerius.service.DeleteService;
import cz.incad.kramerius.utils.FedoraUtils;
import cz.incad.kramerius.utils.conf.KConfiguration;

public class DeleteServiceImpl implements DeleteService {


    @Inject
    @Named("securedFedoraAccess")
    FedoraAccess fedoraAccess;
    @Inject
    KConfiguration configuration;

    public static final Logger LOGGER = Logger.getLogger(DeleteServiceImpl.class.getName());

    private static final String INFO = "info:fedora/";

    @Override
    public void deleteTree(String pid, String message) throws IOException {
        Set<String> pids = fedoraAccess.getPids(pid);
        for (String s : pids) {
            String p = s.replace(INFO, "");
            LOGGER.info("Deleting object: "+p);
            try{
                fedoraAccess.getAPIM().purgeObject(p, message, false);
            }catch(Exception ex){
                LOGGER.warning("Cannot delete object "+p+", skipping: "+ex);
            }
        }
    }

    /**
     * args[0] uuid of the root object (without uuid: prefix)
     * args[1] pid_path to root object
     * @throws IOException
     */
    public static void main(String[] args) throws IOException{
        LOGGER.info("DeleteService: "+Arrays.toString(args));
        DeleteServiceImpl inst = new DeleteServiceImpl();
        inst.fedoraAccess = new FedoraAccessImpl(null);
        inst.deleteTree("uuid:"+args[0], null);
        List<RelationshipTuple> parents = FedoraUtils.getSubjectPids("uuid:"+args[0]);
        for (RelationshipTuple parent:parents){
            try{
                inst.fedoraAccess.getAPIM().purgeRelationship(parent.getSubject(), parent.getPredicate(), parent.getObject(), parent.isIsLiteral(), parent.getDatatype());
                LOGGER.info("Removed relation from parent:"+parent.getSubject()+" "+ parent.getPredicate()+" "+ parent.getObject());
                IndexerProcessStarter.spawnIndexer(true, "Reindex delete "+args[0], parent.getSubject().replace("info:fedora/uuid:", ""));
            }catch (Exception e){
                LOGGER.warning("Cannot delete object relation for"+parent.getSubject()+", skipping: "+e);
            }
        }
        IndexerProcessStarter.spawnIndexRemover(args[1], args[0]);
        LOGGER.info("DeleteService finished.");
    }

}
