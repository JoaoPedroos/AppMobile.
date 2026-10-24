package br.com.advr;
import android.os.Bundle;
public class LoginActivity extends BaseActivity {
 @Override protected void onCreate(Bundle state) {
  super.onCreate(state);setContentView(R.layout.activity_login);
  findViewById(R.id.login).setOnClickListener(v-> {
   String email=input(R.id.email),senha=input(R.id.senha);
   if(error(R.id.email,Validar.emailError(email)))return;
   if(error(R.id.senha,senha.isEmpty()?"Informe a senha.":null))return;
   task(()->repo().login(email,senha),id->{app().userId=id;((android.widget.EditText)findViewById(R.id.senha)).setText("");go(DashboardActivity.class);finish();});
  });
  findViewById(R.id.toggle_senha).setOnClickListener(v->{
   android.widget.EditText senha=findViewById(R.id.senha);
   boolean hidden=senha.getTransformationMethod() instanceof android.text.method.senhaTransformationMethod;
   senha.setTransformationMethod(hidden?null:android.text.method.senhaTransformationMethod.getInstance());
   senha.setSelection(senha.length());((android.widget.TextView)v).setText(hidden?"Ocultar senha":"Mostrar senha");
  });
  findViewById(R.id.register).setOnClickListener(v->go(RegisterActivity.class));
 }
}
