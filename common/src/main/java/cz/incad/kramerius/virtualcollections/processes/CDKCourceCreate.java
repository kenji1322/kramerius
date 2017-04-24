package cz.incad.kramerius.virtualcollections.processes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.impl.FedoraAccessImpl;
import cz.incad.kramerius.utils.conf.KConfiguration;
import cz.incad.kramerius.virtualcollections.CollectionUtils;

public class CDKCourceCreate {
//	   "pid":"vc:f750b424-bda4-4113-849a-5e9dbbfb5846",
//       "name":"NTK",
//       "type":"source",
//       "url":"http://k4.techlib.cz/search",
//       "description_txt_en":"National technical library",
//       "description_txt_cz":"Národní technická knihovna",
//       "_version_":1561764691782402048},
//     {
	
	public static void main(String[] args) throws UnsupportedEncodingException, IOException, InterruptedException {
		FedoraAccess fa = new FedoraAccessImpl(KConfiguration.getInstance(), null);
		Map<String, String> texts = new HashMap<String, String>();
		texts.put("en", "seznam");
		texts.put("cs", "seznam");
		CollectionUtils.create("vc:8f2d2a9c-5f83-4f6e-909c-a7e01319ce64", fa, null, true, texts, null);
	}
}
