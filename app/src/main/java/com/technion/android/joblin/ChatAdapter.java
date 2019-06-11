package com.technion.android.joblin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;

    private Context mContext;
    private List<Message> chat;

    public ChatAdapter(Context context, List<Message> chat) {
        this.mContext = context;
        this.chat = chat;
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType==MSG_TYPE_LEFT)
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.chat_item_left, parent, false);
        else
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.chat_item_right, parent, false);
        return new ChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatAdapter.ViewHolder holder, int position) {
        Message message = chat.get(position);
        holder.messageTxt.setText(message.getMessage());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.timeTxt.setText(sdf.format(message.getTimestamp().toDate()));
    }

    @Override
    public int getItemViewType(int position) {

        if(chat.get(position).getSender()
                .equals(mAuth.getCurrentUser().getEmail()))
            return MSG_TYPE_RIGHT;
        return MSG_TYPE_LEFT;
    }

    @Override
    public int getItemCount() {
        return chat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView messageTxt;
        private TextView timeTxt;

        public ViewHolder(View itemView) {
            super(itemView);
            messageTxt = itemView.findViewById(R.id.viewMessageTxt);
            timeTxt = itemView.findViewById(R.id.timeMessageTxt);
        }
    }
}
