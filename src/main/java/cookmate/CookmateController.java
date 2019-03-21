package cookmate;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class CookmateController {

    private static final String startngrok = "https://e421ffe3.ngrok.io/wateron";
    private static final String stopngrok = "https://e421ffe3.ngrok.io/wateron";

    private static final Map<String, Integer> storage = new HashMap<>();
    private static final String KEY = "STATE";
    private static final int COOK_TIME = 10 * 60 * 1000;

    CookmateController() {
        storage.put(KEY, 0);
    }

    @RequestMapping("/cooker/start/{minutes}")
    public String cook(@PathVariable(value = "minutes") String minutes) throws ParseException {
        System.out.println("Got cook request with value:" + minutes);
        if (storage.get(KEY) == 1) {
            return "success";
        }
        Timer timer = new Timer(true);
        TimerTask task = new CookTask();
        long seconds = Long.parseLong(minutes) * 60 * 1000;
        timer.schedule(task, seconds);
        return "success";
    }

    @RequestMapping("/cooker/status")
    public String state() throws ParseException {
        System.out.println("Got status request ");
        return storage.get(KEY) + "";
    }


    @RequestMapping("/cooker/stop")
    public String cookstop() {
        off();
        return "success";
    }

    private static void callCooker(String url) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject(url, String.class);
        } catch (Exception ex) {
            System.out.println("Error in calling url" + url);
        }

    }

    private void on() {
        storage.put(KEY, 1);

        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                off();
            }
        }, COOK_TIME);
    }

    private void off() {
        System.out.println("Turning off at time:" + new Date().toString());
        storage.put(KEY, 0);
    }

    class CookTask extends TimerTask {

        @Override
        public void run() {
            System.out.println("Running task at time:" + new Date().toString());
            //callCooker(startngrok);
            on();
        }
    }


}
