package br.com.advr;
import java.util.Locale;
import java.util.regex.Pattern;
public final class Validators {
 private Validators() {}
 public static String digits(String s) { return s.replaceAll("[^0-9]", ""); }
 public static String email(String s) { return s.trim().toLowerCase(Locale.ROOT); }
 public static String nameError(String s) { return s.trim().length() < 3 ? "Informe um nome com pelo menos 3 caracteres." : null; }
 public static String emailError(String s) { return Pattern.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+",s.trim()) ? null : "Informe um e-mail válido."; }
 public static String passwordError(String s) { return s.length() < 8 ? "A senha deve ter pelo menos 8 caracteres." : null; }
 public static boolean cpfValid(String value) {
  String s=digits(value);
  if(s.length()!=11 || s.matches("(\\d)\\1{10}")) return false;
  for(int end=9;end<=10;end++) {
   int sum=0; for(int i=0;i<end;i++) sum+=(s.charAt(i)-'0')*(end+1-i);
   int digit=(sum*10)%11; if(digit==10)digit=0;
   if(digit!=s.charAt(end)-'0')return false;
  }
  return true;
 }
}
