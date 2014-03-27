/*
 * Copyright (C) 2013 Pavel Stastny
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
package cz.incad.kramerius.processes.mock;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import cz.incad.kramerius.processes.impl.ProcessStarter;
import cz.incad.kramerius.processes.utils.ProcessUtils;

public class MockMasterProcess {

    public static final boolean SA_FLAG = false;

    public static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(MockLPProcess.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {
        LOGGER.info("args:" + Arrays.asList(args));
        if (!SA_FLAG) {
            ProcessStarter.updateName(" Mock Master Process ");
        }

        spawnMock();

        // 1MB space
        long mb = 1l << 20;
        // 1TB space
        long tb = 1l << 40;
        // 1GB space
        long gb = 1l << 30;
        long start = System.currentTimeMillis();
        for (long i = 0; i < gb; i++) {
            if ((i % 10000) == 0) {
                LOGGER.info("  diff = " + (System.currentTimeMillis() - start)
                        + "ms and i =" + i);
            }
        }

        spawnMock();
        
        Thread.sleep(60*1*1000);

        spawnMock();
        
        System.err.println(" Jak leta jdou a hroby pribyvaji, pocitam vrasky vryte do pleti.  Koleje bezi, bezi za tramvaji jak ... ");
        LOGGER.info(" stop with " + (System.currentTimeMillis() - start) + "ms");
    }

    
    public static void spawnMock() {
        String base = ProcessUtils.getLrServlet();
        String token = System.getProperty(ProcessStarter.TOKEN_KEY);
        String url = base + "?action=start&def=mock&out=text"+(token!=null?"&token="+token:"");
        try {
            ProcessStarter.httpGet(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
