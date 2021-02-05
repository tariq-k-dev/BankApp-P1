package bankapp.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/test")
public class TestSetup {
	
	  @GET
    @Produces(MediaType.TEXT_HTML)
    public String setupTest() {
        return "<h3 style='color: #008b8b;'>Jersey up and running...</h3>";
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String setupJsonTest() {
	    return "{\"name\":\"greeting\", \"message\":\"Bank App Jersey Project up and running with JSON!\"}";
	}
}
