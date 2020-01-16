package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {

    String ip, text;
    EditText IPAddressInput, TextInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //to referance, need to create an object of an button. sendButton is the button id
        //sending layout, use the method findViewById
        Button sendButton = findViewById(R.id.sendButton);
        IPAddressInput = findViewById(R.id.IPAddressInput);
        TextInput = findViewById(R.id.TextInput);

        //onClick. Anytime the button is clicked, do this
        sendButton.setOnClickListener(v -> {

            ip = IPAddressInput.getText().toString().trim();
            text = TextInput.getText().toString().trim();

            //thread is needed for network access when connecting to rabbitmq
            Thread publishThread = new Thread(() -> {

                ConnectionFactory factory = new ConnectionFactory(); //factory gives new connection to Rabbitmq
                //factory.set** is everything about the connection
                factory.setHost(ip);
                //login to rabbitmq admin page, need new user info, not default
                factory.setPassword("guest1");
                factory.setUsername("guest1");

                //give new connection to rabbitmq
                try (Connection connection = factory.newConnection()) {
                    //have to through channel (send/consume)
                    Channel channel = connection.createChannel();
                    //declare or create a new que on the rabbitmq server. the important one is the que-name
                    channel.queueDeclare("hello-world", false, false, false, null);

                    //send a message
                    String message = text;
                    //this publish method sends messages to the que. the routing key is the que name
                    channel.basicPublish("", "hello-world", false, null, message.getBytes());
                } catch (TimeoutException | IOException e) {
                    e.printStackTrace();
                }
            });

            publishThread.start();

            CharSequence text = "Message has been sent";

            Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
            //toast.show();

        });
    }
}
