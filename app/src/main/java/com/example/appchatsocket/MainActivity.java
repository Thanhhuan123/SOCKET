package com.example.appchatsocket;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appchatsocket.MessageAdapter;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Socket socket;
    private ListView messageListView;
    private EditText messageEditText;
    private Button sendButton;
    private ArrayList<String> messages;
    private ArrayAdapter<String> messageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageListView = findViewById(R.id.messageListView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messages);
        messageListView.setAdapter(messageAdapter);

        // Khởi tạo Socket
        try {
            socket = IO.socket("http://192.168.1.3:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Kết nốimáy chủ
        socket.connect();
        Log.d("Socket", "Connected: " + socket.connected());

        // Gửi tin nhắn
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendMessage(message);
                    messageEditText.setText("");
                }
            }
        });

        // Nhận tin nhắn từ máy chủ
        socket.on("message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            String message = data.getString("message");
                            Log.d("ReceivedMessage", "Received: " + message); // Kiểm tra dữ liệu nhận được
                            messages.add(message);
                            messageAdapter.notifyDataSetChanged(); // Cập nhật giao diện ListView
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    // Gửi tin nhắn
    private void sendMessage(String message) {
        JSONObject data = new JSONObject();
        try {
            data.put("message", message);
            socket.emit("message", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ngắt kết nối khi thoát
        if (socket != null && socket.connected()) {
            socket.disconnect();
        }
    }
}
