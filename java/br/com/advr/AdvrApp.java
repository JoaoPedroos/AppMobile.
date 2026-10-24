package br.com.advr;
import android.app.Application;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/** Estado da sessão apenas em memória: fechar o processo exige novo login. */
public class AdvrApp extends Application {
 public final ExecutorService io = Executors.newSingleThreadExecutor();
 public volatile long userId = -1;
 public Repository repository;
 @Override public void onCreate() { super.onCreate(); repository = new Repository(this); }
}
