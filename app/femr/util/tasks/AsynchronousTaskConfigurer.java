package femr.util.tasks;

import com.google.inject.AbstractModule;

public class AsynchronousTaskConfigurer extends AbstractModule {

    @Override
    protected void configure() {
        //All asynchronous tasks must be loaded as eager singletons
        bind(InternetConnectionTask.class).asEagerSingleton();
    }
}
