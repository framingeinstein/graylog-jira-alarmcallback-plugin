package biz.acceleration.graylog.plugins.alarmcallback.jira;

import java.util.Arrays;
import java.util.Collection;
import org.graylog2.plugin.Plugin;
import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.PluginModule;

/**
 * Implement the Plugin interface here.
 */
public class JiraAlarmCallbackPlugin implements Plugin {
    @Override
    public PluginMetaData metadata() {
        return new JiraAlarmCallbackMetaData();
    }

    @Override
    public Collection<PluginModule> modules () {
        return Arrays.<PluginModule>asList(new JiraAlarmCallbackModule());
    }
}
