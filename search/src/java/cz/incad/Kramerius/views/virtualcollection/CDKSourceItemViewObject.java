package cz.incad.Kramerius.views.virtualcollection;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.virtualcollections.CDKSource;
import cz.incad.kramerius.virtualcollections.Collection;

public class CDKSourceItemViewObject extends CollectionItemViewObject{

	
	private List<CollectionItemViewObject> subcols;
	private CDKSource cdkSource;

	
	public CDKSourceItemViewObject(CDKSource source, List<Collection> collections, FedoraAccess fedoraAccess, Locale locale) throws IOException {
		super(source, fedoraAccess);
		this.cdkSource = source;
		this.subcols = onlyLocalizedDescriptions(locale, collections, fedoraAccess);
	}

	public List<CollectionItemViewObject> getCollections() {
		return this.subcols;
	}
	
	public String getUrl() {
		return this.cdkSource.getUrl();
	}
	
	public String getLabel() {
		return this.cdkSource.getLabel();
	}


	public String getHarvestingTimestamp() {
		LocalDateTime currentTimeStamp = this.cdkSource.getHarvestingTimestamp();
		if (currentTimeStamp != null) {
			ZonedDateTime departure = ZonedDateTime.of(currentTimeStamp, ZoneId.of(ZoneOffset.UTC.getId()));
			return departure.format(DateTimeFormatter.ISO_INSTANT);
		} else return "";
	}

	public String getPublishingTimestamp() {
		LocalDateTime currentTimeStamp = this.cdkSource.getPublishingTimestamp();
		if (currentTimeStamp != null) {
			ZonedDateTime departure = ZonedDateTime.of(currentTimeStamp, ZoneId.of(ZoneOffset.UTC.getId()));
			return departure.format(DateTimeFormatter.ISO_INSTANT);
		} else return "";
	}

}

