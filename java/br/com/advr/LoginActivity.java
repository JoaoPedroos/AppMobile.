package br.com.advr;
import android.os.Bundle;
public class LoginActivity extends BaseActivity {
 @Override protected void onCreate(Bundle state) {
  super.onCreate(state);setContentView(R.layout.activity_login);
  findViewById(R.id.login).setOnClickListener(v-> {
   String email=input(R.id.email),password=input(R.id.password);
   if(error(R.id.email,Validators.emailError(email)))return;
   if(error(R.id.password,password.isEmpty()?"Informe a senha.":null))return;
   task(()->repo().login(email,password),id->{app().userId=id;((android.widget.EditText)findViewById(R.id.password)).setText("");go(DashboardActivity.class);finish();});
  });
  findViewById(R.id.toggle_password).setOnClickListener(v->{
   android.widget.EditText password=findViewById(R.id.password);
   boolean hidden=password.getTransformationMethod() instanceof android.text.method.PasswordTransformationMethod;
   password.setTransformationMethod(hidden?null:android.text.method.PasswordTransformationMethod.getInstance());
   password.setSelection(password.length());((android.widget.TextView)v).setText(hidden?"Ocultar senha":"Mostrar senha");
  });
  findViewById(R.id.register).setOnClickListener(v->go(RegisterActivity.class));
 }
}
