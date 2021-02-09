package performancelab;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {

//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
//        System.out.println(dateFormat.format( new Date() ) );
//
//
//        if (true){
//            return;
//        }
//
//        try (FileWriter wr=new FileWriter("F:/log.log",true)){
//            int cap=200;
//            int beg=32;
//            for (int i=0;i<15000;i++){
//                Thread.sleep(1);
//                boolean suc=(Math.random()*100<50);
//                boolean suc2=(Math.random()*100<50);
//                String act=suc2?"top up":"scoop";
//                String res="";
//                long toAdd=Math.round(Math.random()*100);
//                if (toAdd==0)
//                    toAdd=10;
//
//                if (suc2 && cap<=beg+toAdd){
//                    res="фейл";
//                }
//                else if (!suc2 && beg<toAdd){
//                    res="фейл";
//                }
//                else{
//                    res="успех";
//                    if (suc2)
//                        beg+=toAdd;
//                    else
//                        beg-=toAdd;
//                }
//
//                wr.write(dateFormat.format( new Date() )+"Z – [username"+Math.abs(Math.round(Math.random()*10)-1)+"] - wanna "+act+" "+toAdd+"l ("+res+")");
//                wr.append('\n');
//                wr.flush();
//            }
//        }
//        catch (IOException | InterruptedException ex){
//
//        }




        boolean hasPer=true;

        String tim1=new String("2020-01-01Т12:51:32").replaceAll("[^0-9]", "");
        String tim2=new String("2021-02-09Т21:20:28").replaceAll("[^0-9]", "");

        if (tim1.length()==14)
            tim1+="000";
        if (tim2.length()==14)
            tim2+="000";



        int dat1=Integer.parseInt(tim1.substring(0,8));
        int time1=Integer.parseInt(tim1.substring(8,17));
        int dat2=Integer.parseInt(tim2.substring(0,8));
        int time2=Integer.parseInt(tim2.substring(8,17));
        System.out.println(dat1);
        System.out.println(time1);
        System.out.println(dat2);
        System.out.println(time2);

        try {
            Stream<String> str=Files.lines(Paths.get("F:/log.log"));
            Stream<String> str2=Files.lines(Paths.get("F:/log.log"));
            Stream<String> str3=Files.lines(Paths.get("F:/log.log"));
//            str.forEach((s) -> System.out.println(s));
            System.out.println();

            int beginningValue=Integer.parseInt(str2.filter(s->s.contains("текущий объем")).findFirst().get().replaceAll("[^0-9]", ""));



            Map<String,String> map=str3.filter(s->s.contains("успех") || s.contains("фейл"))
                    .collect(Collectors.toMap(s->s.substring(0,24).replaceAll("[^0-9]", ""), s->s.split("wanna")[1]));


            String currentKey="";
            String currentVal="";
            System.out.println(beginningValue);
            for (Map.Entry<String,String> es:map.entrySet()){
                currentKey=es.getKey();
                currentVal=es.getValue();
                if (currentVal.contains("успех") && Integer.parseInt(currentKey.substring(0,8))<dat1
                            || ( Integer.parseInt(currentKey.substring(0,8))==dat1
                            && Integer.parseInt(currentKey.substring(8,17))<=time1)){
                    if (currentVal.contains("top")){
                        beginningValue+=Integer.parseInt(currentVal.replaceAll("[^0-9]", ""));
                        System.out.println(("plus")+currentKey);
                    }
                    else if (es.getValue().contains("scoop")){
                        beginningValue-=Integer.parseInt(currentVal.replaceAll("[^0-9]", ""));;
                        System.out.println(("minus")+currentKey);
                    }
                }
            }
            System.out.println(beginningValue);

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
                System.out.println("sd");
                currentVal=es.getValue();
                System.out.println(es.getKey()+" === "+currentVal+" ;;;; "+dat2+" "+time2);
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

            System.out.println("количество попыток налить "+commFillTries);
            System.out.println("процент ошибок при налитии "+Math.round(failFillTries/commFillTries*100));
            System.out.println("налитый объем воды "+filledVol);
            System.out.println("неналитый объем воды "+failFillVol);
            System.out.println();
            System.out.println("количество попыток забрать "+commTakeTries);
            System.out.println("процент ошибок при заборе "+Math.round(failTakeTries/commTakeTries*100));
            System.out.println("забранный объем воды "+TakenVol);
            System.out.println("незабранный объем воды "+failTakeVol);

            endingValue=endingValue+filledVol-TakenVol;
            System.out.println(endingValue);

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
