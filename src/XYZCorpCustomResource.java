package myRestIIQ4;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;


import sailpoint.object.Filter;
import sailpoint.object.QueryOptions;
import sailpoint.object.Identity;
import sailpoint.api.SailPointContext;
import sailpoint.api.SailPointFactory;
import sailpoint.authorization.AllowAllAuthorizer;
import sailpoint.authorization.Authorizer;
import sailpoint.rest.BaseResource;
import sailpoint.tools.GeneralException;

@Path("XYZCustom")
public class XYZCorpCustomResource extends BaseResource{
	private static final Logger logger = Logger.getLogger(XYZCorpCustomResource.class);
	 @POST
	 @Path("custom/")
	 @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
		@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	 public String getCustomString()
	 throws GeneralException {
		
	 return "1";
	 }
	 @GET
	 @Path("tester")
	 @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	 @Produces(MediaType.APPLICATION_JSON)
	 //public String returnClients(@Context HttpServletRequest request) {
	 public String returnClients() {
		 logger.info("Hello");
		 String resultString = null;
		 String auth = request.getHeader("Authorization");
		 
		  //  Account acc = null;
		    if (auth!=null) {
		    	resultString = "Authorized message";
		    }else {
		    
		     // not logged in, handle it gracefully
		    	resultString = "Not Authorized message";
	 		}
	 return resultString;
	 }
	 @GET
	 @Path("listIdentities")
	 @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
		@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	 public List<CustomIdentity> getListIdentities(){
		  SailPointContext context;
		  logger.warn("test1");
		  List<CustomIdentity> resultList = new ArrayList<CustomIdentity>();
		try {
			 logger.warn("test1.1");
			context = SailPointFactory.getCurrentContext();
		
			authentication(); 
			
		logger.warn("test1.5");

		 
		 Filter correlatedFilter=  Filter.eq("correlated",true);
		 logger.warn("test2"); 										
		 QueryOptions qo = new QueryOptions();
		 qo.add(correlatedFilter);
		 CustomIdentity customIdentity=null;
		  
	
		  Iterator<Identity>	iterator = context.search(Identity.class, qo);
			while(iterator.hasNext()) {
				Identity identity = iterator.next();
				customIdentity=new CustomIdentity();
				logger.warn(identity.getId());
				customIdentity.setId(identity.getId());
				customIdentity.setFirstName(identity.getFirstname());
				customIdentity.setLastName(identity.getLastname());
				resultList.add(customIdentity);
				
			 }
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(null != resultList) {
		 logger.warn("Result_size:"+resultList.size());
		}
		 return resultList;
	 }
	 
	/* @POST
	 @Path("search")
	 @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
		@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	 public Object search(String hall,Double price){
		 logger.warn("Search command");
		 return 1;
	 }
	 */
	/* @OPTIONS
	 public Response corsMyResource(@HeaderParam("Access-Control-Request-Headers") String requestH) {
		 logger.warn("options request");
	     ResponseBuilder rb = Response.ok();

	    // return makeCORS(rb, requestH);
	 }
	 */
	/* private Response makeCORS(ResponseBuilder req, String returnMethod) {
		   ResponseBuilder rb = req.header("Access-Control-Allow-Origin", "*")
		      .header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");

		   if (!"".equals(returnMethod)) {
		      rb.header("Access-Control-Allow-Headers", returnMethod);
		   }

		   return rb.build();
		}*/
	
	
	  public Map<String, String> authentication() throws Throwable {
	  logger.warn("AUTHENTICATION_STARTED");
	  
	      Map<String, String> map1=null;
	      Map map;
	     
	      authorize(new Authorizer[] { (Authorizer)new AllowAllAuthorizer() });
	      String[] credentials = getCredentials();
	      String user = credentials[0];
	      String password = credentials[1];
	      Map<String, String> authorizationResult = null;
	      if (null != user && null != password) {
	        SailPointContext ctx = SailPointFactory.getCurrentContext();
	        ctx.setUserName(user);
	        authorizationResult = getHandler(ctx).checkAuthentication(user, password);
	        if (null != authorizationResult && authorizationResult.get("identity") != null)
	          getRequest().setAttribute("sailpoint.rest.identityName", authorizationResult.get("identity")); 
	      } 
	      logger.warn("AUTHENTICATION_FINISHED");
	      return map1;
	    
	  }
	
}