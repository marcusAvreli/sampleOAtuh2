package myRestIIQ4;


import org.apache.log4j.Logger;

import sailpoint.rest.AuthenticationFilter;
import sailpoint.rest.BaseOAuthRestFilter;
import sailpoint.rest.SailPointRestApplication;
import sailpoint.rest.oauth.SailPointOAuthRestApplication;

/**
 * Hello world!
 *
 */
//https://community.sailpoint.com/t5/IdentityIQ-Wiki/OAuth-2-0-Client-Credentials-as-a-Token-Based-Protocol-for-API/ta-p/77630#toc-hId--1010040998
//https://community.sailpoint.com/t5/Technical-White-Papers/IdentityIQ-REST-API-Integration/ta-p/76814
//public class App extends SailPointRestApplication {
public class App extends SailPointOAuthRestApplication{
	private static final Logger logger = Logger.getLogger(App.class);
	    
    public App() {
        super();
        logger.info("START_APPLICATION STARTED");
        //super.register(new CORSFilter());
     //   register(BaseOAuthRestFilter.class);
        register(XYZCorpCustomResource.class);
        register(MyAuthenticationFilter.class);
        logger.info("START_APPLICATION FINISH");
       // register(new CORSFilter());
        
       // register(CORSFilter.class);
    }
}
