    package com.example.educheck.Controleur.Dashboard;
            import android.Manifest;
            import android.content.Intent;
            import android.database.Cursor;
            import android.graphics.Bitmap;
            import android.graphics.BitmapFactory;
            import android.graphics.drawable.BitmapDrawable;
            import android.graphics.drawable.Drawable;
            import android.net.Uri;
            import android.os.Bundle;
            import android.provider.MediaStore;
            import android.view.LayoutInflater;
            import android.view.View;
            import android.view.ViewGroup;
            import android.widget.Button;
            import android.widget.ImageView;

            import androidx.activity.result.ActivityResultLauncher;
            import androidx.core.app.ActivityCompat;
            import androidx.fragment.app.Fragment;

            import com.example.educheck.Controleur.DashboardAdmin.GalleryActivityResultContract;
            import com.example.educheck.Modele.Implementation.DashboardImplementation;
            import com.example.educheck.Modele.Interface.AsyncTaskcallback;
            import com.example.educheck.R;

            import org.json.JSONArray;
            import org.json.JSONException;
            import org.json.JSONObject;

            import java.io.ByteArrayOutputStream;

    public class FragJustification2 extends Fragment implements AsyncTaskcallback {

        private static final String TOKEN = "token";
        private static final String ARG_JUST = "id_justification";
        private static final String PROF_NAME="profName";
        private static final String NAME_COURS="nameCours";
        private static final int STORAGE_PERMISSION_CODE = 24;
        private static final int REQUEST_CODE_PERMISSION = 23;

        private View view;
        private Button btnSelectFile;

        private Button btnUploadFile;

        private ImageView image;

        private DashboardImplementation request;

        private ActivityResultLauncher<Void> galleryLauncher;
        String id_j;
        String token;

        String profName;
        String nameCours;

        String mail;

        public FragJustification2(){

        }
        public static FragJustification2 newInstance(String token, String id_justification,String  profName,String nameCours) {
            FragJustification2 fragment = new FragJustification2();
            Bundle args = new Bundle();
            args.putString(TOKEN, token);
            args.putString(ARG_JUST, id_justification);
            args.putString(PROF_NAME,profName);
            args.putString(NAME_COURS,nameCours);
            fragment.setArguments(args);
            return fragment;
        }

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ActivityCompat.requestPermissions(getActivity(),new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
            if (getArguments() != null) {
                token = getArguments().getString(TOKEN);
                id_j = getArguments().getString(ARG_JUST);
                profName=getArguments().getString(PROF_NAME);
                nameCours=getArguments().getString(NAME_COURS);
                System.out.println("token "+ token+" id_j: "+id_j+" profName: "+profName+" nameCours: "+nameCours);

            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_justification, container, false);

            btnSelectFile = view.findViewById(R.id.btnSelectFile);
            image = view.findViewById(R.id.imgJ);

            btnUploadFile = view.findViewById(R.id.btnUpload); // Initialize the btnUploadFile button

            btnSelectFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    galleryLauncher.launch(null);
                }
            });
            btnUploadFile.setOnClickListener(v -> {
                saveToBDD();
            });

            galleryLauncher = registerForActivityResult(new GalleryActivityResultContract(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Intent.ACTION_PICK), result -> {
                if (result != null) {
                    String[] filePathColumn = {MediaStore.Downloads.DATA};
                    Cursor cursor = requireContext().getContentResolver().query((Uri) result, filePathColumn, null, null, null);
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String imgDecodableString = cursor.getString(columnIndex);
                            image.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
                        }
                        cursor.close();
                    }
                }
            });
            return view;
        }

        public void saveToBDD(){
            Justificatif justif;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Bitmap bitmap = null;
            Drawable drawable = image.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] img_Justificatif = byteArrayOutputStream.toByteArray();
            request = new DashboardImplementation(this);
            request.Justify(id_j,token,mail,profName,img_Justificatif);
           // request.postJustificatif(this.token, justif);  a faire
        }



        @Override
        public void onTaskCompleted(JSONArray items) throws JSONException {
            if(!items.getJSONObject(0).has("code_retour")) {
                JSONObject response = items.getJSONObject(0);
                System.out.println(response.toString());
            }

        }
    }