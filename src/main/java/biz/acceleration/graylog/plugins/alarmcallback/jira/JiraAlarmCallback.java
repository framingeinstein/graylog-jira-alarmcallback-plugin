package biz.acceleration.graylog.plugins.alarmcallback.jira;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.Project;
import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.alarms.callbacks.AlarmCallback;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackConfigurationException;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackException;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationException;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.configuration.fields.BooleanField;
import org.graylog2.plugin.configuration.fields.ConfigurationField;
import org.graylog2.plugin.configuration.fields.TextField;
import org.graylog2.plugin.streams.Stream;


/**
 * This is the plugin. Your class should implement one of the existing plugin
 * interfaces. (i.e. AlarmCallback, MessageInput, MessageOutput)
 */
public class JiraAlarmCallback implements AlarmCallback{

    
    Configuration configuration;
    JiraWrapper wrapper;
    
    @Override
    public void initialize(Configuration c) throws AlarmCallbackConfigurationException {
        configuration = c;
        
        wrapper = new JiraWrapper(
                configuration.getString("service_uri"),
                configuration.getString("service_username"),
                configuration.getString("service_password"),
                configuration.getString("issue_summary"),
                configuration.getString("project_key"),
                configuration.getString("issue_type"),
                configuration.getString("issue_assignee"),
                configuration.getBoolean("issue_reuse")
        );
    }

    @Override
    public void call(Stream stream, AlertCondition.CheckResult cr) throws AlarmCallbackException {
        
        try {
            wrapper.trigger(cr);
            
        } catch (JiraException ex) {
            Logger.getLogger(JiraAlarmCallback.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ConfigurationRequest getRequestedConfiguration() {
        final ConfigurationRequest cr = new ConfigurationRequest();
        
        cr.addField(new TextField(
        "service_uri", "JIRA URI", "", "JIRA URI where rest api lives (ie. https://localhost:8080). ",
        ConfigurationField.Optional.NOT_OPTIONAL));
        
        cr.addField(new TextField(
        "service_username", "Username", "", "JIRA Username you wish to issues to be created as.",
        ConfigurationField.Optional.NOT_OPTIONAL)); // required, must be filled out
        
        cr.addField(new TextField("service_passsword",
                "Password",
                "",
                "Password to connect with",
                ConfigurationField.Optional.NOT_OPTIONAL,
                TextField.Attribute.IS_PASSWORD));


        

        cr.addField(new TextField(
        "project_key", "JIRA Project Key", "", "Jira Project Key for project issue will be posted to. ",
        ConfigurationField.Optional.NOT_OPTIONAL));
        
         cr.addField(new TextField(
        "issue_type", "Issue Type", "", "Jira issue type that will be set for issues (Bug,Enhancement,...). ",
        ConfigurationField.Optional.NOT_OPTIONAL));
        
        cr.addField(new TextField(
        "issue_assignee", "Issue Assignee", "", "Jira user that will be assigned issues. ",
        ConfigurationField.Optional.NOT_OPTIONAL));
        
        cr.addField(new TextField(
        "issue_summary", "Issue Summary", "", "Summary to be set for issues created. ",
        ConfigurationField.Optional.NOT_OPTIONAL));
        
        cr.addField(new BooleanField("issue_reuse", "Try to keep same issue", false, ""));

        return cr;
    }

    @Override
    public String getName() {
        return "JIRA Alarm Callback";
    }

    @Override
    public Map<String, Object> getAttributes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void checkConfiguration() throws ConfigurationException {
        
        /*
         This is where we can validate the connection information provided I believe
         */
        
        if (configuration.stringIsSet("service_uri")) {
            try {
                final URI iconUri = new URI(configuration.getString("service_uri"));

                if (!"http".equals(iconUri.getScheme()) && !"https".equals(iconUri.getScheme())) {
                    throw new ConfigurationException("service_uri" + " must be a valid HTTP or HTTPS URL.");
                }
            } catch (URISyntaxException e) {
                throw new ConfigurationException("Couldn't parse " + "service_uri" + " correctly.", e);
            }
        }
        
        
        try {
            
            JiraWrapper w = new JiraWrapper(
                configuration.getString("service_uri"),
                configuration.getString("service_username"),
                configuration.getString("service_password"),
                configuration.getString("issue_summary"),
                configuration.getString("project_key"),
                configuration.getString("issue_type"),
                configuration.getString("issue_assignee"),
                configuration.getBoolean("issue_reuse")
            );
            
            Project project = w.getProject(configuration.getString("project_key"));
                              
                               //throw new UnsupportedOperationException("Not supported yet.");
        } catch (JiraException e) {
            throw new ConfigurationException("Couldn't connect to " + "service_uri" + "... check credentials and project key.", e);
        }
    }
}
