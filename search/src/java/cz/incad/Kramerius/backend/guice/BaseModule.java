package cz.incad.Kramerius.backend.guice;

import java.io.File;
import java.sql.Connection;
import java.util.Locale;

import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import cz.incad.kramerius.Constants;
import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.MostDesirable;
import cz.incad.kramerius.SolrAccess;
import cz.incad.kramerius.audio.AudioLifeCycleHook;
import cz.incad.kramerius.audio.urlMapping.CachingFedoraUrlManager;
import cz.incad.kramerius.audio.urlMapping.RepositoryUrlManager;
import cz.incad.kramerius.impl.FedoraAccessImpl;
import cz.incad.kramerius.impl.MostDesirableImpl;
import cz.incad.kramerius.impl.SolrAccessImpl;
import cz.incad.kramerius.processes.GCScheduler;
import cz.incad.kramerius.processes.ProcessScheduler;
import cz.incad.kramerius.processes.database.Kramerius4ConnectionProvider;
import cz.incad.kramerius.processes.impl.GCSchedulerImpl;
import cz.incad.kramerius.processes.impl.ProcessSchedulerImpl;
import cz.incad.kramerius.relation.RelationService;
import cz.incad.kramerius.relation.impl.RelationServiceImpl;
import cz.incad.kramerius.security.SecuredFedoraAccessImpl;
import cz.incad.kramerius.service.GoogleAnalytics;
import cz.incad.kramerius.service.LifeCycleHook;
import cz.incad.kramerius.service.METSService;
import cz.incad.kramerius.service.impl.GoogleAnalyticsImpl;
import cz.incad.kramerius.service.impl.METSServiceImpl;
import cz.incad.kramerius.statistics.StatisticReport;
import cz.incad.kramerius.statistics.StatisticsAccessLog;
import cz.incad.kramerius.statistics.impl.AuthorReport;
import cz.incad.kramerius.statistics.impl.DatabaseStatisticsAccessLogImpl;
import cz.incad.kramerius.statistics.impl.LangReport;
import cz.incad.kramerius.statistics.impl.ModelStatisticReport;
import cz.incad.kramerius.utils.conf.KConfiguration;
import cz.incad.kramerius.virtualcollections.CDKSourcesAware;
import cz.incad.kramerius.virtualcollections.CDKStateSupport;
import cz.incad.kramerius.virtualcollections.Collection;
import cz.incad.kramerius.virtualcollections.CollectionsManager;
import cz.incad.kramerius.virtualcollections.impl.CDKStateSupportImpl;
import cz.incad.kramerius.virtualcollections.impl.cdk.CDKProcessingCollectionManagerImpl;
import cz.incad.kramerius.virtualcollections.impl.fedora.FedoraCollectionsManagerImpl;
import cz.incad.kramerius.virtualcollections.impl.solr.SolrCollectionManagerImpl;
import cz.incad.kramerius.virtualcollections.impl.support.CDKCollectionsIndexImpl;
import cz.incad.kramerius.virtualcollections.support.CDKCollectionsIndex;

/**
 * Base kramerius module
 */
public class BaseModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FedoraAccess.class).annotatedWith(Names.named("rawFedoraAccess")).to(FedoraAccessImpl.class).in(Scopes.SINGLETON);
        bind(FedoraAccess.class).annotatedWith(Names.named("securedFedoraAccess")).to(SecuredFedoraAccessImpl.class).in(Scopes.SINGLETON);
        bind(StatisticsAccessLog.class).to(DatabaseStatisticsAccessLogImpl.class).in(Scopes.SINGLETON);
        
        Multibinder<StatisticReport> reports = Multibinder.newSetBinder(binder(), StatisticReport.class);
        reports.addBinding().to(ModelStatisticReport.class);
        reports.addBinding().to(AuthorReport.class);
        reports.addBinding().to(LangReport.class);

        
        bind(SolrAccess.class).to(SolrAccessImpl.class).in(Scopes.SINGLETON);

        bind(METSService.class).to(METSServiceImpl.class);
        bind(KConfiguration.class).toInstance(KConfiguration.getInstance());

        bind(Connection.class).annotatedWith(Names.named("kramerius4")).toProvider(Kramerius4ConnectionProvider.class);

        bind(Locale.class).toProvider(LocalesProvider.class);

        bind(ProcessScheduler.class).to(ProcessSchedulerImpl.class).in(Scopes.SINGLETON);
        bind(GCScheduler.class).to(GCSchedulerImpl.class).in(Scopes.SINGLETON);

        // TODO: MOVE
        bind(LocalizationContext.class).toProvider(CustomLocalizedContextProvider.class);

        bind(MostDesirable.class).to(MostDesirableImpl.class);

        bind(Collection.class).toProvider(VirtualCollectionProvider.class);
        
        bind(CDKCollectionsIndex.class).to(CDKCollectionsIndexImpl.class);
        
        bind(CollectionsManager.class).annotatedWith(Names.named("fedora")).to(FedoraCollectionsManagerImpl.class);
        bind(CollectionsManager.class).annotatedWith(Names.named("solr")).to(SolrCollectionManagerImpl.class);
        bind(CollectionsManager.class).annotatedWith(Names.named("cdk")).to(CDKProcessingCollectionManagerImpl.class);
        bind(CDKSourcesAware.class).to(CDKProcessingCollectionManagerImpl.class);
        bind(CDKStateSupport.class).to(CDKStateSupportImpl.class).asEagerSingleton();
        
        bind(RelationService.class).to(RelationServiceImpl.class).in(Scopes.SINGLETON);
        bind(GoogleAnalytics.class).to(GoogleAnalyticsImpl.class).in(Scopes.SINGLETON);

        
        bind(RepositoryUrlManager.class).to(CachingFedoraUrlManager.class).in(Scopes.SINGLETON); //TODO: implement correct shutdown (Issue 567)

        Multibinder<LifeCycleHook> lfhooks = Multibinder.newSetBinder(binder(), LifeCycleHook.class);
        lfhooks.addBinding().to(AudioLifeCycleHook.class);
        
        // only CDK
        //bind(CDKVirtualCollectionsGet.class).to(CDKVirtualCollectionsGetImpl.class);
        //bind(CDKVirtualCollectionsGet.class).to(CDKSolrVirtualCollectionsGetImpl.class);

        
    }

    @Provides
    @Named("fontsDir")
    public File getProcessFontsFolder() {
        String dirName = Constants.WORKING_DIR + File.separator + "fonts";
        return new File(dirName);
    }

}
