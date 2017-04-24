package cz.incad.Kramerius.views.virtualcollection;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import cz.incad.Kramerius.backend.guice.GuiceServlet;
import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.utils.conf.KConfiguration;
import cz.incad.kramerius.virtualcollections.CollectionsManager;

public class AbstractColsServlet extends GuiceServlet  {

	@Inject
	@Named("securedFedoraAccess")
	protected FedoraAccess fedoraAccess;
	
	@Inject
	protected KConfiguration kConfiguration;

	@Inject
	@Named("fedora")
	protected CollectionsManager collectionManager;

	
	protected void writeOutput(HttpServletRequest req, HttpServletResponse resp, String s) throws IOException {
	    resp.setCharacterEncoding("UTF-8");
	    PrintWriter out = resp.getWriter();
	    out.print(s);
	}

	protected String[] getLangs() {
		String[] langs = kConfiguration.getPropertyList("interface.languages");
		return langs;
	}
}
