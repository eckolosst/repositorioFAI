/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.eperson;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.log4j.Logger;
import org.dspace.app.util.CollectionDropDown;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Button;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.Field;
import org.dspace.app.xmlui.wing.element.List;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.app.xmlui.wing.element.Select;
import org.dspace.app.xmlui.wing.element.Text;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.I18nUtil;
import org.dspace.core.LogManager;
import org.dspace.eperson.Group;
import org.dspace.eperson.Subscribe;
import org.xml.sax.SAXException;


/**
 * Display a form that allows the user to edit their profile.
 * There are two cases in which this can be used: 1) when an
 * existing user is attempting to edit their own profile, and
 * 2) when a new user is registering for the first time.
 *
 * There are several parameters this transformer accepts:
 *
 * email - The email address of the user registering for the first time.
 *
 * registering - A boolean value to indicate whether the user is registering for the first time.
 *
 * retryInformation - A boolean value to indicate whether there was an error with the user's profile.
 *
 * retryPassword - A boolean value to indicate whether there was an error with the user's password.
 *
 * allowSetPassword - A boolean value to indicate whether the user is allowed to set their own password.
 *
 * @author Scott Phillips
 */
public class Alerta extends AbstractDSpaceTransformer
{
    private static Logger log = Logger.getLogger(Alerta.class);

    /** Language string used: */
        
    private static final Message T_dspace_home =
        message("xmlui.general.dspace_home");    
    
    private static final Message T_trail_update =
        message("xmlui.EPerson.Alerta.trail_update");
    
    private static final Message T_head_update =
        message("xmlui.EPerson.Alerta.head_update");
    
    private static final Message T_subscriptions =
        message("xmlui.EPerson.Alerta.subscriptions");

    private static final Message T_subscriptions_help =
        message("xmlui.EPerson.Alerta.subscriptions_help");

    private static final Message T_email_subscriptions =
        message("xmlui.EPerson.Alerta.email_subscriptions");

    private static final Message T_select_collection =
        message("xmlui.EPerson.Alerta.select_collection");
    
    private static Locale[] supportedLocales = getSupportedLocales();
    static
    {
        Arrays.sort(supportedLocales, new Comparator<Locale>() {
            public int compare(Locale a, Locale b)
            {
                return a.getDisplayName().compareTo(b.getDisplayName());
            }
        });
    }
    
    /** The email address of the user registering for the first time.*/
    private String email;

    /** Determine if the user is registering for the first time */
    private boolean registering;
    
    /** Determine if the user is allowed to set their own password */
    private boolean allowSetPassword;
    
    /** A list of fields in error */
    private java.util.List<String> errors;
    
    public void setup(SourceResolver resolver, Map objectModel, String src,
            Parameters parameters) throws ProcessingException, SAXException,
            IOException
    {
        super.setup(resolver,objectModel,src,parameters);
        
        this.email = parameters.getParameter("email","unknown");
        this.registering = parameters.getParameterAsBoolean("registering",false);
        this.allowSetPassword = parameters.getParameterAsBoolean("allowSetPassword",false);
        
        String errors = parameters.getParameter("errors","");
        if (errors.length() > 0)
        {
            this.errors = Arrays.asList(errors.split(","));
        }
        else
        {
            this.errors = new ArrayList<String>();
        }
        
        // Ensure that the email variable is set.
        if (eperson != null)
        {
            this.email = eperson.getEmail();
        }
    }
       
    public void addPageMeta(PageMeta pageMeta) throws WingException
    {
        // Set the page title
        
        pageMeta.addMetadata("title").addContent("Suscribirse a Alerta");
               
        pageMeta.addTrailLink(contextPath + "/",T_dspace_home);
               
        pageMeta.addTrail().addContent(T_trail_update);
        
    }
    
    
   public void addBody(Body body) throws WingException, SQLException
   {
       // Log that we are viewing a profile
       log.info(LogManager.getHeader(context, "view_profile", ""));

       Request request = ObjectModelHelper.getRequest(objectModel);
       
       String defaultFirstName="",defaultLastName="",defaultPhone="";
       String defaultDNI="";
       String defaultTitulo="";
       String defaultDependencia = "";
       String defaultFuncion = "";
       String defaultLanguage=null;
       
        if (eperson != null)
        {
            defaultFirstName = eperson.getFirstName();
            defaultLastName = eperson.getLastName();
            defaultPhone = eperson.getMetadata("phone");
            defaultLanguage = eperson.getLanguage();
            defaultDNI = eperson.getDNI();
        }
        else{
            //Debería redirigir a login
        }
       
       String action = contextPath;
       action += "/alerta";
             
       
       Division profile = body.addInteractiveDivision("information",
               action,Division.METHOD_POST,"primary");
       
       
       profile.setHead(T_head_update);             
       
       
       
       List form = profile.addList("form",List.TYPE_FORM);
       
       // Subscriptions
       
       List subscribe = form.addList("subscriptions",List.TYPE_FORM);
       subscribe.setHead(T_subscriptions);

       subscribe.addItem(T_subscriptions_help);

       Collection[] currentList = Subscribe.getSubscriptions(context, context.getCurrentUser());
       Collection[] possibleList = Collection.findAll(context);

       Select subscriptions = subscribe.addItem().addSelect("subscriptions");
       subscriptions.setLabel(T_email_subscriptions);
       subscriptions.setHelp("");
       subscriptions.enableAddOperation();
       subscriptions.enableDeleteOperation();

       subscriptions.addOption(-1,T_select_collection);
           CollectionDropDown.CollectionPathEntry[] possibleEntries = CollectionDropDown.annotateWithPaths(possibleList);
       for (CollectionDropDown.CollectionPathEntry possible : possibleEntries)
       {
           subscriptions.addOption(possible.collection.getID(), possible.path);
       }

       for (Collection collection: currentList)
       {
           subscriptions.addInstance().setOptionSelected(collection.getID());
       }
       
       
       
       profile.addHidden("eperson-continue").setValue(knot.getId());
              
       
   }
   
   /**
    * Recycle
    */
    public void recycle()
    {
        this.email = null;
        this.errors = null;
        super.recycle();
    }

    /**
     * get the available Locales for the User Interface as defined in dspace.cfg
     * property webui.supported.locales
     * returns an array of Locales or null
     *
     * @return an array of supported Locales or null
     */
    private static Locale[] getSupportedLocales()
    {
        String ll = ConfigurationManager.getProperty("webui.supported.locales");
        if (ll != null)
        {
            return I18nUtil.parseLocales(ll);
        }
        else
        {
            Locale result[] = new Locale[1];
            result[0] =  I18nUtil.DEFAULTLOCALE;
            return result;
        }
    }
}
