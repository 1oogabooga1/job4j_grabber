package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {
    private final Parse parse;
    private final Store store;
    private final Scheduler scheduler;
    private final int time;

    public Grabber(Parse parse, Store store, Scheduler scheduler, int time) {
        this.parse = parse;
        this.store = store;
        this.scheduler = scheduler;
        this.time = time;
    }

    @Override
    public void init() throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data)
                .build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(time)
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    public static class GrabJob implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            try {
                List<Post> posts = parse.list("https://career.habr.com/vacancies?page=1&q=Java+developer&type=all");
                for (Post post : posts) {
                    store.save(post);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

        public void web(Store store) {
            new Thread(() -> {
                try (ServerSocket server = new ServerSocket(Integer.parseInt(cfg().getProperty("port")))) {
                    while (!server.isClosed()) {
                        Socket socket = server.accept();
                        try (OutputStream out = socket.getOutputStream()) {
                            out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
                            for (Post post : store.getAll()) {
                                out.write(post.toString().getBytes(Charset.forName("Windows-1251")));
                                out.write(System.lineSeparator().getBytes());
                            }
                        } catch (IOException io) {
                            io.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        private static Properties cfg() throws Exception {
            Properties config = new Properties();
            try (InputStream input = Grabber.class.getClassLoader()
                    .getResourceAsStream("app.properties")) {
                config.load(input);
            }
            return config;
        }

        public static void main(String[] args) throws Exception {
            var config = cfg();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            var parse = new HabrCareerParse(new HabrCareerDateTimeParser());
            var store = new PsqlStore(config);
            var time = Integer.parseInt(config.getProperty("time"));
            Grabber grab = new Grabber(parse, store, scheduler, time);
            grab.web(store);
        }
    }
