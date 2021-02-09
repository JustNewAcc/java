package performancelab;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {

        if (args.length<3){
            System.out.println("Usage: [APPLICATION_NAME] [PATH_TO_LOG_FILE.log] [BEGINNING_DATE] [ENDING_DATE]");
            System.out.println("Example: java –jar task3.jar ./log.log 2020-01-01T12:00:00 2020-01-01T13:00:00");
            return;
        }

        String path=args[0];

        String tim1=args[1].replaceAll("[^0-9]", "");
        String tim2=args[2].replaceAll("[^0-9]", "");

        if (tim1.length()==14)
            tim1+="000";
        if (tim2.length()==14)
            tim2+="000";



        int dat1=Integer.parseInt(tim1.substring(0,8));
        int time1=Integer.parseInt(tim1.substring(8,17));
        int dat2=Integer.parseInt(tim2.substring(0,8));
        int time2=Integer.parseInt(tim2.substring(8,17));

        try {
            Stream<String> str2=Files.lines(Paths.get(path));
            Stream<String> str3=Files.lines(Paths.get(path));

            int beginningValue=Integer.parseInt(str2.filter(s->s.contains("текущий объем")).findFirst().get().replaceAll("[^0-9]", ""));

            Map<String,String> map=str3.filter(s->s.contains("успех") || s.contains("фейл"))
                    .collect(Collectors.toMap(s->s.substring(0,24).replaceAll("[^0-9]", ""), s->s.split("wanna")[1]));


            String currentKey="";
            String currentVal="";
            for (Map.Entry<String,String> es:map.entrySet()){
                currentKey=es.getKey();
                currentVal=es.getValue();
                if (currentVal.contains("успех") && Integer.parseInt(currentKey.substring(0,8))<dat1
                            || ( Integer.parseInt(currentKey.substring(0,8))==dat1
                            && Integer.parseInt(currentKey.substring(8,17))<=time1)){
                    if (currentVal.contains("top")){
                        beginningValue+=Integer.parseInt(currentVal.replaceAll("[^0-9]", ""));
                    }
                    else if (es.getValue().contains("scoop")){
                        beginningValue-=Integer.parseInt(currentVal.replaceAll("[^0-9]", ""));
                    }
                }
            }

            int endingValue=beginningValue;

            double failFillTries=0.0;
            int commFillTries=0;
            int filledVol=0;
            int failFillVol=0;

            double failTakeTries=0.0;
            int commTakeTries=0;
            int TakenVol=0;
            int failTakeVol=0;

            Map<String,String> sortedMap=
                    map.keySet().stream().filter(s->(Integer.parseInt(s.substring(0,8))>dat1
                            || (Integer.parseInt(s.substring(0,8))==dat1 && Integer.parseInt(s.substring(8,17))>=time1)))

                            .filter(s->(Integer.parseInt(s.substring(0,8))<dat2)
                            || (Integer.parseInt(s.substring(0,8))==dat2 && Integer.parseInt(s.substring(8,17))<=time2))
                            .collect(Collectors.toMap(s->s,s->map.get(s)));

            for (Map.Entry<String,String> es:sortedMap.entrySet()){
                currentVal=es.getValue();
                if (currentVal.contains("top")){
                    commFillTries++;
                    if (currentVal.contains("фейл")){
                        failFillTries++;
                        failFillVol+=Integer.parseInt(currentVal.replaceAll("[^0-9]", ""));
                    }
                    else{
                        filledVol+=Integer.parseInt(currentVal.replaceAll("[^0-9]", ""));
                    }
                }
                else{
                    commTakeTries++;
                    if (currentVal.contains("фейл")){
                        failTakeTries++;
                        failTakeVol+=Integer.parseInt(currentVal.replaceAll("[^0-9]", ""));
                    }
                    else{
                        TakenVol+=Integer.parseInt(currentVal.replaceAll("[^0-9]", ""));
                    }
                }
            }


            endingValue=endingValue+filledVol-TakenVol;


            try (OutputStream os = new FileOutputStream("F:/test.csv"); PrintWriter w = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));){
                os.write(239);
                os.write(187);
                os.write(191);

                w.write("количество попыток налить;");
                w.write("процент ошибок при налитии;");
                w.write("налитый объем воды;");
                w.write("неналитый объем воды;");

                w.write("количество попыток забрать;");
                w.write("процент ошибок при заборе;");
                w.write("забранный объем воды;");
                w.write("незабранный объем воды;");

                w.write("объем воды в бочке в начале периода;");
                w.write("объем воды в бочке в конце периода;");
                w.println();
                w.flush();

                w.print(commFillTries);
                w.write(";");
                w.print(String.valueOf((Math.round(failFillTries/commFillTries*100))));
                w.write(";");
                w.print(filledVol);
                w.write(";");
                w.print(failFillVol);
                w.write(";");

                w.print(commTakeTries);
                w.write(";");
                w.write(String.valueOf(Math.round(failFillTries/commFillTries*100)));
                w.write(";");
                w.print(TakenVol);
                w.write(";");
                w.print(failTakeVol);
                w.write(";");

                w.print(beginningValue);
                w.write(";");
                w.print(endingValue);

                w.flush();

            }
            catch (FileNotFoundException ex){
                ex.printStackTrace();
            }

        }
        catch (IOException ex) {
            System.out.println("Usage: [APPLICATION_NAME] [PATH_TO_LOG_FILE.log] [BEGINNING_DATE (optional)] [ENDING_DATE (optional)]");
            System.out.println("Example: java –jar App ./log.log 2020-01-01T12:00:00 2020-01-01T13:00:00");
        }
    }
}
