package performancelab;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
    if (args.length<2)
        throw new IllegalArgumentException();

        String arg1=args[0];
        String arg2=args[1];

        arg1=arg1.replaceAll("\\*+",".*");
        Pattern p= Pattern.compile(arg1);
        Matcher m=p.matcher(arg2);
        if (m.matches())
            System.out.println("OK");
        else
            System.out.println("KO");
    }
}
