package com.example.appchatsocket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> messages;

    public MessageAdapter(Context context, List<String> messages) {
        super(context, 0, messages);
        this.context = context;
        this.messages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
            holder = new ViewHolder();
            holder.messageTextView = convertView.findViewById(R.id.messageTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String message = messages.get(position);
        holder.messageTextView.setText(message);

        return convertView;
    }

    private static class ViewHolder {
        TextView messageTextView;
    }
}
