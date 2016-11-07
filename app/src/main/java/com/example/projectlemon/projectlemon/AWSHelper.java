package com.example.projectlemon.projectlemon;

import android.os.Environment;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.kinesis.kinesisrecorder.KinesisRecorder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
<<<<<<< HEAD

=======
>>>>>>> 12a7de77573d98496841dffad58b94fc718bbe3f

import java.io.File;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AWSHelper {

    private static AWSHelper aws;
    public static CognitoCredentialsProvider credentialsProvider;
    public static KinesisRecorder rec;

    private AWSHelper(){
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:a0c3cc8d-5bb5-4c28-b4b7-9282805a37d3", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );
        String path = "CetysCarpool";
        try{
            rec = new KinesisRecorder(
                    getApplicationContext().getDir(path, 0), // An empty directory KinesisRecorder can use for storing requests
                    Regions.US_EAST_1,  // Region that this Recorder should save and send requests to
                    credentialsProvider);


        }catch (Exception ex){
            //Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();

        }
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        //DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);


    }

    public void dynamoDB(){
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
       // DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
    }

    public static AWSHelper getInstance(){

        if(aws == null){
            aws = new AWSHelper();
        }
        return aws;
    }

}
