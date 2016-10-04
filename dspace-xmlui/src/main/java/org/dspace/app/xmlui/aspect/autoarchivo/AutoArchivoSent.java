/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.xmlui.aspect.autoarchivo;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;

import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.NOPValidity;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.authorize.AuthorizeException;
import org.xml.sax.SAXException;

/**
 * Simple page to let the user know their feedback has been sent.
 * 
 * @author Scott Phillips
 */
public class AutoArchivoSent extends AbstractDSpaceTransformer implements CacheableProcessingComponent
{
    /** language strings */
    public static final Message titulo_msj = message("xmlui.panelderecho.servicios.sol_autoarchivo");
    
    public static final Message dspace_home_msj = message("xmlui.general.dspace_home");
    
    public static final Message trail_msj = message("xmlui.panelderecho.servicios.sol_autoarchivo");
    
    public static final Message T_head = message("xmlui.panelderecho.servicios.sol_autoarchivo");
    
    public static final Message para1 = message("xmlui.AutoArchivoSent.para1");
    
    
    /**
     * Generate the unique caching key.
     */
    public Serializable getKey() {
        return "1";
    }

    /**
     * Generate the cache validity object.
     */
    public SourceValidity getValidity() {
       return NOPValidity.SHARED_INSTANCE;
    }
    
    
    public void addPageMeta(PageMeta pageMeta) throws SAXException, WingException, UIException,
            SQLException, IOException,AuthorizeException{
        pageMeta.addMetadata("title").addContent(titulo_msj);
       
        pageMeta.addTrailLink(contextPath + "/",dspace_home_msj);
        pageMeta.addTrail().addContent(trail_msj);
    }

  
    public void addBody(Body body) throws SAXException, WingException,
            UIException, SQLException, IOException, AuthorizeException{
        Division feedback = body.addDivision("autoarchivo-sent","primary");
     
        feedback.setHead(T_head);
        
        feedback.addPara(para1);
        
    }
}
