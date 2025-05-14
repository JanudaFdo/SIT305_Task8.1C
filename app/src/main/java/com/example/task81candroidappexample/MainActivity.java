package com.example.task81candroidappexample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText chatInputBox;
    private Button sendButton;
    private ProgressBar progressBar;
    private LinearLayout messagesContainer;
    private TextView welcomeMessage;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get username from intent
        username = getIntent().getStringExtra("username");
        if (username == null || username.isEmpty()) {
            username = "User";
        }

        chatInputBox = findViewById(R.id.chatInputBox);
        sendButton = findViewById(R.id.sendButton);
        progressBar = findViewById(R.id.progressBar);
        messagesContainer = findViewById(R.id.messagesContainer);
        welcomeMessage = findViewById(R.id.welcomeMessage);

        // Set welcome message with username
        welcomeMessage.setText("Welcome " + username + "!");

        sendButton.setOnClickListener(v -> sendMessage());

        // Add initial bot message
        addBotMessage("Hi " + username + "! How can I help you today?");
    }

    private void sendMessage() {
        String userMessage = chatInputBox.getText().toString().trim();
        if (userMessage.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add user message to the chat
        addUserMessage(userMessage);

        // Clear input box
        chatInputBox.setText("");

        // Show ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        String url = "http://10.0.2.2:5000/chat"; // Update to machine IP for physical device
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Hide ProgressBar and show response
                    progressBar.setVisibility(View.GONE);
                    String botMessage = response.trim();
                    
                    // Add bot message to the chat
                    addBotMessage(botMessage);
                },
                error -> {
                    // Hide ProgressBar and show error
                    progressBar.setVisibility(View.GONE);
                    String errorMessage = "Error connecting to server";
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    
                    // Add error message as bot message
                    addBotMessage("Sorry, I'm having trouble connecting to the server.");
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userMessage", userMessage);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(this).add(request);
    }

    private void addUserMessage(String message) {
        View userMessageView = LayoutInflater.from(this).inflate(R.layout.user_message_item, messagesContainer, false);
        TextView userMessageText = userMessageView.findViewById(R.id.userMessageText);
        userMessageText.setText(message);
        messagesContainer.addView(userMessageView);
    }

    private void addBotMessage(String message) {
        View botMessageView = LayoutInflater.from(this).inflate(R.layout.bot_message_item, messagesContainer, false);
        TextView botMessageText = botMessageView.findViewById(R.id.botMessageText);
        botMessageText.setText(message);
        messagesContainer.addView(botMessageView);
    }
}