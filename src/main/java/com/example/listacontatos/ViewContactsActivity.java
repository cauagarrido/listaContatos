package com.example.listacontatos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog; // Importação correta
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ViewContactsActivity extends AppCompatActivity implements ContactAdapter.OnContactLongClickListener, ContactAdapter.OnContactClickListener {

    private static final String TAG = "ViewContactsActivity";
    private RecyclerView recyclerViewContacts;
    private TextView textViewNoContacts;
    private DatabaseHelper dbHelper;
    private ContactAdapter adapter;

    private static final int REQUEST_CODE_ADD_CONTACT = 1;
    private static final int REQUEST_CODE_EDIT_CONTACT = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);

        MaterialToolbar toolbar = findViewById(R.id.toolbarViewContacts);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerViewContacts = findViewById(R.id.recyclerViewContacts);
        textViewNoContacts = findViewById(R.id.textViewNoContacts);
        dbHelper = new DatabaseHelper(this);

        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactAdapter(new ArrayList<>(), this, this);
        recyclerViewContacts.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAddContact);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(ViewContactsActivity.this, AddContactActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_CONTACT);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContactsInBackground();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadContactsInBackground() {
        Log.d(TAG, "Iniciando carregamento de contatos em background.");
        new LoadContactsTask(this).execute();
    }

    private void deleteContactInBackground(long contactId) {
        Log.d(TAG, "Iniciando remoção de contato em background. ID: " + contactId);
        new DeleteContactTask(this).execute(contactId);
    }

    @Override
    public void onContactClicked(Contact contact) {
        Log.d(TAG, "Clique simples no contato (RecyclerView): " + contact.getName());
        Intent intent = new Intent(ViewContactsActivity.this, EditContactActivity.class);
        intent.putExtra("CONTACT_ID", contact.getId());
        startActivityForResult(intent, REQUEST_CODE_EDIT_CONTACT);
    }

    @Override
    public void onContactLongClicked(Contact contact) {
        Log.d(TAG, "Clique longo no contato (RecyclerView): " + contact.getName());
        showOptionsDialog(contact);
    }

    private void showOptionsDialog(final Contact contact) {
        Log.d(TAG, "Mostrando opções para: " + contact.getName());
        // REMOVIDO o segundo argumento R.style... para usar o tema padrão do diálogo da activity
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Opções para: " + contact.getName());
        builder.setItems(new CharSequence[]{"Alterar", "Remover"},
                (dialog, which) -> {
                    switch (which) {
                        case 0: // Alterar
                            Log.d(TAG, "Opção Alterar selecionada para: " + contact.getName());
                            Intent intent = new Intent(ViewContactsActivity.this, EditContactActivity.class);
                            intent.putExtra("CONTACT_ID", contact.getId());
                            startActivityForResult(intent, REQUEST_CODE_EDIT_CONTACT);
                            break;
                        case 1: // Remover
                            Log.d(TAG, "Opção Remover selecionada para: " + contact.getName());
                            showDeleteConfirmationDialog(contact);
                            break;
                    }
                });
        builder.create().show();
    }

    private void showDeleteConfirmationDialog(final Contact contact) {
        Log.d(TAG, "Mostrando confirmação de remoção para: " + contact.getName());

        new AlertDialog.Builder(this)
                .setTitle("Remover Contato")
                .setMessage("Tem certeza que deseja remover " + contact.getName() + "?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    Log.d(TAG, "Confirmou remoção para: " + contact.getName());
                    deleteContactInBackground(contact.getId());
                })
                .setNegativeButton("Não", null)
                .setIcon(R.drawable.ic_delete_placeholder) // Certifique-se de ter este drawable
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if ((requestCode == REQUEST_CODE_ADD_CONTACT || requestCode == REQUEST_CODE_EDIT_CONTACT) && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Contato adicionado/atualizado, recarregando lista.");
            loadContactsInBackground();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static class LoadContactsTask extends AsyncTask<Void, Void, List<Contact>> {
        private WeakReference<ViewContactsActivity> activityReference;

        LoadContactsTask(ViewContactsActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<Contact> doInBackground(Void... voids) {
            ViewContactsActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing() || activity.dbHelper == null) {
                return null;
            }
            Log.d(TAG, "LoadContactsTask: doInBackground - Carregando do DB");
            return activity.dbHelper.getAllContacts();
        }

        @Override
        protected void onPostExecute(List<Contact> loadedContacts) {
            ViewContactsActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing() || activity.adapter == null) {
                return;
            }
            Log.d(TAG, "LoadContactsTask: onPostExecute - Contatos carregados: " + (loadedContacts != null ? loadedContacts.size() : "null"));
            if (loadedContacts == null || loadedContacts.isEmpty()) {
                activity.textViewNoContacts.setVisibility(View.VISIBLE);
                activity.recyclerViewContacts.setVisibility(View.GONE);
                activity.adapter.updateContacts(new ArrayList<>());
            } else {
                activity.textViewNoContacts.setVisibility(View.GONE);
                activity.recyclerViewContacts.setVisibility(View.VISIBLE);
                activity.adapter.updateContacts(loadedContacts);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static class DeleteContactTask extends AsyncTask<Long, Void, Boolean> {
        private WeakReference<ViewContactsActivity> activityReference;

        DeleteContactTask(ViewContactsActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Boolean doInBackground(Long... params) {
            ViewContactsActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing() || activity.dbHelper == null) {
                return false;
            }
            long contactId = params[0];
            Log.d(TAG, "DeleteContactTask: doInBackground - Deletando contato ID: " + contactId);
            return activity.dbHelper.deleteContact(contactId);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            ViewContactsActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            if (success) {
                Log.d(TAG, "DeleteContactTask: onPostExecute - Contato removido com sucesso.");
                Toast.makeText(activity, "Contato removido.", Toast.LENGTH_SHORT).show();
                activity.loadContactsInBackground();
            } else {
                Log.e(TAG, "DeleteContactTask: onPostExecute - Erro ao remover contato.");
                Toast.makeText(activity, "Erro ao remover contato.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}