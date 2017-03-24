package cz.incad.Kramerius.exts.menu.main.impl.adm.items;

import java.io.IOException;

import cz.incad.Kramerius.exts.menu.main.impl.AbstractMainMenuItem;
import cz.incad.Kramerius.exts.menu.main.impl.adm.AdminMenuItem;

public class CDKHarvestCollectionItem extends AbstractMainMenuItem implements AdminMenuItem {

	public  CDKHarvestCollectionItem() {
	}
	
	@Override
	public boolean isRenderable() {
		return true;
	}

	@Override
	public String getRenderedItem() throws IOException {
        return renderMainMenuItem(
                "javascript:cdkCollectionHarvest(); javascript:hideAdminMenu();",
                "administrator.menu.dialogs.importPeriodical.title", false);
	}
}
