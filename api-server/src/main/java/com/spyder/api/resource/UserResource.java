package com.spyder.api.resource;

import com.spyder.api.common.Common.ErrorResponse;
import com.spyder.api.common.Common.Util;
import com.spyder.api.entity.User;
import com.spyder.api.repository.UserRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.hibernate.annotations.Parameter;

import javax.swing.text.html.parser.Entity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserRepository userRepo;

    @GET
    public List<User> getAll() {
        return userRepo.listAll();
    }

    @POST
    @Transactional
    public Response create(User user) throws Exception{
        try {
            Util.validateEmail(user.getEmail());
        }catch(Exception e){
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
        userRepo.persist(user);
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(long id) throws Exception{
        userRepo.deleteById(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/exists/{emailId}")
    @Operation(hidden = true)
    public Map<String, Boolean> emailExists(@PathParam("emailId") String emailId) {
        User user = userRepo.find("email", emailId).firstResult();
        Map<String, Boolean> result = new HashMap<>();
        result.put("exists", user != null);
        return result;
    }

}
