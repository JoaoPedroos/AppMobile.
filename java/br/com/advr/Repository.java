package br.com.advr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

/** Cadastro e autenticação local. Executado em segundo plano pelo aplicativo. */
public class Repository {
    private final DbHelper helper;
    public Repository(Context context) { helper = new DbHelper(context); }
    private SQLiteDatabase db() { return helper.getWritableDatabase(); }
    public String userName(long id) {
        try (Cursor c = db().rawQuery("SELECT nome FROM usuarios WHERE id=?", new String[]{String.valueOf(id)})) {
            if (!c.moveToFirst()) throw new IllegalArgumentException("Sua sessão expirou. Entre novamente.");
            return c.getString(0);
        }
    }
    public long register(String name,String email,String cpf,String password) {
        String problem=Validators.nameError(name);
        if(problem==null)problem=Validators.emailError(email);
        if(problem==null && !Validators.cpfValid(cpf))problem="Informe um CPF válido.";
        if(problem==null)problem=Validators.passwordError(password);
        if(problem!=null)throw new IllegalArgumentException(problem);
        ContentValues values=new ContentValues();
        values.put("nome",name.trim()); values.put("email",Validators.email(email)); values.put("cpf",Validators.digits(cpf));
        values.put("senha_hash",PasswordHasher.hash(password)); values.put("perfil","CLIENTE");
        SQLiteDatabase d=db(); d.beginTransaction();
        try {
            long id=d.insertOrThrow("usuarios",null,values);
            d.setTransactionSuccessful();return id;
        } catch(SQLiteConstraintException e) { throw new IllegalArgumentException("Este email/CPF já está em uso."); }
        finally { d.endTransaction(); }
    }
    public long login(String email,String password) {
        SQLiteDatabase d=db(); long id,until; int failures; String hash;
        try(Cursor c=d.rawQuery("SELECT id,senha_hash,tentativas_login,bloqueado_ate FROM usuarios WHERE email=?",new String[]{Validators.email(email)})) {
            if(!c.moveToFirst())throw new IllegalArgumentException("Email ou senha incorretos.");
            id=c.getLong(0); hash=c.getString(1); failures=c.getInt(2); until=c.getLong(3);
        }
        long now=System.currentTimeMillis();
        if(until>now)throw new IllegalArgumentException("Conta bloqueada. Tente novamente em "+Math.max(1,(until-now+59999)/60000)+" minuto(s).");
        if(until!=0)failures=0;
        ContentValues v=new ContentValues();
        if(!PasswordHasher.matches(password,hash)) {
            failures++;v.put("tentativas_login",failures>=5?0:failures);v.put("bloqueado_ate",failures>=5?now+300000:0);
            d.update("usuarios",v,"id=?",new String[]{""+id});
            throw new IllegalArgumentException(failures>=5?"Conta bloqueada por 5 minutos após 5 erros seguidos.":"Email ou senha incorretos.");
        }
        v.put("tentativas_login",0);v.put("bloqueado_ate",0);d.update("usuarios",v,"id=?",new String[]{""+id});return id;
    }
}
