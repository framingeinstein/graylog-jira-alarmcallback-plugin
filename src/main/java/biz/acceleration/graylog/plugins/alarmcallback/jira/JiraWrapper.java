/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.acceleration.graylog.plugins.alarmcallback.jira;
import net.rcarz.jiraclient.*;
import org.graylog2.plugin.MessageSummary;
import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.streams.Stream;


/**
 *
 * @author jason
 * Need to define these in the configuration of the plugin:
 *      credentials (username and password),
 *      issue type - the JIRA issue type to create
 *      assignee - the JIRA user to assign the issue to
 *      summary - the summary to set for the issues created
 *      
 */
public class JiraWrapper {
    
    private BasicCredentials creds = null;
    private JiraClient jira = null;
    private String summary = null;
    private String assignee = null;
    private String projectKey = null;
    private String issueType = null;
    private boolean reuse = false;
    
    
    public String getSummary(){
        return this.summary;
    }
    public JiraWrapper(String url, String username, String password, 
            String summary, String projectKey, String issueType, 
            String assignee, boolean reuse){
        this.creds = new BasicCredentials("jmorgan", "h4K^cr&#");
        this.jira = new JiraClient("http://localhost:8080", creds);
        this.summary = summary;
        this.assignee = assignee;
        this.issueType = issueType;
        this.projectKey = projectKey;
        this.reuse = reuse;
    }
    
    
    public JiraClient getJIRAClient(){
        return this.jira;
    }
    
    
    public Project getProject(String key) throws JiraException{
        return jira.getProject(key);
    }
    
    
    public void trigger(AlertCondition.CheckResult result) throws JiraException{
        Issue issue = null;
        if(!reuse){
            issue = createIssue(result.getResultDescription());
        } else {
           
           Issue.SearchResult issuesSearch = jira.searchIssues("summary ~ \"" + this.getSummary() + "\" AND (status = Open OR status = \"In Progress\" OR status = \"To Do\" OR status = Reopened) ORDER BY created DESC", 1);
           if(issuesSearch.issues.size() == 1){
               issue = issuesSearch.issues.get(0);
               
             
           } else {
               issue = createIssue(result.getResultDescription());
           }
            
        }
        
        StringBuilder sb = new StringBuilder();
        String delim = "{code}";
        for(MessageSummary m : result.getMatchingMessages()){
            sb.append(delim).append(m.getMessage()).append(delim);
        }
        
        issue.addComment(sb.toString());
    }
    
    public void updateIssue(Issue issue, String description) throws JiraException {
        

            
    }
    
    public Issue createIssue(String description) throws JiraException {
        
        
       /* Create a new issue. */
        Issue issue = jira.createIssue(this.projectKey, this.issueType)
            .field(Field.SUMMARY, this.summary)
            .field(Field.DESCRIPTION, description)
            .execute();

        issue.update()
                .field(Field.ASSIGNEE, this.assignee)
                .execute();
        return issue;
    }
    
    public static void main(String[] args) {
        
        JiraWrapper wrapper = new JiraWrapper(
                "http://localhost:8080", "jmorgan", "h4K^cr&#",
                "Graylog Alert", "TEST", "TEST", "jmorgan", false);
        
        try {
            //stream.getTitle();
            //Issue issue = wrapper.createIssue("Description");
            Issue.SearchResult result = wrapper.getJIRAClient().searchIssues("summary ~ \"" + wrapper.getSummary() + "\" AND (status = Open OR status = \"In Progress\" OR status = \"To Do\" OR status = Reopened) ORDER BY created DESC", 1);
            Issue issue = result.issues.get(0);
            
               //System.out.println("Result: " + i);
            System.out.println(issue);
        } catch(JiraException ex){
            System.out.println(ex.getMessage());
        }
    }
    
}
