package com.example.wtsaskingforsignature.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wtsaskingforsignature.R;
import com.example.wtsaskingforsignature.data.api.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.VH> {
    private final List<ChatMessage> data = new ArrayList<>();

    public void setMessages(List<ChatMessage> messages) {
        data.clear();
        data.addAll(messages);
        notifyDataSetChanged();
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH holder, int position) {
        ChatMessage m = data.get(position);
        holder.role.setText("[" + m.getRole() + "]");
        holder.content.setText(m.getContent());
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView role, content;
        VH(@NonNull View itemView) {
            super(itemView);
            role = itemView.findViewById(R.id.role);
            content = itemView.findViewById(R.id.content);
        }
    }
}


