package br.com.advr;
import android.os.Bundle;
public class RegisterActivity extends BaseActivity {
 @Override protected void onCreate(Bundle state) {
  super.onCreate(state);setContentView(R.layout.activity_register);
  findViewById(R.id.back).setOnClickListener(v->finish());
  findViewById(R.id.save).setOnClickListener(v->save());
 }
 private void save() {
  String name=input(R.id.name),email=input(R.id.email),cpf=input(R.id.cpf),password=input(R.id.password);
  boolean bad=false;
  bad=error(R.id.name,Validar.nameError(name))||bad;
  bad=error(R.id.email,Validar.emailError(email))||bad;
  bad=error(R.id.cpf,Validar.cpfValid(cpf)?null:"Informe um CPF válido.")||bad;
  bad=error(R.id.password,Validar.passwordError(password))||bad;
  bad=error(R.id.confirm,password.equals(input(R.id.confirm))?null:"As senhas não coincidem.")||bad;
  if(bad)return;
  task(()->repo().register(name,email,cpf,password),id-> {
   new android.app.AlertDialog.Builder(this).setTitle("Cadastro realizado")
    .setMessage("Conta criada. Entre com seu e-mail e senha.")
    .setCancelable(false).setPositiveButton("Ir para login",(d,w)->loginScreen()).show();
  });
 }
}
