package com.example.educheck.Controleur.Registration;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.educheck.Modele.Implementation.InscriptionImplementation;
import com.example.educheck.Modele.Interface.AsyncTaskcallback;
import com.example.educheck.Modele.Student;
import com.example.educheck.Modele.University;
import com.example.educheck.R;

import org.json.JSONArray;
import org.json.JSONException;


public class Registration2 extends AppCompatActivity implements AsyncTaskcallback {
    private TextView TextView_AddInformation;
    private TextView TextView_EmailIneStatus;
    private AutoCompleteTextView EditText_email;
    private EditText EditText_INE;
    private TextView TextView_status;
    private Button Button_Submit;
    private Student student;
    private University university;
    private InscriptionImplementation inscription;
    private Spinner spinner;
    private List ChoiceList;
    private Intent intentParcours_choice;
    private ArrayList<String> mailsSuffixeFound;
    private ArrayAdapter<String> adapterAutoComplete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);
        spinner =findViewById(R.id.status_spinner);
        ChoiceList = new ArrayList();
        ChoiceList.add("Student");
        ChoiceList.add("Teacher");
        ArrayAdapter adapter = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                ChoiceList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);



        TextView_EmailIneStatus = findViewById(R.id.main_textviewEmailIneStatus);
        TextView_AddInformation = findViewById(R.id.main_textviewAddInformation);
        EditText_email = findViewById(R.id.main_EditTextemail);
        EditText_INE = findViewById(R.id.main_EditTextIne);
        TextView_status = findViewById(R.id.main_TextViewStatus);
        Button_Submit = findViewById(R.id.main_button_Submit);
        Button_Submit.setEnabled(false);
        intentParcours_choice = new Intent(getApplicationContext(), ParcoursChoices.class);



        EditText_INE.addTextChangedListener(emailIneStatusWatcher);
        TextView_status.addTextChangedListener(emailIneStatusWatcher);
        student = (Student) getIntent().getSerializableExtra("student");
        inscription = new InscriptionImplementation(this);
        university = (University) getIntent().getSerializableExtra("university");
        mailsSuffixeFound = new ArrayList<>();

        adapterAutoComplete = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mailsSuffixeFound);
        EditText_email.setAdapter(adapterAutoComplete);
        EditText_email.addTextChangedListener(emailIneStatusWatcher);

        Button_Submit.setOnClickListener(v -> {
           student.setIne( EditText_INE.getText().toString());
           student.setStatus(spinner.getSelectedItem().toString());
           student.setMail(EditText_email.getText().toString());
            intentParcours_choice.putExtra("student", student);
            intentParcours_choice.putExtra("university",university);
            inscription.registerOnUniversity(university,student);
            startActivity(intentParcours_choice);
        });
    }
        private final TextWatcher  emailIneStatusWatcher= new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString();

                List<String> suggestions = new ArrayList<>();
                char triggerChar = '@';
                if (text.contains(String.valueOf(triggerChar))) {
                    System.out.println(mailsSuffixeFound.size());
                    String teacher = text.substring(0, text.indexOf(triggerChar) + 1) + university.getSuffixeTeacher();
                    suggestions.add(teacher);
                    String student = text.substring(0, text.indexOf(triggerChar) + 1) + university.getSuffixeStudent();
                    suggestions.add(student);


                }

                // Mettez à jour les suggestions dans l'adaptateur
                adapterAutoComplete.clear();
                adapterAutoComplete.addAll(suggestions);
                adapterAutoComplete.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Button_Submit.setEnabled(EditText_INE.getText().length() == 11
                        && Patterns.EMAIL_ADDRESS.matcher(EditText_email.getText()).matches()
                );
            }
        };

    @Override
    public void onTaskCompleted(JSONArray items) throws JSONException {

    }

}







