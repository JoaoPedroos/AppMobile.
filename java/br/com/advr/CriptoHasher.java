package br.com.advr;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
/** PBKDF2 com salt aleatório por senha; nunca armazena senha em texto puro. */
public final class CriptoHasher {
 private static final int ITERATIONS=600000;
 private CriptoHasher() {}
 public static String hash(String senha) {
  byte[] salt=new byte[16]; new SecureRandom().nextBytes(salt);
  return ITERATIONS+":"+Base64.getEncoder().encodeToString(salt)+":"+Base64.getEncoder().encodeToString(derive(senha,salt,ITERATIONS));
 }
 public static boolean matches(String senha,String stored) {
  try {
   String[] p=stored.split(":"); int count=Integer.parseInt(p[0]);
   if(count<1 || count>2000000)return false;
   return MessageDigest.isEqual(Base64.getDecoder().decode(p[2]),derive(senha,Base64.getDecoder().decode(p[1]),count));
  } catch(RuntimeException e) { return false; }
 }
 private static byte[] derive(String senha,byte[] salt,int count) {
  char[] chars=senha.toCharArray(); PBEKeySpec spec=new PBEKeySpec(chars,salt,count,256);
  try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); }
  catch(Exception e) { throw new IllegalStateException("Não é possivel proteger a senha.",e); }
  finally { spec.clearPassword(); Arrays.fill(chars,'\0'); }
 }
}
