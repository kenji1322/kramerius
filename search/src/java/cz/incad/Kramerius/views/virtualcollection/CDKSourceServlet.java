package cz.incad.Kramerius.views.virtualcollection;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fedora.api.ObjectProfile;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.inject.Inject;

import cz.incad.Kramerius.backend.guice.GuiceServlet;
import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.processes.impl.ProcessStarter;
import cz.incad.kramerius.processes.utils.ProcessUtils;
import cz.incad.kramerius.security.SecurityException;
import cz.incad.kramerius.virtualcollections.CDKProcessingIndex;
import cz.incad.kramerius.virtualcollections.CDKProcessingIndexException;
import cz.incad.kramerius.virtualcollections.CDKSource;
import cz.incad.kramerius.virtualcollections.CollectionUtils;
import cz.incad.kramerius.virtualcollections.CollectionsManager;

public class CDKSourceServlet extends AbstractColsServlet {
	
	public static final Logger LOGGER = Logger.getLogger(CDKSourceServlet.class.getName());
	
	public static final String ACTION_NAME = "action";
	
	@Inject
	CDKProcessingIndex procIndex;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Actions actionToDo = Actions.CHANGE;
            String actionNameParam = req.getParameter(ACTION_NAME);
            if (actionNameParam != null) {
                actionToDo = Actions.valueOf(actionNameParam);
            }
            try {
                actionToDo.doPerform(this, fedoraAccess, this.collectionManager, req, resp);
            } catch (IOException e1) {
                LOGGER.log(Level.SEVERE, e1.getMessage(), e1);
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e1.toString());
                PrintWriter out = resp.getWriter();
                out.print(e1.toString());
            } catch (SecurityException e1) {
                LOGGER.log(Level.INFO, e1.getMessage());
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } catch (Exception e1) {
                LOGGER.log(Level.SEVERE, e1.getMessage(), e1);
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                PrintWriter out = resp.getWriter();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e1.toString());
                out.print(e1.toString());
            }
        } catch (SecurityException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }


    
    enum Actions {

        /**
         * Request to create a virtual collection
         */
        CREATE {

            @Override
            void doPerform(CDKSourceServlet ss, FedoraAccess fedoraAccess, CollectionsManager colMan, HttpServletRequest req, HttpServletResponse resp) throws IOException, SecurityException, InterruptedException {
            	try {
					String url = req.getParameter("url");
					String name = req.getParameter("name");
					
					Map<String, String> plainTexts = new HashMap<String, String>();
					Enumeration paramNames = req.getParameterNames();
					while(paramNames.hasMoreElements()) {
					    String p = paramNames.nextElement().toString();
					    if (p.startsWith("text_")) {
					        String langCode = p.substring("text_".length());
					        plainTexts.put(langCode, req.getParameter(p));
					    }
					}
						
					// indexing - possibly synchronization problem
					String pid = CollectionUtils.create(fedoraAccess, null, true, plainTexts, new CollectionUtils.CollectionManagerWait(colMan));
					JSONObject jsonObj = new JSONObject();

					jsonObj.put("currentTimeStamp", 	0L);
					jsonObj.put("pid", pid);
					jsonObj.put("name", name);
					jsonObj.put("url", url);
					jsonObj.put("canLeave", true);
					
					// should be created as more open
					jsonObj.put("description_txt_en", plainTexts.get("en"));
					jsonObj.put("description_txt_cs", plainTexts.get("cs"));
					
					
					ss.procIndex.index(CDKProcessingIndex.Type.source, jsonObj);
					
					resp.setContentType("text/plain");
					ss.writeOutput(req, resp, pid);
				} catch (JSONException e) {
					throw new IOException(e);
				} catch (CDKProcessingIndexException e) {
					throw new IOException(e);
				}
            }
        },
        /**
         * Request to delete a virtual collection
         */
        DELETE {

            @Override
            void doPerform(CDKSourceServlet ss, FedoraAccess fedoraAccess, CollectionsManager colMan, HttpServletRequest req, HttpServletResponse resp) throws Exception, SecurityException {
                String pid = req.getParameter("pid");
                CollectionUtils.delete(pid, fedoraAccess);
            }
        },
        /**
         * Request to change a virtual collection
         */
        CHANGE {

            @Override
            void doPerform(CDKSourceServlet ss, FedoraAccess fedoraAccess, CollectionsManager colMan, HttpServletRequest req, HttpServletResponse resp) throws IOException, SecurityException {
                try {
					String pid = req.getParameter("pid");
					Map<String, String> plainTexts = new HashMap<String, String>();

					String surl = req.getParameter("url");
					String name = req.getParameter("name");
					String[] langs = ss.getLangs();
					URL url = new URL(surl);
					for (int i = 0; i < langs.length; i++) {
					    String lang = langs[++i];
					    String text = req.getParameter("text_" + lang);
					    if (text != null) {
					        CollectionUtils.modifyLangDatastream(pid, lang, text, fedoraAccess);
					        plainTexts.put(lang, text);
					    }
					}
					
					JSONObject jsonObj = new JSONObject();

					jsonObj.put("currentTimeStamp", 	0L);
					jsonObj.put("pid", pid);
					jsonObj.put("name", name);
					jsonObj.put("url", url);
					jsonObj.put("canLeave", true);
					
					// should be created as more open
					jsonObj.put("description_txt_en", plainTexts.get("en"));
					jsonObj.put("description_txt_cs", plainTexts.get("cs"));
					
					ss.procIndex.index(CDKProcessingIndex.Type.source, jsonObj);

					PrintWriter out = resp.getWriter();
					out.print("1");
				} catch (JSONException e) {
					throw new IOException(e);
				} catch (CDKProcessingIndexException e) {
					throw new IOException(e);
				}

            }
        };

        abstract void doPerform(CDKSourceServlet vc, FedoraAccess fedoraAccess, CollectionsManager colMan, HttpServletRequest req, HttpServletResponse response) throws Exception, SecurityException;
    }
    
}
