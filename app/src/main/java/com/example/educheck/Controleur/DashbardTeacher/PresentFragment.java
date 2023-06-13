package com.example.educheck.Controleur.DashbardTeacher;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.educheck.Modele.AcademicBackground;
import com.example.educheck.Modele.Cours;
import com.example.educheck.Modele.Implementation.DashboardImplementation;
import com.example.educheck.Modele.Interface.AsyncTaskcallback;
import com.example.educheck.Modele.Marks;
import com.example.educheck.Modele.Student;
import com.example.educheck.Modele.University;
import com.example.educheck.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PresentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PresentFragment extends Fragment implements AsyncTaskcallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TOKEN = "token";
    private static final String GET_ACADEMIC_BACKGROUNDS = "getAcademicBackground", ADD_ABS = "addAbsent",
            GET_USERS = "getUsers";
    private static final String UNIVERSITY = "university";

    // TODO: Rename and change types of parameters
    private String token;

    private Map<String, ArrayList<Cours>> allCourse;
    private University university;
    private String request;

    private String  idPath, idCourse, mailStudent;
    private Spinner spinnerDegree;
    private Spinner spinnerCareer;
    private Spinner spinnerCourses;

    private DashboardImplementation dashboardImplementation;
    private ArrayList<String> dataCareer;
    private ArrayList<String> dataCourses;
    private ArrayList<String> dataDegree;
    private ArrayList<AcademicBackground> academicBackgrounds;
    private ArrayList<Student> students;
    private ArrayList<Student> studentsFilter;
    private Button btnSend;
    private RecyclerView listView;
    private PresentAdapter presentAdapter;
    private RecyclerView.LayoutManager layoutManager;


    public PresentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param token      token session.
     * @param university University of teacher.
     * @return A new instance of fragment PresentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PresentFragment newInstance(String token, University university) {
        PresentFragment fragment = new PresentFragment();
        Bundle args = new Bundle();
        args.putString(TOKEN, token);
        args.putSerializable(UNIVERSITY, university);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            token = getArguments().getString(TOKEN);
            university = (University) getArguments().getSerializable(UNIVERSITY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_present, container, false);
        spinnerCareer = view.findViewById(R.id.spinnerCareerChoice);
        spinnerCourses = view.findViewById(R.id.spinnerCoursesChoice);
        btnSend=view.findViewById(R.id.btnAbsence);
        spinnerDegree = view.findViewById(R.id.spinnerDegreeChoice);
        listView = view.findViewById(R.id.listViewStudent);

        dashboardImplementation = new DashboardImplementation(this);
        allCourse = new HashMap<>();
        students= new ArrayList<>();
        studentsFilter = new ArrayList<>();
        dataCareer = new ArrayList<>();
        dataCourses = new ArrayList<>();
        dataDegree = new ArrayList<>();
        academicBackgrounds = new ArrayList<>();
        dataCareer.add("Select");
        dataCourses.add("Select");
        layoutManager = new LinearLayoutManager(getContext());
        listView.setLayoutManager(layoutManager);
        presentAdapter = new PresentAdapter(studentsFilter);
        listView.setAdapter(presentAdapter);

        ArrayAdapter<String> adapterDataCareer = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, dataCareer);
        adapterDataCareer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCareer.setAdapter(adapterDataCareer);

        ArrayAdapter<String> adapterDataCourses = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, dataCourses);
        adapterDataCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adapterDataCourses);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDegree.setAdapter(adapter);

        setOnItemSelectedSpinnerListener();

        sendRequest(GET_ACADEMIC_BACKGROUNDS);

        btnSend.setOnClickListener(v -> sendRequest(ADD_ABS));

        return view;
    }

    @Override
    public void onTaskCompleted(JSONArray items) throws JSONException {
        switch (request) {
            case GET_ACADEMIC_BACKGROUNDS:
                if (items.length() > 0 && !items.getJSONObject(0).has("status")) {
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject json = items.getJSONObject(i);
                        AcademicBackground parcour = new AcademicBackground(json.getString("name"), json.getString("type"),
                                null, json.getString("_id"), json.getString("referant"));
                        ArrayList<Cours> cours = new ArrayList<>();
                        JSONArray coursArray = json.getJSONArray("cours");
                        for (int j = 0; j < coursArray.length(); j++) {
                            JSONObject c = coursArray.getJSONObject(j);
                            Cours cc = new Cours(c.getString("name"), c.getString("profName"), Integer.parseInt(c.getString("credit")), c.getString("_id"));
                            cours.add(cc);
                        }
                        allCourse.put(json.getString("_id"), cours);
                        academicBackgrounds.add(parcour);
                    }
                }
                sendRequest(GET_USERS);
                break;
            case GET_USERS:
                if (items.length() > 0) {
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject user = items.getJSONObject(i);
                        JSONArray path = user.getJSONArray("path");
                        Student student = new Student(user.getString("firstName"), user.getString("lastName"), user.getString("mail"), user.getString("ine"), "Student");
                        for (int k = 0; k < path.length(); k++) {
                            JSONObject parcour = path.getJSONObject(k);
                            JSONArray cours = parcour.getJSONArray("cours");
                            student.addPath(parcour.getString("id"));
                            for (int j = 0; j < cours.length(); j++) {
                                JSONObject res = cours.getJSONObject(j);
                                student.addCour(res.getString("idCour"));
                            }
                        }
                        students.add(student);
                    }
                    studentsFilter.addAll(students);
                }
                break;
            case ADD_ABS:
                JSONObject response = items.getJSONObject(0);
                Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void sendRequest(String name) {
        this.request = name;

        switch (request){
            case GET_ACADEMIC_BACKGROUNDS:
                dashboardImplementation.getAllAcademicBackgrounds(university.getSuffixeTeacher());
                break;
            case GET_USERS:
                students.clear();
                dashboardImplementation.getUsers(token);
                break;
            case ADD_ABS:
                Calendar instance = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh/mm" );
                String formattedDate = sdf.format(instance.getTime());
                ArrayList mail = initSendDataAbs();
                dashboardImplementation.addAbs(token, mail, spinnerCourses.getSelectedItem().toString(), formattedDate);
                break;
        }
    }

    private ArrayList<String> initSendDataAbs(){
        ArrayList<String> mails = new ArrayList<>();
        ArrayList<PresentAdapter.StudentViewHolder> studentViewHolders = presentAdapter.getViewHolders();
        for(int i =0; i <studentViewHolders.size(); i++){
            if(studentViewHolders.get(i).checkBox.isChecked())
                mails.add(studentViewHolders.get(i).textViewMail.toString());
        }

        return mails;
    }

    private Student seekByMail(String mail){
        for(Student s : students){
            if(s.getMail().equals(mail))
                return s;
        }
        return null;
    }

    private void initialisationSpinnerPath(String typePath) {
        dataCareer.clear();
        dataCourses.clear();
        dataCourses.add("Select");
        dataCareer.add("Select");
        spinnerCareer.setSelection(0);
        spinnerCourses.setSelection(0);
        for (AcademicBackground aca : academicBackgrounds) {
            if (aca.getType().equals(typePath))
                dataCareer.add(aca.getName());
        }
    }
    private void initialisationSpinnerCourse(String idPath){
        dataCourses.clear();
        dataCourses.add("Select");
        if(allCourse.size()>0)
            for (Cours cour : allCourse.get(idPath)){
                dataCourses.add(cour.getName());
            }
    }
    private void initIdPath(String acaName){
        for (AcademicBackground aca : academicBackgrounds){
            if(aca.getName().equals(acaName))
                idPath = aca.get_id();
        }
    }
    private void initIdCourse(String courseName, String idPath){
        for(Cours cour : allCourse.get(idPath)){
            if(cour.getName().equals(courseName))
                idCourse = cour.get_id();
        }
    }
    private void filterStudent(String name, String id){
        studentsFilter.clear();
        studentsFilter.addAll(students);
        if(name.equals("path")){
            studentsFilter.removeIf(student -> !student.getPaths().contains(id));
        }else if(name.equals("course")) {
            studentsFilter.removeIf(student -> !student.getCours().contains(id));
        }
        presentAdapter = new PresentAdapter(studentsFilter);
        listView.setAdapter(presentAdapter);
    }

    private boolean isCourseSelected(){
        return !spinnerDegree.getSelectedItem().toString().equals("Select") &&
                !spinnerCareer.getSelectedItem().toString().equals("Select") &&
                !spinnerCourses.getSelectedItem().toString().equals("Select");
    }

    private void setOnItemSelectedSpinnerListener(){

        spinnerDegree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String typePath = spinnerDegree.getSelectedItem().toString();
                if(!typePath.equals("Select")) {
                    initialisationSpinnerPath(typePath);
                    filterStudent("type", "");
                }

                btnSend.setEnabled(isCourseSelected());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCareer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String acaBName = spinnerCareer.getSelectedItem().toString();
                if(!acaBName.equals("Select")) {
                    initIdPath(acaBName);
                    initialisationSpinnerCourse(idPath);
                    filterStudent("path", idPath);
                }
                btnSend.setEnabled(isCourseSelected());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerCourses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String courseName = spinnerCourses.getSelectedItem().toString();
                if(!courseName.equals("Select")) {
                    initIdCourse(courseName, idPath);
                    filterStudent("course", idCourse);
                }
                btnSend.setEnabled(isCourseSelected());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}