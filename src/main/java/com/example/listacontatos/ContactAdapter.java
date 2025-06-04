package com.example.listacontatos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contactList;
    private OnContactLongClickListener longClickListener;
    private OnContactClickListener clickListener;

    public interface OnContactLongClickListener {
        void onContactLongClicked(Contact contact);
    }

    public interface OnContactClickListener {
        void onContactClicked(Contact contact);
    }

    public ContactAdapter(List<Contact> contactList, OnContactClickListener clickListener, OnContactLongClickListener longClickListener) {
        this.contactList = (contactList != null) ? contactList : new ArrayList<Contact>();
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_contact, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact currentContact = contactList.get(position);
        holder.nameTextView.setText(currentContact.getName());
        holder.phoneTextView.setText(currentContact.getPhone());

        if (currentContact.getEmail() != null && !currentContact.getEmail().isEmpty()) {
            holder.emailTextView.setText(currentContact.getEmail());
            holder.emailTextView.setVisibility(View.VISIBLE);
        } else {
            holder.emailTextView.setVisibility(View.GONE);
        }

        if (currentContact.getName() != null && !currentContact.getName().isEmpty()) {

            holder.avatarImageView.setImageResource(R.drawable.ic_contact_avatar_placeholder);
        }


        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onContactClicked(currentContact);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onContactLongClicked(currentContact);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void updateContacts(List<Contact> newContacts) {
        this.contactList.clear();
        if (newContacts != null) {
            this.contactList.addAll(newContacts);
        }
        notifyDataSetChanged();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView phoneTextView;
        TextView emailTextView;
        ImageView avatarImageView;

        ContactViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.textViewContactName);
            phoneTextView = view.findViewById(R.id.textViewContactPhone);
            emailTextView = view.findViewById(R.id.textViewContactEmail);
            avatarImageView = view.findViewById(R.id.imageViewContactAvatar);
        }
    }
}