

package com.legendarysoftwares.homerental;

import static com.legendarysoftwares.homerental.ChatScreen.receiverPhotoUrl;
import static com.legendarysoftwares.homerental.ChatScreen.senderImg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ChatScreenAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<ChatScreenModelClass> messagesAdpterArrayList;
    int ITEM_SEND=1;
    int ITEM_RECIVE=2;

    public ChatScreenAdapter(Context context, ArrayList<ChatScreenModelClass> messagesAdpterArrayList) {
        this.context = context;
        this.messagesAdpterArrayList = messagesAdpterArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new senderVierwHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout, parent, false);
            return new reciverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatScreenModelClass ChatsModel = messagesAdpterArrayList.get(position);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context).setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

                return false;
            }
        });
        if (holder.getClass()==senderVierwHolder.class){
            senderVierwHolder viewHolder = (senderVierwHolder) holder;
            viewHolder.sentMassage.setText(ChatsModel.getMessage());
            Picasso.get().load(senderImg).into(viewHolder.massageSenderDP);
        }else { reciverViewHolder viewHolder = (reciverViewHolder) holder;
            viewHolder.receivedMassage.setText(ChatsModel.getMessage());
            Picasso.get().load(receiverPhotoUrl).into(viewHolder.massageReceiverDP);


        }
    }

    @Override
    public int getItemCount() {
        return messagesAdpterArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatScreenModelClass messages = messagesAdpterArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderid())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECIVE;
        }
    }

    class  senderVierwHolder extends RecyclerView.ViewHolder {
        ImageView massageSenderDP;
        TextView sentMassage;
        public senderVierwHolder(@NonNull View itemView) {
            super(itemView);
            massageSenderDP = itemView.findViewById(R.id.sender_profile);
            sentMassage = itemView.findViewById(R.id.sender_massage);

        }
    }
    class reciverViewHolder extends RecyclerView.ViewHolder {
        ImageView massageReceiverDP;
        TextView receivedMassage;
        public reciverViewHolder(@NonNull View itemView) {
            super(itemView);
            massageReceiverDP = itemView.findViewById(R.id.receiver_profile);
            receivedMassage = itemView.findViewById(R.id.receiver_massage);
        }
    }
}
