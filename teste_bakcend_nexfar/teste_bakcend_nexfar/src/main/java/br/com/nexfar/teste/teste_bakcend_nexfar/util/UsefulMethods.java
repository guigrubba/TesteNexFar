package br.com.nexfar.teste.teste_bakcend_nexfar.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UsefulMethods {

    public static String formatDate(LocalDateTime dateTime){
        DateTimeFormatter formate = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedDate = formate.format(dateTime);

        return formattedDate;
    }

}
