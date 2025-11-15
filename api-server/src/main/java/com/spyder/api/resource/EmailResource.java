package com.spyder.api.resource;

import com.spyder.api.common.Common.ErrorResponse;
import com.spyder.api.common.Common.Util;
import com.spyder.api.entity.Emails;
import com.spyder.api.entity.User;
import com.spyder.api.repository.EmailRepository;
import com.spyder.api.repository.UserRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/emails")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmailResource {

    @Inject
    EmailRepository emailRepo;

    @GET
    public List<Emails> getAll() {
        return emailRepo.listAll();
    }

    @GET
    @Path("/{emailId}")
    public List<Emails> getAllById(@PathParam("emailId") String emailId){
        List<Emails> result = emailRepo.find("recipient", emailId).list();
        if(null != result) return result;
        return List.of();
    }

}
