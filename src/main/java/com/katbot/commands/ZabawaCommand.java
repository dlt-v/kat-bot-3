package com.katbot.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;

public class ZabawaCommand implements Command {
    public final static String[] zabawaArray = {
            "https://cdn.discordapp.com/attachments/470246446306820096/979377452046499910/received_975894643069907.mp4",
            "https://cdn.discordapp.com/attachments/704613785247154256/964866647963865138/1FE2ECB3-99C3-409D-997F-B5341CF88A33.mov",
            "https://cdn.discordapp.com/attachments/872946285865496626/967151695660466206/DJ_1.MP4",
            "https://cdn.discordapp.com/attachments/849750633967910942/967128569711058944/i_smell_zabawa.mp4",
            "https://cdn.discordapp.com/attachments/817846819827744878/966792263088885810/bedzie_sie_dziao.mp4",
            "https://cdn.discordapp.com/attachments/817846819827744878/965713531267846245/Bedzie.mp4",
            "https://cdn.discordapp.com/attachments/817846819827744878/965155393909166150/Norm-is-spitting0.MP4",
            "https://cdn.discordapp.com/attachments/690874101526429736/965017386363273237/20220412_480p2.mp4",
            "https://cdn.discordapp.com/attachments/760910999837474898/965007209610215504/zabawaaaaaaaaa.mp4",
            "https://cdn.discordapp.com/attachments/760910999837474898/964846435533209630/20220416_360p.mp4",
            "https://cdn.discordapp.com/attachments/546722708805648384/962811840289710161/redditsave.com_bedzie_zabawa-pux8vpnh3ks81.mp4",
            "https://cdn.discordapp.com/attachments/909923700302819379/964087273442398208/km_20220413_480p1.mp4",
            "https://cdn.discordapp.com/attachments/760910999837474898/963851069455233064/e.mp4",
            "https://cdn.discordapp.com/attachments/715620255703105576/963489550242553936/InShot_20220412_185525768.mp4",
            "https://cdn.discordapp.com/attachments/920779450994548757/963204627820597258/20220412_480p.mp4",
            "https://cdn.discordapp.com/attachments/760910999837474898/963186341099745320/YouCut_20220411_231736298.mp4",
            "https://cdn.discordapp.com/attachments/760910999837474898/963185480676016208/YouCut_20220411_231450624.mp4",
            "https://cdn.discordapp.com/attachments/760910999837474898/963183807278751814/YouCut_20220411_230821803.mp4",
            "https://cdn.discordapp.com/attachments/760910999837474898/963182654344286228/YouCut_20220411_230342949.mp4",
            "https://cdn.discordapp.com/attachments/760910999837474898/963182161295458304/YouCut_20220411_230145999.mp4",
            "https://cdn.discordapp.com/attachments/760910999837474898/963164896932032542/YouCut_20220411_215313008.mp4",
            "https://cdn.discordapp.com/attachments/868560618079285368/963164694917550170/dasdad.mp4",
            "https://cdn.discordapp.com/attachments/760910999837474898/963125473410420786/YouCut_20220411_154612927.mp4",
            "https://cdn.discordapp.com/attachments/584995825525194752/962814568663494686/redditsave.com_biesiada_rodzinna_o_3_nad_ranem-g6nsl5fy04s81.mp4",
            "https://cdn.discordapp.com/attachments/584995825525194752/962814551001301072/redditsave.com_bdzie_gono-htphe41zz3s81.mp4",
            "https://cdn.discordapp.com/attachments/584995825525194752/962814475931639908/redditsave.com_bdzie_robienie_twojej_mamy-juf2btdf6js81.mp4",
            "https://cdn.discordapp.com/attachments/924419359831633980/962602255377375292/BEDZIE.mp4",
            "https://cdn.discordapp.com/attachments/760910999837474898/962694434837921902/redditsave.com_bdzie_si_dziao-6j51uxd24js81-240.mp4",
            "https://cdn.discordapp.com/attachments/555499644533080064/962668620100542484/received_2701837419959750.mp4",
            "https://cdn.discordapp.com/attachments/760910999837474898/962825328038920315/heylois.mp4",
            "https://cdn.discordapp.com/attachments/760910999837474898/963080010346930226/redditsave.com_co_bedzie-ykxiexziocs81.mp4",
            "https://cdn.discordapp.com/attachments/261646540936839168/962623911395217478/redditsave.com_bdzie-9rjyo04xlhs81-480.mp4",
            "https://cdn.discordapp.com/attachments/872946285865496626/962827898161610782/received_750386329678783.mp4",
            "https://cdn.discordapp.com/attachments/328501947042889728/962041759813226556/redditsave.com_tak_zaczyna_si_zabawa-ldvhnwc88yr81.mp4",
            "https://cdn.discordapp.com/attachments/884911547615432734/962737228726038568/There_Will_Be_Zabawa.mp4",
    };
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        Random random = new Random();
        event.getChannel().sendMessage(zabawaArray[random.nextInt(0, zabawaArray.length)]).queue();
    }
}
