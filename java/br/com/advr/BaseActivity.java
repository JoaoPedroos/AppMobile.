package br.com.advr;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Build;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import java.util.concurrent.Callable;

/** Executa banco e hash em segundo plano para não travar a tela. */
public class BaseActivity extends Activity {
 protected boolean working;
 protected AdvrApp app() { return (AdvrApp)getApplication(); }
 protected Repository repo() { return app().repository; }
 @Override protected void onCreate(Bundle state) {
  super.onCreate(state);
 }
 @Override public void setContentView(int resource) {
  super.setContentView(resource);
  if(Build.VERSION.SDK_INT>=30) {
   getWindow().setDecorFitsSystemWindows(false);
   View root=((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
   root.setOnApplyWindowInsetsListener((v,insets)-> {
    android.graphics.Insets bars=insets.getInsets(WindowInsets.Type.systemBars()|WindowInsets.Type.ime());
    v.setPadding(bars.left,bars.top,bars.right,bars.bottom); return insets;
   });root.requestApplyInsets();
  }
 }
 protected String input(int id) { return ((EditText)findViewById(id)).getText().toString(); }
 protected boolean error(int id,String error) {
  EditText view=findViewById(id);view.setError(error);if(error!=null)view.requestFocus();return error!=null;
 }
 protected void message(String text) {
  TextView box=findViewById(R.id.message);box.setText(text);box.setVisibility(text==null||text.isEmpty()?View.GONE:View.VISIBLE);
 }
 protected void go(Class<?> target) { startActivity(new Intent(this,target)); }
 protected void loginScreen() {
  app().userId=-1;
  startActivity(new Intent(this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));finish();
 }
 protected void report(String title,String text) {
  TextView body=new TextView(this);body.setText(text);body.setTextSize(16);body.setTextIsSelectable(true);body.setPadding(24,16,24,16);
  android.widget.ScrollView scroll=new android.widget.ScrollView(this);scroll.addView(body);
  new AlertDialog.Builder(this).setTitle(title).setView(scroll).setPositiveButton("Fechar",null).show();
 }
 protected interface Result<T> { void accept(T value); }
 protected <T> void task(Callable<T> work,Result<T> success) { task(work,success,e->message(e.getMessage()==null?"Não foi possível concluir a operação.":e.getMessage())); }
 protected <T> void task(Callable<T> work,Result<T> success,Result<Exception> failure) {
  if(working)return; working=true;setBusy(true);message("");
  app().io.execute(()-> {
   try { T result=work.call();runOnUiThread(()-> {if(isFinishing()||isDestroyed())return;working=false;setBusy(false);success.accept(result);}); }
   catch(Exception e) { runOnUiThread(()-> {if(isFinishing()||isDestroyed())return;working=false;setBusy(false);failure.accept(e);}); }
  });
 }
 private void setBusy(boolean busy) {
  View p=findViewById(R.id.progress);if(p!=null)p.setVisibility(busy?View.VISIBLE:View.GONE);
  if(busy)getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
  else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
 }
 @Override public void onBackPressed() { if(!working)super.onBackPressed(); }
}
