package com.devebot.service;

import com.devebot.opflow.OpflowBuilder;
import com.devebot.opflow.OpflowCommander;
import com.devebot.opflow.exception.OpflowRequestTimeoutException;
import com.devebot.opflow.exception.OpflowServiceNotReadyException;
import com.devebot.opflow.exception.OpflowWorkerNotFoundException;
import com.devebot.opflow.sample.models.AlertMessage;
import com.devebot.opflow.sample.models.FibonacciInputItem;
import com.devebot.opflow.sample.models.FibonacciOutputItem;
import com.devebot.opflow.sample.services.AlertSender;
import com.devebot.opflow.sample.services.AlertSenderImpl;
import com.devebot.opflow.sample.services.FibonacciCalculator;
import com.devebot.opflow.sample.services.FibonacciCalculatorImpl;
import com.devebot.opflow.sample.utils.CommonUtil;
import com.devebot.opflow.sample.utils.Randomizer;
import com.devebot.opflow.supports.OpflowJsonTool;
import com.devebot.opflow.supports.OpflowObjectTree;
import com.devebot.sample.RandomOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by myasus on 4/3/20.
 */
public class FibonacciService {
    final static Logger logger = LoggerFactory.getLogger(FibonacciService.class);
    public static FibonacciService instance = null;
    public OpflowCommander commander;
    public FibonacciCalculator calculator;
    public AlertSender alertSender;

    public static FibonacciService getInstance() {
        if (instance == null) {
            synchronized(FibonacciService.class) {
                if (instance == null) {
                    instance = new FibonacciService();
                }
            }
        }
        return instance;
    }

    private FibonacciService(){
        try {
            if (calculator == null) {
                if (commander == null) {
                    String opflowPath = "master.properties" ;
                    commander = OpflowBuilder.createCommander(opflowPath);
                    calculator = commander.registerType(FibonacciCalculator.class, new FibonacciCalculatorImpl());
                    alertSender = commander.registerType(AlertSender.class, new AlertSenderImpl());
                    commander.serve();

                } else {
                    calculator = commander.registerType(FibonacciCalculator.class, new FibonacciCalculatorImpl());
                }
            }
        } catch(Exception ex) {
            logger.debug("Can't not connect to Queue "+ ex.getMessage());
        }
    }

    public String alert(String requestId, String request){
        try {
            logger.debug("[+] Alert");
            AlertMessage message = OpflowJsonTool.toObject(request, AlertMessage.class);
            logger.debug("[-] message: " + OpflowJsonTool.toString(message));
            alertSender.notify(message);
            return OpflowJsonTool.toString(message);
        } catch (Exception exception) {
            return OpflowObjectTree.buildMap()
                    .put("message", exception.toString())
                    .toString(true);
        }
    }

    public String fibonacci(String requestId, int number){
        FibonacciInputItem data = new FibonacciInputItem(number, requestId);
        logger.debug("[+] Make a RPC call with number: " + number + " with requestId: " + requestId);
        try {
            FibonacciOutputItem output = calculator.calc(data);
            logger.debug("[-] output: " + OpflowJsonTool.toString(output));
            return OpflowJsonTool.toString(output);
        }catch (OpflowServiceNotReadyException e) {
            return OpflowObjectTree.buildMap()
                    .put("reason", "suspend")
                    .put("message", e.getMessage())
                    .toString(true);
        }
        catch (OpflowRequestTimeoutException e) {
            return OpflowObjectTree.buildMap()
                    .put("reason", "timeout")
                    .put("message", e.getMessage())
                    .toString(true);
        }
        catch (OpflowWorkerNotFoundException e) {
            return OpflowObjectTree.buildMap()
                    .put("reason", "disabled")
                    .put("message", e.getMessage())
                    .toString(true);
        }
        catch (Exception exception) {
            return OpflowObjectTree.buildMap()
                    .put("message", exception.toString())
                    .toString(true);
        }
    }

    public String random(String reqId, int total, RandomOptions randomOptions, String method) {
        ExecutorService executor = null;
        try {
            logger.debug("[+] Make a RPC call with number: " + total);
            RandomOptions opts = null;
            switch (method) {
                case "PUT":
                    opts = randomOptions;
                    break;
            }
            if (opts == null) {
                opts = new RandomOptions();
            }

            if (opts.getExceptionTotal() > total) {
                throw new IllegalArgumentException("exceptionTotal[" + opts.getExceptionTotal() + "] is greater than total[" + total + "]");
            }

            executor = Executors.newFixedThreadPool(opts.getConcurrentCalls());

            List<Object> list = new ArrayList<>();
            if (total > 0) {
                List<Callable<Object>> tasks = new ArrayList<>();
                int exceptionCount = 0;
                int digit = CommonUtil.countDigit(total);
                String pattern = "/%0" + digit + "d";
                for (int i = 0; i<total; i++) {
                    String requestId = reqId + String.format(pattern, i);
                    int m = Randomizer.random(2, 45);
                    int remains = opts.getExceptionTotal() - exceptionCount;
                    if (0 < remains) {
                        if (remains < (total - i)) {
                            if (m % 2 != 0) {
                                m = 100;
                                exceptionCount++;
                            }
                        } else {
                            m = 100;
                            exceptionCount++;
                        }
                    }
                    int n = m;
                    tasks.add(new Callable() {
                        @Override
                        public Object call() throws Exception {
                            try {
                                if (n % 2 == 0) {
                                    return calculator.calc(n);
                                } else {
                                    return calculator.calc(new FibonacciInputItem(n, requestId));
                                }
                            } catch (Exception e) {
                                return OpflowObjectTree.buildMap()
                                        .put("number", n)
                                        .put("errorClass", e.getClass().getName())
                                        .put("errorMessage", e.getMessage())
                                        .toMap();
                            }
                        }
                    });
                }
                List<Future<Object>> futures = executor.invokeAll(tasks);
                for (Future<Object> future: futures) {
                    list.add(future.get());
                }
            }
            return OpflowJsonTool.toString(list, true);
        } catch (Exception exception) {
            return OpflowObjectTree.buildMap()
                    .put("message", exception.toString())
                    .toString(true);
        } finally {
            if (executor != null) {
                executor.shutdown();
                try {
                    executor.awaitTermination(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void close() {
        commander.close();
    }
}
