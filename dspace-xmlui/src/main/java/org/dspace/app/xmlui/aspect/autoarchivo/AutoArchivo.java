/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dspace.app.xmlui.aspect.autoarchivo;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.util.HashUtil;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.NOPValidity;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.List;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.app.xmlui.wing.element.Text;
import org.dspace.app.xmlui.wing.element.TextArea;
import org.dspace.authorize.AuthorizeException;
import org.xml.sax.SAXException;

/**
 *
 * @author lucas
 */
public class AutoArchivo extends AbstractDSpaceTransformer implements CacheableProcessingComponent{

    //puede ser necesario cambiar algunos mensajes.
    private static final Message titulo_msj = message("xmlui.panelderecho.servicios.sol_autoarchivo");
    private static final Message dspace_home_msj = message("xmlui.general.dspace_home");
    private static final Message trail_msj = message("xmlui.panelderecho.servicios.sol_autoarchivo");
    private static final Message para1 = message("xmlui.solicitud-autoarchivo.intro");
    private static final Message correo_msj = message("xmlui.ArtifactBrowser.FeedbackForm.email");
    private static final Message correo_ayuda_msj = message("xmlui.ArtifactBrowser.FeedbackForm.email_help");
    private static final Message nombre_msj = message("xmlui.solicitud-autoarchivo.nombre");
    private static final Message comunidad_msj = message("xmlui.solicitud-autoarchivo.comunidad");
    private static final Message comunidad_ayuda_msj = message("xmlui.solicitud-autoarchivo.comunidad_ayuda");
    private static final Message obs_msj = message("xmlui.solicitud-autoarchivo.obs");
    private static final Message boton_msj = message("xmlui.solicitud-autoarchivo.boton_msj");
    
    @Override
    public Serializable getKey() {
        String nombre = parameters.getParameter("nombre", "");
        String email = parameters.getParameter("email","");
        String comunidad = parameters.getParameter("comunidad","");
        String observaciones = parameters.getParameter("observaciones","");
        String page = parameters.getParameter("page","unknown");
        
       return HashUtil.hash(nombre + "-" + email + "-" + comunidad + "-" + observaciones + "-" + page);
    }

    @Override
    public SourceValidity getValidity() {
        return NOPValidity.SHARED_INSTANCE;
    }

    @Override
    public void addPageMeta(PageMeta pageMeta)throws SAXException,
            WingException, UIException, SQLException, IOException,
            AuthorizeException{
        pageMeta.addMetadata("title").addContent(titulo_msj);
        pageMeta.addTrailLink(contextPath + "/",dspace_home_msj);
        pageMeta.addTrail().addContent(trail_msj);
    }
    
    @Override
    public void addBody(Body body) throws SAXException, WingException,
            UIException, SQLException, IOException, AuthorizeException{
        Division solicitud = body.addInteractiveDivision("autoarchivo-form",
                contextPath+"/autoarchivo", Division.METHOD_POST, "primary");
        /**method_post determina el método utilizado para transmitir los valores de campo recogidos**/
        
        solicitud.setHead(titulo_msj);
        solicitud.addPara(para1);
        
        //se crea la lista de formularios
        List form = solicitud.addList("form",List.TYPE_FORM);
        
        //1er atributo del form
        Text nombre = form.addItem().addText("nombre");
        nombre.setLabel(nombre_msj);
        nombre.setValue(parameters.getParameter("nombre",""));
        
        Text email = form.addItem().addText("email");
        email.setAutofocus("autofocus");
        email.setLabel(correo_msj);
        email.setHelp(correo_ayuda_msj);
        email.setValue(parameters.getParameter("email",""));
        
        Text comunidad = form.addItem().addText("comunidad");
        comunidad.setLabel(comunidad_msj);
        comunidad.setHelp(comunidad_ayuda_msj);
        comunidad.setValue(parameters.getParameter("comunidad",""));
        
        TextArea observaciones = form.addItem().addTextArea("observaciones");
        observaciones.setLabel(obs_msj);
        observaciones.setValue(parameters.getParameter("observaciones",""));
        
        form.addItem().addButton("submit").setValue(boton_msj);
        
        solicitud.addHidden("page").setValue(parameters.getParameter("page","unknown"));
    }
    
}
