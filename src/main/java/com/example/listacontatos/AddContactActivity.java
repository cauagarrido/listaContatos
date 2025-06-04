package com.example.listacontatos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText; // Usar TextInputEditText
import java.lang.ref.WeakReference;

public class AddContactActivity extends AppCompatActivity {
    private static final String TAG = "AddContactActivity";
    private TextInputEditText editTextContactName;
    private TextInputEditText editTextContactPhone;
    private TextInputEditText editTextContactEmail;
    private MaterialButton buttonSaveContact;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        MaterialToolbar toolbar = findViewById(R.id.toolbarAddContact);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }



        editTextContactName = findViewById(R.id.editTextContactName);
        editTextContactPhone = findViewById(R.id.editTextContactPhone);
        editTextContactEmail = findViewById(R.id.editTextContactEmail);
        buttonSaveContact = findViewById(R.id.buttonSaveContact);

        dbHelper = new DatabaseHelper(this);

        buttonSaveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContact();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void saveContact() {
        String name = editTextContactName.getText().toString().trim();
        String phone = editTextContactPhone.getText().toString().trim();
        String email = editTextContactEmail.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(AddContactActivity.this, "Nome e Telefone são obrigatórios.", Toast.LENGTH_SHORT).show();
            return;
        }

        Contact newContact = new Contact(name, phone, email);
        Log.d(TAG, "Iniciando salvamento de contato em background: " + name);
        new AddContactTask(this).execute(newContact);
    }

    @SuppressLint("StaticFieldLeak")
    private static class AddContactTask extends AsyncTask<Contact, Void, Long> {
        private WeakReference<AddContactActivity> activityReference;

        AddContactTask(AddContactActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Long doInBackground(Contact... contacts) {
            AddContactActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing() || activity.dbHelper == null) return -1L;
            Log.d(TAG, "AddContactTask: doInBackground - Salvando no DB");
            return activity.dbHelper.addContact(contacts[0]);
        }

        @Override
        protected void onPostExecute(Long id) {
            AddContactActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            if (id != -1) {
                Log.d(TAG, "AddContactTask: onPostExecute - Contato salvo com ID: " + id);
                Toast.makeText(activity, "Contato salvo!", Toast.LENGTH_SHORT).show();
                activity.setResult(Activity.RESULT_OK); // Informa que a operação foi bem-sucedida
                activity.finish();
            } else {
                Log.e(TAG, "AddContactTask: onPostExecute - Erro ao salvar contato.");
                Toast.makeText(activity, "Erro ao salvar contato.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}