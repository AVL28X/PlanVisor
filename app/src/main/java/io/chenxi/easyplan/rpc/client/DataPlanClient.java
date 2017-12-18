package io.chenxi.easyplan.rpc.client;

import android.util.Log;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * A simple client that requests service from data plan server
 */
public class DataPlanClient {
    private static final Logger logger = Logger.getLogger(DataPlanClient.class.getName());

    private final ManagedChannel channel;
    private final DataPlanServiceGrpc.DataPlanServiceBlockingStub blockingStub;

    public static final boolean IS_DRY_RUN = true;


    /**
     * Construct client connecting to HelloWorld server at {@code host:port}.
     */
    public DataPlanClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext(true)
                .build());
    }

    /**
     * Construct client for accessing RouteGuide server using the existing channel.
     */
    public DataPlanClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = DataPlanServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public static String extractName(Object o) {
        return ((DataPlanMsg) o).getName();
    }

    public static double extractPrice(Object o) {
        return ((DataPlanMsg) o).getPrice();
    }

    public static double extractQuota(Object o) {
        return ((DataPlanMsg) o).getQuota();
    }

    public static String extractDesc(Object o) {
        return ((DataPlanMsg) o).getDescription();
    }

    public static Date[] generateTestDates() {
        Date[] dates = new Date[30];
        Date today = new Date();
        for (int i = 0; i < 30; i++) {
            Date date = new Date(today.getTime() + (1000 * 60 * 60 * 24 * i));
            dates[i] = date;
        }

        return dates;
    }

    public static double[] generateRandomUsages(Date[] dates) {
        double[] usages = new double[dates.length];
        for (int i = 0; i < dates.length; i++) {
            usages[i] = (i % 7) * 10 + Math.random() * 1;
//            if(i<28){
//                usages[i] = 0.0;
//            }
        }
        return usages;
    }

//    public static UserParamRequest createUserParamRequest() {
//        double[] usages = new double[30];
//        Date[] dates = new Date[30];
//
//        UserParamRequest.Builder builder = UserParamRequest.newBuilder();
//        Date today = new Date();
//        for (int i = 0; i < 30; i++) {
//            usages[i] = (i % 7) * 10 + Math.random() * 1;
//            Date date = new Date(today.getTime() + (1000 * 60 * 60 * 24 * i));
//            dates[i] = date;
//            Usage usage = Usage.newBuilder().setUsage(usages[i]).setDay(date.getDay()).setMonth(date.getMonth()).setYear(date.getYear()).build();
//            builder.addUsages(usage);
//        }
//        builder.setOverage(0.01);
//
//        return builder.build();
//    }


    public UserParamResponse getUserParams(Date[] dates, double[] usages, double overage) {

//        System.out.println(Arrays.toString(dates));
//        System.out.println(Arrays.toString(usages));

        dates = generateTestDates();
        usages = generateRandomUsages(dates);
        UserParamRequest.Builder builder = UserParamRequest.newBuilder();
        for (int i = 0; i < dates.length; i++) {
            Date date = dates[i];
            Usage usage = Usage.newBuilder().setUsage(usages[i]).setDay(date.getDay()).setMonth(date.getMonth()).setYear(date.getYear()).build();
            builder.addUsages(usage);
        }

        builder.setOverage(overage);
        return this.blockingStub.getUserParam(builder.build());
    }

    public UsagesResponse getRecommendUsages(int year, int month, UserParams userParams, DataPlanMsg dataPlanMsg) {
        RecommendUsagesRequest request = RecommendUsagesRequest.newBuilder().setDataPlan(dataPlanMsg).setUserParams(userParams).setYear(year).setMonth(month).build();
        return this.blockingStub.getRecommendUsages(request);
    }

    public double getUtility(UserParams userParams, DataPlanMsg dataPlanMsg) {
        UtilityRequest request = UtilityRequest.newBuilder().setDataPlan(dataPlanMsg).setUserParams(userParams).build();
        UtilityResponse response = this.blockingStub.getUtility(request);
        return response.getUtility();
    }


    public ArrayList getRecommendDataPlans(Date[] dates, double[] usages, double overage) {
        Log.i("tag", "beginning to get recommend data plans");


        UserParams userParams = getUserParams(dates, usages, overage).getUserParams();
        Log.i("tag", "user params received");
        List<DataPlanMsg> dataPlans = getRecommendDataPlans(userParams);
        ArrayList ret = new ArrayList();
        ret.addAll(dataPlans);
        return ret;
    }

    public List<DataPlanMsg> getRecommendDataPlans(UserParams userParams) {
        DataPlanRequest request = DataPlanRequest.newBuilder().setUserParams(userParams).build();
        DataPlanResponse response = this.blockingStub.getRecommendedDataPlans(request);
        return response.getDataPlansList();
    }

    public String helloWorld() {
        HWRequest request = HWRequest.newBuilder().setWord("Hello from client").build();
        System.out.println(request);
        HWResponse response = this.blockingStub.helloWorld(request);
        System.out.println(response);
        return response.getWord();
    }

    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting.
     */
    public static void main(String[] args) throws Exception {
        String host = "ec2-34-211-116-143.us-west-2.compute.amazonaws.com";
//        String host = "localhost";
        DataPlanClient client = new DataPlanClient(host, 50051);

        //Test Hello World
        client.helloWorld();

        //A pseudo data plan and get recommended usages
        DataPlanMsg dataPlanMsg = DataPlanMsg.newBuilder().setQuota(1000).setOverage(0.01).setPrice(35).build();
        System.out.println("Pseudo data plan created");
        System.out.println(dataPlanMsg);

        //Test parameter estimation
//        Date[] dates = generateTestDates();
//        double[] usages = generateRandomUsages(dates);
//        UserParamResponse userParamsResponse = client.getUserParams(dates, usages, dataPlanMsg.getOverage());
//        UserParams userParams = userParamsResponse.getUserParams();
//        UserParamsStd userParamsStd = userParamsResponse.getUserParamsStd();
//        System.out.println("Calibrated Params:");
//        System.out.println(userParams);
//        System.out.println("Standard Deviation:");
//        System.out.println(userParamsStd);
//
//        UsagesResponse response = client.getRecommendUsages(2017, 12, userParams, dataPlanMsg);
//        System.out.println("Recommended Usages: ");
//        System.out.println(response);
//
//        // Calculate Utility
//        double utility = client.getUtility(userParams, dataPlanMsg);
//        System.out.println("Utility of data plan: ");
//        System.out.println(utility);
//        // Get DataPlans
//        List<DataPlanMsg> dataPlanMsgs = client.getRecommendDataPlans(userParams);
//        System.out.println("Recommended Data Plans");
//        for (DataPlanMsg dp : dataPlanMsgs) {
//            System.out.println(dp);
//        }
        client.shutdown();

    }
}