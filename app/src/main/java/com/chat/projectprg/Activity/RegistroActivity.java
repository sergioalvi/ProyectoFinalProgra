package com.chat.projectprg.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chat.projectprg.Entidades.Usuario;
import com.chat.projectprg.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.Reference;


public class RegistroActivity extends AppCompatActivity {

    private EditText txtNombre, txtCorreo, txtContraseña, txtContraseñaRepetida;
    private Button btnRegistrar;

    //Registro de usuarios en Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        txtNombre = (EditText) findViewById(R.id.idRegistroNombre);
        txtCorreo = (EditText) findViewById(R.id.idRegistroCorreo);
        txtContraseña = (EditText) findViewById(R.id.idRegistroContraseña);
        txtContraseñaRepetida = (EditText) findViewById(R.id.idRegistroContraseñaRepetida);
        btnRegistrar = (Button) findViewById(R.id.idRegistroRegistrar);


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        //Registro
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String correo = txtCorreo.getText().toString();
                final String nombre = txtNombre.getText().toString();

                if(isValidEmail(correo) && validarContraseña() && validarNombre(nombre)){
                    String contraseña = txtContraseña.getText().toString();


                    mAuth.createUserWithEmailAndPassword(correo, contraseña)
                            .addOnCompleteListener(RegistroActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegistroActivity.this, "Registrado Correctamente", Toast.LENGTH_SHORT).show();

                                        //seteo de datos en Firebase chat
                                        Usuario usuario = new Usuario();
                                        usuario.setCorreo(correo);
                                        usuario.setNombre(nombre);
                                        FirebaseUser currentUser = mAuth.getCurrentUser();
                                        DatabaseReference reference = database.getReference("Usuario/"+currentUser.getUid());
                                        reference.setValue(usuario);
                                        finish();
                                        // Sign in success, update UI with the signed-in user's information

                                    } else {
                                        Toast.makeText(RegistroActivity.this, "Error al registrarse", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }else{
                    Toast.makeText(RegistroActivity.this, "Validacion Correcta", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean validarContraseña() {
        String contraseña, contraseñaRepetida;
        contraseña = txtContraseña.getText().toString();
        contraseñaRepetida = txtContraseñaRepetida.getText().toString();

        if (contraseña.equals(contraseñaRepetida)) {
            if (contraseña.length() >= 6 && contraseña.length() <= 16) {
                return true;
            }else return  false;
        }else return  false;

    }

    public boolean validarNombre(String nombre){
     return  !nombre.isEmpty();
    }
}
