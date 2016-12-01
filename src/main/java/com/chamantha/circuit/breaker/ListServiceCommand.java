package com.chamantha.circuit.breaker;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.concurrent.Future;

/**
 * Created by chamantha on 12/1/16.
 */

public class ListServiceCommand extends HystrixCommand<String> {

    private final String listId;

    // e.g.
    // Injecting of list service
    //ListService listService;
    public ListServiceCommand(String listId) {
        super(HystrixCommandGroupKey.Factory.asKey("listServiceGroup"));
        this.listId = listId;
    }

    @Override
    protected String run() {
        // Service Invocation
        // e.g. listService.getCustomerListByListId(listId)
        throw new RuntimeException("Accourding to our example our service always fails");
    }

    @Override
    protected String getFallback() {

        // fallback implementation to improve the service resilience of the service
        // Various implementations can take place...
        return "Some manipulated response " + listId + "!";
    }

    public static class UnitTest {

        @Test
        public void testSynchronous() {
            String customerId1 = Long.toString(System.currentTimeMillis());
            String customerId2 = Long.toString(System.currentTimeMillis());
            assertEquals("Some manipulated response " + customerId1 + "!", new ListServiceCommand(customerId1).execute());
            assertEquals("Some manipulated response " + customerId2 + "!", new ListServiceCommand(customerId2).execute());
        }

        @Test
        public void testAsynchronous1() throws Exception {
            String customerId1 = Long.toString(System.currentTimeMillis());
            String customerId2 = Long.toString(System.currentTimeMillis());

            assertEquals("Some manipulated response " + customerId1 + "!", new ListServiceCommand(customerId1).queue().get());
            assertEquals("Some manipulated response " + customerId2 + "!", new ListServiceCommand(customerId2).queue().get());
        }

        @Test
        public void testAsynchronous2() throws Exception {

            String customerId1 = Long.toString(System.currentTimeMillis());
            String customerId2 = Long.toString(System.currentTimeMillis());

            Future<String> firstCustomersListResult = new ListServiceCommand(customerId1).queue();
            Future<String> secondCustomersListResult = new ListServiceCommand(customerId2).queue();

            assertEquals("Some manipulated response " + customerId1 + "!", firstCustomersListResult.get());
            assertEquals("Some manipulated response " + customerId2 + "!", secondCustomersListResult.get());
        }
    }

}