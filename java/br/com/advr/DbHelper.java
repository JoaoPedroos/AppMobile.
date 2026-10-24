package br.com.advr;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
public class DbHelper extends SQLiteOpenHelper {
 public static final String NAME="advr.db";
 public static final int VERSION=1;
 private final Context context;
 public DbHelper(Context context) { super(context,NAME,null,VERSION); this.context=context.getApplicationContext(); }
 @Override public void onConfigure(SQLiteDatabase db) { super.onConfigure(db); db.setForeignKeyConstraintsEnabled(true); }
 @Override public void onCreate(SQLiteDatabase db) {
  try(InputStream in=context.getAssets().open("banco_advr.sql"); ByteArrayOutputStream out=new ByteArrayOutputStream()) {
   byte[] buffer=new byte[4096]; int n; while((n=in.read(buffer))!=-1)out.write(buffer,0,n);
   String script=new String(out.toByteArray(),StandardCharsets.UTF_8).replaceAll("(?m)^--.*$", "");
   // O schema v1 contém somente instruções simples, sem triggers ou ';' em literais.
   for(String command:script.split(";")) if(!command.trim().isEmpty())db.execSQL(command.trim());
  } catch(Exception e) { throw new IllegalStateException("Falha ao criar o banco SQLite.",e); }
 }
 @Override public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {
  // Nenhuma migração é necessária na primeira versão. Nunca apagar tabelas aqui.
  throw new IllegalStateException("Falta implementar a migração de "+oldVersion+" para "+newVersion);
 }
}
