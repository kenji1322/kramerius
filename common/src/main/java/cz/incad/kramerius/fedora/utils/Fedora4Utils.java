package cz.incad.kramerius.fedora.utils;

import cz.incad.kramerius.FedoraNamespaces;
import cz.incad.kramerius.fedora.om.Repository;
import cz.incad.kramerius.fedora.om.RepositoryException;
import cz.incad.kramerius.utils.StringUtils;
import cz.incad.kramerius.utils.XMLUtils;
import cz.incad.kramerius.utils.conf.KConfiguration;
import cz.incad.kramerius.utils.pid.PIDParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by pstastny on 10/10/2017.
 */
public class Fedora4Utils {

    public static final Logger LOGGER = Logger.getLogger(Fedora4Utils.class.getName());

    // divide factor for storing in repo
    public static final int DIVIDE_FACTOR = 3;

    // cannot be configured; part of relative paths
    public static final String BOUND_CONTEXT= "rest";

    // prefixes used in repo for string stuff
    public static final String DATA_PREFIX_PATH = "data";
    public static final String MODELS_PREFIX_PATH = "model";
    public static final String COLLECTIONS_PREFIX_PATH = "collections";

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DATE_FORMAT_WITHOUTMILIS = "yyyy-MM-dd'T'HH:mm:ss'Z'";

//    /** DEFAULT Date formatter*/
//    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");



    /**
     * Normalizing path in order to store object into repo
     * @param pid Given pid which should be normalized
     * @return Path into repository  disected into pieces
     */
    public static final List<String> normalizePath(String pid) {
        if (pid.startsWith(PIDParser.INFO_FEDORA_PREFIX)) {
            pid = StringUtils.minus(pid, PIDParser.INFO_FEDORA_PREFIX);
        }
        String prefix = (pid.toLowerCase().startsWith("model:") || pid.toLowerCase().startsWith("vc:")) ? (pid.toLowerCase().startsWith("model:") ? MODELS_PREFIX_PATH : COLLECTIONS_PREFIX_PATH) : DATA_PREFIX_PATH;
        if (pid.contains(":")) {
            pid = pid.substring(pid.indexOf(':')+1);
        }
        return prefix.equals(DATA_PREFIX_PATH) ? dividePid(pid, prefix) : notDividedPid(pid, prefix);
    }


    /**
     * Utility method returns whole path from given containers
     * @param parts
     * @return
     */
    public static final String path(List<String> parts) {
        return parts.stream().reduce("", (p,s)-> {return p=p+"/"+s;});
    }

    /**
     * Returns path but not divide into pieces
     * @param pid
     * @param prefix
     * @return
     */
    private static List<String> notDividedPid(String pid, String prefix) {
        return new ArrayList<>(Arrays.asList(prefix, pid));
    }

    private static List<String> dividePid(String pid, String prefix) {
        pid =  pid.replaceAll("-","");
        List<String> list = new ArrayList<>(Arrays.asList(prefix));
        StringBuilder sbuilder = new StringBuilder();
        char[] chars = pid.toCharArray();
        for (int j = 0; j < chars.length; j++) {
            if (j % DIVIDE_FACTOR == 0) {
                if (sbuilder.length() > 0) {
                    list.add(sbuilder.toString());
                }
                sbuilder = new StringBuilder();
            }
            sbuilder.append(chars[j]);
        }

        if (sbuilder.length() > 0) {
            list.add(sbuilder.toString());
        }
        return list;
    }

    /**
     * Returns configured endpoint
     * @return
     */
    public static String endpoint() {
        String fedoraHost = KConfiguration.getInstance().getConfiguration().getString("fedora4Host");
        return fedoraHost + (fedoraHost.endsWith("/")? "" : "/") + BOUND_CONTEXT;
    }


    public static List<String> link(String link) {
        String endpoint = endpoint();
        link = StringUtils.minus(link, endpoint);
        if (link.endsWith("/")) {
            link = StringUtils.minus(link, "/"+BOUND_CONTEXT+"/");
        } else {
            link = StringUtils.minus(link, "/"+BOUND_CONTEXT);
        }
        if (link.startsWith("/")) link = link.substring(1);
        return Arrays.asList(link.split("/"));
    }


    public static Date extractDate(InputStream body, String name, String namespace) throws ParserConfigurationException, SAXException, IOException, ParseException {
        Document document = XMLUtils.parseDocument(body, true);
        Element lastModified = XMLUtils.findElement(document.getDocumentElement(), name, namespace);
        if (lastModified != null) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                return dateFormat.parse(lastModified.getTextContent());
            } catch (ParseException e) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_WITHOUTMILIS);
                return dateFormat.parse(lastModified.getTextContent());
            }
        } else return null;
    }

    public static String extractText(InputStream body, String localName, String namespace) throws ParserConfigurationException, SAXException, IOException {
        Document document = XMLUtils.parseDocument(body, true);
        Element hasMimeType = XMLUtils.findElement(document.getDocumentElement(), localName, namespace);
        if (hasMimeType != null) {
            return hasMimeType.getTextContent();
        } else throw new IOException("cannot find  mimetype element");
    }

    public static void doInTransaction(Repository rep, Operations op) throws RepositoryException {
        try {
            op.inTransaction(rep);
            rep.commitTransaction();
        }catch(Throwable e) {
            rep.rollbackTransaction();
            LOGGER.log(Level.SEVERE,e.getMessage(),e);
        }
    }

    public static interface  Operations {
        public void inTransaction(Repository rep) throws RepositoryException, UnsupportedEncodingException;
    }
}