package de.leycm.jacc;

import de.leycm.jacc.adapter.JulAdapter;
import de.leycm.jacc.adapter.Log4jAdapter;
import de.leycm.jacc.adapter.LogbackAdapter;
import de.leycm.jacc.adapter.SystemStreamAdapter;
import de.leycm.neck.instance.Initializable;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotPlugin extends JavaPlugin {
    private JulAdapter julAdapter;
    private Log4jAdapter log4jAdapter;
    private LogbackAdapter logbackAdapter;
    private SystemStreamAdapter systemStreamAdapter;

    @Override
    public void onEnable() {
        Initializable.register(new JacConsoleBootstrap(300), LogApiFactory.class);

        julAdapter = new JulAdapter();
        julAdapter.register();
        log4jAdapter = new Log4jAdapter();
        log4jAdapter.register();
        logbackAdapter = new LogbackAdapter();
        logbackAdapter.register();
        systemStreamAdapter = new SystemStreamAdapter();
        systemStreamAdapter.register();
    }

    @Override
    public void onDisable() {
        julAdapter.unregister();
        log4jAdapter.unregister();
        logbackAdapter.unregister();
        systemStreamAdapter.unregister();
    }

}
