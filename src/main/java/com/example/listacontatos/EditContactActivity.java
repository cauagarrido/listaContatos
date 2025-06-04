package com.example.listacontatos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.lang.ref.WeakReference;

public class EditContactActivity extends AppCompatActivity {
    private static final String TAG = "EditContactActivity";
    private TextInputEditText editTextEditContactName;
    private TextInputEditText editTextEditContactPhone;
    private TextInputEditText editTextEditContactEmail;
    private MaterialButton buttonUpdateContact;
    private DatabaseHelper dbHelper;
    private Contact currentContact;
    private long contactId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_contact);

        MaterialToolbar toolbar = findViewById(R.id.toolbarEditContact);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        editTextEditContactName = findViewById(R.id.editTextEditContactName);
        editTextEditContactPhone = findViewById(R.id.editTextEditContactPhone);
        editTextEditContactEmail = findViewById(R.id.editTextEditContactEmail);
        buttonUpdateContact = findViewById(R.id.buttonUpdateContact);

        dbHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("CONTACT_ID")) {
            contactId = intent.getLongExtra("CONTACT_ID", -1);
        }

        if (contactId == -1) {
            Toast.makeText(this, "Erro: ID do contato inválido.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Log.d(TAG, "Carregando contato para edição em background. ID: " + contactId);
        new GetContactTask(this).execute(contactId);

        buttonUpdateContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateContactData();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void populateUIWithContact(Contact contact) {
        currentContact = contact;
        if (currentContact != null) {
            Log.d(TAG, "Populando UI com dados do contato: " + currentContact.getName());
            editTextEditContactName.setText(currentContact.getName());
            editTextEditContactPhone.setText(currentContact.getPhone());
            editTextEditContactEmail.setText(currentContact.getEmail());
        } else {
            Log.e(TAG, "Não foi possível popular UI, contato nulo. ID: " + contactId);
            Toast.makeText(this, "Erro: Contato não encontrado para edição.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void updateContactData() {
        if (currentContact == null) {
            Toast.makeText(EditContactActivity.this, "Erro: Dados do contato não carregados.", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = editTextEditContactName.getText().toString().trim();
        String phone = editTextEditContactPhone.getText().toString().trim();
        String email = editTextEditContactEmail.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(EditContactActivity.this, "Nome e Telefone são obrigatórios.", Toast.LENGTH_SHORT).show();
            return;
        }

        currentContact.setName(name);
        currentContact.setPhone(phone);
        currentContact.setEmail(email);

        Log.d(TAG, "Iniciando atualização de contato em background: " + name);
        new UpdateContactTask(this).execute(currentContact);
    }

    @SuppressLint("StaticFieldLeak")
    private static class GetContactTask extends AsyncTask<Long, Void, Contact> {
        private WeakReference<EditContactActivity> activityReference;

        GetContactTask(EditContactActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Contact doInBackground(Long... ids) {
            EditContactActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing() || activity.dbHelper == null) return null;
            Log.d(TAG, "GetContactTask: doInBackground - Buscando contato ID: " + ids[0]);
            return activity.dbHelper.getContact(ids[0]);
        }

        @Override
        protected void onPostExecute(Contact contact) {
            EditContactActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            activity.populateUIWithContact(contact);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static class UpdateContactTask extends AsyncTask<Contact, Void, Integer> {
        private WeakReference<EditContactActivity> activityReference;

        UpdateContactTask(EditContactActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Integer doInBackground(Contact... contacts) {
            EditContactActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing() || activity.dbHelper == null) return 0;
            Log.d(TAG, "UpdateContactTask: doInBackground - Atualizando no DB");
            return activity.dbHelper.updateContact(contacts[0]);
        }

        @Override
        protected void onPostExecute(Integer rowsAffected) {
            EditContactActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            if (rowsAffected > 0) {
                Log.d(TAG, "UpdateContactTask: onPostExecute - Contato atualizado com sucesso.");
                Toast.makeText(activity, "Contato atualizado!", Toast.LENGTH_SHORT).show();
                activity.setResult(Activity.RESULT_OK);
                activity.finish();
            } else {
                Log.e(TAG, "UpdateContactTask: onPostExecute - Erro ao atualizar contato.");
                Toast.makeText(activity, "Erro ao atualizar contato.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}