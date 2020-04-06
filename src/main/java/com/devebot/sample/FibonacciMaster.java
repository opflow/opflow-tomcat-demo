package com.devebot.sample;

import com.devebot.opflow.OpflowUUID;
import com.devebot.opflow.sample.models.AlertMessage;
import com.devebot.opflow.sample.utils.Randomizer;
import com.devebot.opflow.supports.OpflowJsonTool;
import com.devebot.service.FibonacciService;
import com.google.gson.nostro.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by myasus on 3/31/20.
 */
@Path("/api")
public class FibonacciMaster {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getMessage() {
        return "Hello. I'm fine. Thank you!";
    }

    @Path("alert")
    @POST
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response alert(@HeaderParam("x-request-id") String requestId, String request) {
        if (requestId == null || requestId.isEmpty())
            requestId = OpflowUUID.getBase64ID();
        FibonacciService fibonacciService = FibonacciService.getInstance();
        String responseStr =fibonacciService.alert(requestId, request);
        return Response.status(200).entity(responseStr).build();
    }
    @Path("fibonacci/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response fibonacci(@HeaderParam("x-request-id") String requestId, @PathParam("number") int number){
        if (requestId == null || requestId.isEmpty())
            requestId = OpflowUUID.getBase64ID();
        FibonacciService fibonacciService = FibonacciService.getInstance();
        String responseStr =fibonacciService.fibonacci(requestId, number);
        return Response.status(200).entity(responseStr).build();
    }

    @Path("random/{total}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response random(@HeaderParam("x-request-id") String requestId, @PathParam("total") int total,
                           RandomOptions randomOptions){
        if (requestId == null || requestId.isEmpty())
            requestId = OpflowUUID.getBase64ID();
        FibonacciService fibonacciService = FibonacciService.getInstance();
        String responseStr =fibonacciService.random(requestId, total, randomOptions, "PUT");
        return Response.status(200).entity(responseStr).build();
    }
}

